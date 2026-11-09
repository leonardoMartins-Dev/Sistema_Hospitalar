package com.example.MedVita.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.StatusConsulta;
import com.example.MedVita.models.enums.TipoAtendimento;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/*
 * =========================================================
 * CONSULTA DE ROTINA  (subclasse de Consulta)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Avaliação geral do paciente.
 *   - Tem data, horário, paciente, médico responsável e motivo.
 *   - Permite registrar observações depois da consulta.
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("ROTINA")
 *   public class ConsultaRotina extends Consulta
 *
 * ATRIBUTO PRÓPRIO
 *   Especialidade encaminhamento   @Enumerated(STRING), coluna "encaminhamento", pode ser null
 *        Preenchido quando o clínico encaminha o paciente para um especialista.
 *
 * CONSTRUTORES
 *   protected ConsultaRotina()   (JPA)
 *   public ConsultaRotina(paciente, profissional, data, horario, motivo)
 *
 * MÉTODOS
 *   getTipo() -> TipoAtendimento.ROTINA
 *
 *   validarRegrasDaModalidade()
 *       - motivo obrigatório
 *       - verificarDataFutura()
 *       - verificarProfissional(): só o MedicoClinicoGeral aceita
 *         (podeRealizar(ROTINA, null) dá true só para ele)
 *
 *   registrarEncaminhamento(Especialidade especialidade, ProfissionalSaude solicitante)
 *       "O clínico geral pode encaminhar pacientes para atendimento especializado."
 *       - o solicitante tem que ser o médico desta consulta (compare os ids);
 *         senão RegraNegocioException("Só o médico da consulta pode encaminhar")
 *       - !solicitante.podeEncaminhar() -> RegraNegocioException (polimorfismo:
 *         só o clínico geral devolve true)
 *       - só com status EM_ATENDIMENTO ou CONCLUIDA (o encaminhamento sai de uma
 *         consulta que aconteceu); senão RegraNegocioException
 *       - especialidade obrigatória; grava em "encaminhamento"
 *       Quem chama: ConsultaService.encaminhar(...)
 *
 *   copiar() -> new ConsultaRotina(getPaciente(), getProfissional(), null, null, getMotivo())
 *       (usado no reagendamento; o encaminhamento não é copiado)
 *
 * TELA: em paciente/agendar-consulta, escolher "Clínica geral" cria uma
 * consulta deste tipo.
 */

@Entity
@DiscriminatorValue("ROTINA")
public class ConsultaRotina extends Consulta {

    @Enumerated(EnumType.STRING)
    @Column(name = "encaminhamento")
    private Especialidade encaminhamento;

    protected ConsultaRotina() {
    }

    public ConsultaRotina(Paciente paciente, ProfissionalSaude profissional, LocalDate data,
            LocalTime horario, String motivo) {
        super(paciente, profissional, data, horario, motivo);
    }

    @Override
    public TipoAtendimento getTipo() {
        return TipoAtendimento.ROTINA;
    }

    @Override
    protected void validarRegrasDaModalidade() {
        verificarMotivo();
        verificarDataFutura();
        verificarProfissional();
    }

    public void registrarEncaminhamento(Especialidade especialidade, ProfissionalSaude solicitante) {
        if (solicitante == null || getProfissional() == null
                || !Objects.equals(solicitante.getId(), getProfissional().getId())) {
            throw new RegraNegocioException("Só o médico da consulta pode encaminhar");
        }
        if (!solicitante.podeEncaminhar()) {
            throw new RegraNegocioException("Só o clínico geral pode encaminhar para um especialista");
        }
        if (getStatus() != StatusConsulta.EM_ATENDIMENTO && getStatus() != StatusConsulta.CONCLUIDA) {
            throw new RegraNegocioException("Só é possível encaminhar durante ou depois da consulta");
        }
        if (especialidade == null) {
            throw new RegraNegocioException("Informe a especialidade do encaminhamento");
        }
        this.encaminhamento = especialidade;
    }

    @Override
    protected Consulta copiar() {
        return new ConsultaRotina(getPaciente(), getProfissional(), null, null, getMotivo());
    }

    public Especialidade getEncaminhamento() { return encaminhamento; }
}
