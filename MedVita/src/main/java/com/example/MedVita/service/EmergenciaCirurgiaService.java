package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE EMERGÊNCIAS E PROCEDIMENTOS CIRÚRGICOS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE É
 * O que é ESPECÍFICO de emergência e cirurgia: criar, assumir a emergência,
 * fila por prioridade e lista de procedimentos do cirurgião.
 * Iniciar, concluir e cancelar NÃO ficam aqui: as rotas genéricas do
 * ConsultaController (João Pedro) já fazem isso para qualquer atendimento,
 * chamando os métodos que AtendimentoEmergencia e ProcedimentoCirurgico
 * sobrescrevem. Assim este arquivo e o ConsultaService não se misturam.
 *
 * USA (só repositórios)
 *   AtendimentoEmergenciaRepository, ProcedimentoCirurgicoRepository  (desta parte)
 *   PacienteRepository           (já existe) -> findById
 *   ProfissionalSaudeRepository  (Leonardo Martins) -> findById
 *
 * MÉTODOS
 *   @Transactional
 *   AtendimentoEmergencia registrarEmergencia(EmergenciaRequest req)
 *     1. paciente = pacienteRepository.findById(...) -> RecursoNaoEncontradoException
 *     2. e = new AtendimentoEmergencia(paciente, req.motivo(), req.prioridade())
 *     3. e.validar(); return save(e)
 *
 *   @Transactional
 *   AtendimentoEmergencia assumirEmergencia(Long id, Long profissionalId)
 *     1. e = emergenciaRepository.findById(id) -> RecursoNaoEncontradoException
 *     2. medico = profissionalSaudeRepository.findById(profissionalId) -> idem
 *     3. e.atribuirProfissional(medico)   (cirurgião é recusado pelo podeRealizar)
 *     4. e.iniciar(); save
 *
 *   List<AtendimentoEmergencia> filaEmergencia(StatusEmergencia status)
 *     -> status null = AGUARDANDO_AVALIACAO; emergenciaRepository.findByStatus(status)
 *        ordenado com CRITICA primeiro e depois por chegada (Comparator do enum
 *        Prioridade)
 *
 *   @Transactional
 *   ProcedimentoCirurgico agendarCirurgia(CirurgiaRequest req)
 *     1. paciente e cirurgião pelos repositórios (404 se não existir)
 *     2. p = new ProcedimentoCirurgico(paciente, cirurgiao, data, horario,
 *                                      descricao, tipoCirurgia, obsPre)
 *     3. p.validar()   (só aceita Cirurgiao, data futura, campos obrigatórios)
 *     4. RN3: procedimentoRepository.findByProfissionalIdAndDataAndHorario(...)
 *            .stream().anyMatch(ProcedimentoCirurgico::ocupaAgenda)
 *        -> RegraNegocioException("O cirurgião já tem procedimento neste horário")
 *     5. save
 *
 *   List<ProcedimentoCirurgico> listarProcedimentosDoCirurgiao(Long cirurgiaoId)
 *     -> confere que o profissional existe e que getCategoria() == CIRURGIAO
 *        (senão RegraNegocioException) e devolve
 *        procedimentoRepository.findByProfissionalIdOrderByDataDescHorarioDesc(id)
 */
