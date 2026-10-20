package com.example.MedVita.models;

/*
 * =========================================================
 * PROCEDIMENTO CIRÚRGICO  (subclasse de Atendimento)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
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
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("CIRURGIA")
 *   public class ProcedimentoCirurgico extends Atendimento
 *
 * ATRIBUTOS PRÓPRIOS
 *   String descricaoProcedimento       coluna descricao_procedimento, obrigatório
 *   TipoCirurgia tipoCirurgia          @Enumerated(STRING), coluna tipo_cirurgia, obrigatório
 *   String observacoesPreOperatorias   coluna obs_pre_operatorias
 *   String observacoesPosOperatorias   coluna obs_pos_operatorias (preenchida ao concluir)
 *   LocalDateTime inicioEfetivo        coluna inicio_efetivo (preenchido ao iniciar)
 *   LocalDateTime fimEfetivo           coluna fim_efetivo (preenchido ao concluir)
 *   StatusProcedimento status          @Enumerated(STRING) @Column(name = "status")
 *                                      começa em AGENDADO
 *   data/horario herdados de Atendimento = os PREVISTOS.
 *   inicioEfetivo/fimEfetivo = os efetivos ("quando aplicável").
 *
 * CONSTRUTORES
 *   protected ProcedimentoCirurgico()   (JPA)
 *   public ProcedimentoCirurgico(paciente, cirurgiao, dataPrevista, horarioPrevisto,
 *                                descricaoProcedimento, tipoCirurgia, observacoesPreOperatorias)
 *
 * MÉTODOS
 *   getTipo() -> TipoAtendimento.CIRURGIA
 *
 *   validarRegrasDaModalidade()
 *       - descrição e tipo de cirurgia obrigatórios
 *       - verificarDataFutura() (data/hora prevista)
 *       - verificarProfissional(): só o Cirurgiao aceita
 *
 *   iniciar(), concluir() e cancelar() são chamados pelas rotas genéricas do
 *   ConsultaController (PUT /consultas/{id}/iniciar etc.), que só conhecem a
 *   classe Atendimento. Nada aqui precisa da parte do João Pedro além da
 *   classe Atendimento.
 *
 *   iniciar()               AGENDADO -> EM_ANDAMENTO; inicioEfetivo = LocalDateTime.now()
 *   concluir(String obsPos) EM_ANDAMENTO -> CONCLUIDO; fimEfetivo = now;
 *                           observacoesPosOperatorias = obsPos
 *                           Se ainda estiver AGENDADO: RegraNegocioException(
 *                           "Não é possível concluir um procedimento que não foi
 *                           iniciado")  <- regra do enunciado
 *   cancelar()              AGENDADO -> CANCELADO. Em andamento não cancela.
 *   ocupaAgenda()           -> status != CANCELADO
 *   isConcluido()           -> status == CONCLUIDO
 *
 *   As mudanças passam por um mudarStatus privado com StatusProcedimento.podeMudarPara.
 *
 * ROTAS (EmergenciaCirurgiaController, também desta parte)
 *   POST /consultas/cirurgias                   agendar (body: CirurgiaRequest)
 *   GET  /consultas/procedimentos?cirurgiaoId=9 procedimentos de um cirurgião
 */
