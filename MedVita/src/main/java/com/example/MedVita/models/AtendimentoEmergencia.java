package com.example.MedVita.models;

/*
 * =========================================================
 * ATENDIMENTO DE EMERGÊNCIA  (subclasse de Atendimento)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Situações que exigem avaliação imediata.
 *   - Registra a prioridade, com categorias fixas: baixa, moderada, alta e
 *     crítica (enum Prioridade).
 *   - Permite identificar os que aguardam avaliação e os já iniciados ou concluídos.
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("EMERGENCIA")
 *   public class AtendimentoEmergencia extends Atendimento
 *
 * ATRIBUTOS PRÓPRIOS
 *   Prioridade prioridade      @Enumerated(STRING), coluna "prioridade", obrigatório
 *   StatusEmergencia status    @Enumerated(STRING) @Column(name = "status")
 *                              começa em AGUARDANDO_AVALIACAO
 *
 * COMO FUNCIONA (rotas do EmergenciaCirurgiaController, também desta parte)
 *   1. O paciente chega: POST /consultas/emergencias com paciente, motivo (a
 *      queixa) e prioridade. data/horario = agora (o construtor preenche).
 *      Ainda SEM médico: profissional fica null.
 *   2. Um médico assume: PUT /consultas/emergencias/{id}/assumir?profissionalId=3
 *      -> atribuirProfissional(medico) e depois iniciar().
 *   3. O médico conclui: PUT /consultas/{id}/concluir. Essa rota é a genérica
 *      do ConsultaController (João Pedro) e funciona sem ele conhecer esta
 *      classe, porque chama atendimento.concluir() (polimorfismo).
 *
 * CONSTRUTORES
 *   protected AtendimentoEmergencia()   (JPA)
 *   public AtendimentoEmergencia(paciente, motivo, prioridade)
 *       -> super(paciente, null, LocalDate.now(),
 *                LocalTime.now().withSecond(0).withNano(0), motivo)
 *
 * MÉTODOS
 *   getTipo() -> TipoAtendimento.EMERGENCIA
 *
 *   validarRegrasDaModalidade()
 *       - prioridade obrigatória
 *       - motivo obrigatório
 *       - se já tiver profissional: verificarProfissional()
 *       (não chama verificarDataFutura: a chegada é agora)
 *
 *   atribuirProfissional(ProfissionalSaude medico)
 *       - só enquanto AGUARDANDO_AVALIACAO; senão RegraNegocioException
 *       - medico.verificarSePodeRealizar(EMERGENCIA, null): clínico e
 *         especialista aceitam, cirurgião recusa (decisão do grupo)
 *       - grava em profissional (setter protected da superclasse)
 *
 *   iniciar()
 *       - sem profissional: RegraNegocioException("Defina o médico antes de iniciar")
 *       - AGUARDANDO_AVALIACAO -> EM_ATENDIMENTO
 *   concluir(String observacoes)
 *       - EM_ATENDIMENTO -> CONCLUIDO; observações obrigatórias
 *   cancelar()
 *       - SEMPRE lança RegraNegocioException("Atendimento de emergência não pode
 *         ser cancelado"). Bom exemplo de polimorfismo para a apresentação: o
 *         mesmo método da Consulta, com outro comportamento.
 *   ocupaAgenda()  -> false (emergência não é agendada, então não prende horário)
 *   isConcluido()  -> status == CONCLUIDO
 *   boolean aguardandoAvaliacao() -> status == AGUARDANDO_AVALIACAO
 *
 *   As mudanças passam por um mudarStatus privado, igual ao da Consulta,
 *   usando StatusEmergencia.podeMudarPara.
 *
 * FILA DE EMERGÊNCIA
 *   GET /consultas/emergencias?status=AGUARDANDO_AVALIACAO
 *   Ordenada por prioridade (CRITICA primeiro) e depois por chegada (data, horario).
 *   Ver EmergenciaCirurgiaService.filaEmergencia(...).
 */
