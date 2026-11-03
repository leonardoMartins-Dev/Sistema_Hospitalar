package com.example.MedVita.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.StatusProcedimento;
import com.example.MedVita.models.enums.TipoAtendimento;
import com.example.MedVita.models.enums.TipoCirurgia;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/*
 * =========================================================
 * PROCEDIMENTO CIRÚRGICO  (subclasse de Atendimento)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Registro de procedimentos cirúrgicos realizados ou agendados.
 *   - Tem descrição, tipo de cirurgia e observações pré e pós-operatórias.
 *   - Associado a um paciente e a um cirurgião responsável.
 *   - Data e horário previstos, e data e horário efetivos quando houver.
 *   - Situações: agendado, em andamento, concluído ou cancelado.
 *   - Não pode ser registrado como concluído se não tiver sido iniciado.
 *
 * ATRIBUTOS PRÓPRIOS
 *   descricaoProcedimento       coluna descricao_procedimento, obrigatório
 *   tipoCirurgia                coluna tipo_cirurgia, obrigatório
 *   observacoesPreOperatorias   coluna obs_pre_operatorias
 *   observacoesPosOperatorias   coluna obs_pos_operatorias (preenchida ao concluir)
 *   inicioEfetivo               coluna inicio_efetivo (preenchido ao iniciar)
 *   fimEfetivo                  coluna fim_efetivo (preenchido ao concluir)
 *   status                      coluna status, começa em AGENDADO
 *   data/horario herdados de Atendimento = os PREVISTOS.
 *
 * iniciar(), concluir() e cancelar() são chamados pelas rotas genéricas do
 * ConsultaController (PUT /consultas/{id}/iniciar etc.), que só conhecem a
 * classe Atendimento (polimorfismo).
 *
 * ROTAS (EmergenciaCirurgiaController, também desta parte)
 *   POST /consultas/cirurgias                   agendar (body: CirurgiaRequest)
 *   GET  /consultas/procedimentos?cirurgiaoId=9 procedimentos de um cirurgião
 */
@Entity
@DiscriminatorValue("CIRURGIA")
public class ProcedimentoCirurgico extends Atendimento {

    @Column(name = "descricao_procedimento")
    private String descricaoProcedimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cirurgia")
    private TipoCirurgia tipoCirurgia;

    @Column(name = "obs_pre_operatorias")
    private String observacoesPreOperatorias;

    @Column(name = "obs_pos_operatorias")
    private String observacoesPosOperatorias;

    @Column(name = "inicio_efetivo")
    private LocalDateTime inicioEfetivo;

    @Column(name = "fim_efetivo")
    private LocalDateTime fimEfetivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusProcedimento status;

    //CONSTRUTORES

    protected ProcedimentoCirurgico() {
        // obrigatório para o JPA
    }

    public ProcedimentoCirurgico(Paciente paciente, ProfissionalSaude cirurgiao,
            LocalDate dataPrevista, LocalTime horarioPrevisto,
            String descricaoProcedimento, TipoCirurgia tipoCirurgia,
            String observacoesPreOperatorias) {
        // a descrição do procedimento também vai como "motivo" do atendimento
        super(paciente, cirurgiao, dataPrevista, horarioPrevisto, descricaoProcedimento);
        this.descricaoProcedimento = descricaoProcedimento;
        this.tipoCirurgia = tipoCirurgia;
        this.observacoesPreOperatorias = observacoesPreOperatorias;
        this.status = StatusProcedimento.AGENDADO;
    }

    //TIPO DO ATENDIMENTO (sai no JSON como "tipo")
    @Override
    public TipoAtendimento getTipo() {
        return TipoAtendimento.CIRURGIA;
    }

    //REGRAS PRÓPRIAS DA CIRURGIA (chamado pelo validar() da classe mãe)
    @Override
    protected void validarRegrasDaModalidade() {
        if (descricaoProcedimento == null || descricaoProcedimento.isBlank()) {
            throw new RegraNegocioException("Informe a descrição do procedimento");
        }
        if (tipoCirurgia == null) {
            throw new RegraNegocioException("Informe o tipo de cirurgia");
        }
        verificarDataFutura();
        verificarProfissional(); // só o Cirurgiao aceita
    }

    //SITUAÇÕES

    @Override
    public void iniciar() {
        mudarStatus(StatusProcedimento.EM_ANDAMENTO);
        inicioEfetivo = LocalDateTime.now();
    }

    @Override
    public void concluir(String observacoesPos) {
        // regra do enunciado: não conclui o que não foi iniciado
        if (status == StatusProcedimento.AGENDADO) {
            throw new RegraNegocioException(
                    "Não é possível concluir um procedimento que não foi iniciado");
        }
        mudarStatus(StatusProcedimento.CONCLUIDO);
        fimEfetivo = LocalDateTime.now();
        observacoesPosOperatorias = observacoesPos;
    }

    @Override
    public void cancelar() {
        mudarStatus(StatusProcedimento.CANCELADO); // só AGENDADO pode cancelar
    }

    @Override
    public boolean ocupaAgenda() {
        return status != StatusProcedimento.CANCELADO;
    }

    @Override
    public boolean isConcluido() {
        return status == StatusProcedimento.CONCLUIDO;
    }

    //QUEM SABE QUAIS MUDANÇAS VALEM É O ENUM
    private void mudarStatus(StatusProcedimento novo) {
        if (!status.podeMudarPara(novo)) {
            throw new RegraNegocioException("Um procedimento " + status.getDescricao()
                    + " não pode passar para " + novo.getDescricao());
        }
        status = novo;
    }

    //GETTERS

    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public TipoCirurgia getTipoCirurgia() { return tipoCirurgia; }
    public String getObservacoesPreOperatorias() { return observacoesPreOperatorias; }
    public String getObservacoesPosOperatorias() { return observacoesPosOperatorias; }
    public LocalDateTime getInicioEfetivo() { return inicioEfetivo; }
    public LocalDateTime getFimEfetivo() { return fimEfetivo; }
    public StatusProcedimento getStatus() { return status; }
}
