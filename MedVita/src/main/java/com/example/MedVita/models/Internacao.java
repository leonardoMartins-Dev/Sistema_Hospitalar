package com.example.MedVita.models;

import java.time.LocalDate;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.StatusInternacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * =========================================================
 * INTERNAÇÃO
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * O período em que um paciente fica internado num quarto: paciente,
 * profissional responsável, quarto, data de entrada, data prevista de alta,
 * data efetiva de alta e observações.
 *
 * O QUE MUDOU NA SPRINT 2
 * O enunciado fala nas "situações pelas quais uma consulta ou internação pode
 * passar". A internação ganhou o enum StatusInternacao (ATIVA | ALTA) no
 * lugar de "está internado se dataEfetivaAlta for null".
 *
 * TABELA internacoes
 *   id                  bigint  gerado pelo banco
 *   paciente_id         bigint  obrigatório -> pacientes(id)               (RN4)
 *   profissional_id     bigint  obrigatório -> profissionais_saude(id)
 *   quarto_id           bigint  obrigatório -> quartos(id)                 (RN4)
 *   data_entrada        date    obrigatório
 *   data_prevista_alta  date
 *   data_efetiva_alta   date    preenchida na alta
 *   observacoes         text
 *   status              text    NOVO: ATIVA | ALTA (começa ATIVA)
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 *   @Entity @Table(name = "internacoes")
 *
 * ATRIBUTOS
 *   Long id
 *   Paciente paciente                @ManyToOne(optional = false)
 *   ProfissionalSaude profissional   @ManyToOne(optional = false)
 *        qualquer categoria pode ser responsável (o enunciado não restringe)
 *   Quarto quarto                    @ManyToOne(optional = false)
 *   LocalDate dataEntrada, dataPrevistaAlta, dataEfetivaAlta
 *   String observacoes
 *   StatusInternacao status          @Enumerated(STRING), começa ATIVA
 *
 * CONSTRUTORES
 *   protected Internacao()   (JPA)
 *   public Internacao(paciente, profissional, quarto, dataEntrada, dataPrevistaAlta, observacoes)
 *
 * MÉTODOS
 *   validar()
 *       - dataEntrada obrigatória
 *       - dataPrevistaAlta (se tiver) não pode ser antes da dataEntrada
 *   boolean estaInternado()  -> status == ATIVA
 *   darAlta(LocalDate dataEfetivaAlta, String observacoes)
 *       - só se ATIVA; senão RegraNegocioException("Esta internação já foi encerrada")
 *       - dataEfetivaAlta não pode ser antes da dataEntrada
 *       - grava a data, acrescenta as observações e muda o status para ALTA
 *       - quem libera o leito (quarto.liberar()) é o InternacaoService, na
 *         mesma transação
 *
 * TELAS: medico/internacoes, paciente/internacoes
 */

@Entity
@Table(name = "internacoes")
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    // qualquer categoria pode ser responsável (o enunciado não restringe)
    @ManyToOne(optional = false)
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quarto_id")
    private Quarto quarto;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "data_prevista_alta")
    private LocalDate dataPrevistaAlta;

    @Column(name = "data_efetiva_alta")
    private LocalDate dataEfetivaAlta;

    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusInternacao status;

    protected Internacao() {
    }

    public Internacao(Paciente paciente, ProfissionalSaude profissional, Quarto quarto,
            LocalDate dataEntrada, LocalDate dataPrevistaAlta, String observacoes) {
        this.paciente = paciente;
        this.profissional = profissional;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.dataPrevistaAlta = dataPrevistaAlta;
        this.observacoes = observacoes;
        this.status = StatusInternacao.ATIVA;
    }

    // REGRAS DA INTERNAÇÃO NOVA (chamado pelo InternacaoService antes de salvar)
    public void validar() {
        if (paciente == null) {
            throw new RegraNegocioException("Informe o paciente");
        }
        if (profissional == null) {
            throw new RegraNegocioException("Informe o profissional responsável");
        }
        if (quarto == null) {
            throw new RegraNegocioException("Informe o quarto");
        }
        if (dataEntrada == null) {
            throw new RegraNegocioException("Informe a data de entrada");
        }
        if (dataPrevistaAlta != null && dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A previsão de alta não pode ser antes da data de entrada");
        }
    }

    public boolean estaInternado() {
        return status == StatusInternacao.ATIVA;
    }

    // ATIVA -> ALTA. Quem libera o leito é o InternacaoService, na mesma transação.
    public void darAlta(LocalDate dataEfetivaAlta, String observacoesAlta) {
        if (!estaInternado()) {
            throw new RegraNegocioException("Esta internação já foi encerrada");
        }
        if (dataEfetivaAlta == null) {
            throw new RegraNegocioException("Informe a data da alta");
        }
        if (dataEfetivaAlta.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A data da alta não pode ser antes da data de entrada");
        }
        this.dataEfetivaAlta = dataEfetivaAlta;
        if (observacoesAlta != null && !observacoesAlta.isBlank()) {
            // acrescenta, sem apagar o que foi registrado na entrada (RN6)
            this.observacoes = (observacoes == null || observacoes.isBlank())
                    ? observacoesAlta.trim()
                    : observacoes + "\nAlta: " + observacoesAlta.trim();
        }
        this.status = StatusInternacao.ALTA;
    }

    // GETTERS (sem setters: os dados só mudam pelo darAlta)
    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public ProfissionalSaude getProfissional() { return profissional; }
    public Quarto getQuarto() { return quarto; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public LocalDate getDataPrevistaAlta() { return dataPrevistaAlta; }
    public LocalDate getDataEfetivaAlta() { return dataEfetivaAlta; }
    public String getObservacoes() { return observacoes; }
    public StatusInternacao getStatus() { return status; }
}
