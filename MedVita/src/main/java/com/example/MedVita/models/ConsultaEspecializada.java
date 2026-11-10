package com.example.MedVita.models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/*
 * =========================================================
 * CONSULTA ESPECIALIZADA  (subclasse de Consulta)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Atendimento ligado a uma especialidade médica.
 *   - O profissional tem que ter área de atuação compatível com o atendimento.
 *   - Registra a especialidade e as observações do atendimento.
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("ESPECIALIZADA")
 *   public class ConsultaEspecializada extends Consulta
 *
 * ATRIBUTO PRÓPRIO
 *   Especialidade especialidade   @Enumerated(STRING), coluna "especialidade", obrigatório
 *
 * CONSTRUTORES
 *   protected ConsultaEspecializada()   (JPA)
 *   public ConsultaEspecializada(paciente, profissional, data, horario, motivo, especialidade)
 *
 * MÉTODOS
 *   getTipo() -> TipoAtendimento.ESPECIALIZADA
 *
 *   getEspecialidadeExigida() -> especialidade
 *       (sobrescreve o da classe Atendimento, que devolve null)
 *
 *   validarRegrasDaModalidade()
 *       - especialidade obrigatória
 *       - motivo obrigatório
 *       - verificarDataFutura()
 *       - verificarProfissional(): chama
 *         profissional.verificarSePodeRealizar(ESPECIALIZADA, especialidade).
 *         O MedicoEspecialista só aceita se a especialidade dele for a mesma;
 *         clínico e cirurgião recusam. Ex.: ortopedista numa consulta de
 *         cardiologia -> RegraNegocioException.
 *
 *   copiar() -> new ConsultaEspecializada(getPaciente(), getProfissional(), null, null,
 *                                         getMotivo(), especialidade)
 *
 * TELA: em paciente/agendar-consulta, escolher uma especialidade (que não seja
 * "Clínica geral") cria uma consulta deste tipo.
 */

@Entity
@DiscriminatorValue("ESPECIALIZADA")
public class ConsultaEspecializada extends Consulta {

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidade")
    private Especialidade especialidade;

    protected ConsultaEspecializada() {
    }

    public ConsultaEspecializada(Paciente paciente, ProfissionalSaude profissional, LocalDate data,
            LocalTime horario, String motivo, Especialidade especialidade) {
        super(paciente, profissional, data, horario, motivo);
        this.especialidade = especialidade;
    }

    @Override
    public TipoAtendimento getTipo() {
        return TipoAtendimento.ESPECIALIZADA;
    }

    @Override
    public Especialidade getEspecialidadeExigida() {
        return especialidade;
    }

    @Override
    protected void validarRegrasDaModalidade() {
        if (especialidade == null) {
            throw new RegraNegocioException("Informe a especialidade da consulta");
        }
        verificarMotivo();
        verificarDataFutura();
        verificarProfissional();
    }

    @Override
    protected Consulta copiar() {
        return new ConsultaEspecializada(getPaciente(), getProfissional(), null, null,
                getMotivo(), especialidade);
    }

    public Especialidade getEspecialidade() { return especialidade; }
}
