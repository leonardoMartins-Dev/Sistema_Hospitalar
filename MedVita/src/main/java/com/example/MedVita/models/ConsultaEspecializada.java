package com.example.MedVita.models;

/*
 * =========================================================
 * CONSULTA ESPECIALIZADA  (subclasse de Consulta)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
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
