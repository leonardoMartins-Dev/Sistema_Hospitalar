package com.example.MedVita.models;

/*
 * =========================================================
 * CONSULTA DE ROTINA  (subclasse de Consulta)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
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
