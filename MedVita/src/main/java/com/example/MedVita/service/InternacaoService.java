package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE INTERNAÇÕES
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * USA: InternacaoRepository, QuartoRepository, PacienteService,
 *      ProfissionalSaudeService
 *
 * MÉTODOS
 *   @Transactional
 *   Internacao internar(InternacaoRequest req)
 *     1. paciente = pacienteService.buscarPorId(...)
 *        profissional = profissionalSaudeService.buscarPorId(...)
 *        quarto = quartoRepository.buscarParaAtualizar(quartoId)   (trava a linha)
 *                 -> RecursoNaoEncontradoException se não existir
 *     2. internacaoRepository.existsByPacienteIdAndStatus(pacienteId, ATIVA)
 *        -> RegraNegocioException("Este paciente já está internado")
 *     3. internacao = new Internacao(...); internacao.validar()
 *     4. quarto.ocupar()   lança RegraNegocioException se estiver lotado (RN4 e RN5)
 *     5. save da internação. O quarto é salvo sozinho no fim da transação
 *        (o Hibernate percebe que a ocupação mudou), mas pode chamar
 *        quartoRepository.save(quarto) para deixar claro.
 *     Com @Transactional, os passos 4 e 5 acontecem juntos ou nada acontece.
 *
 *   @Transactional
 *   Internacao darAlta(Long id, LocalDate dataEfetivaAlta, String observacoes)
 *     1. internacao = buscarPorId(id)
 *     2. internacao.darAlta(dataEfetivaAlta, observacoes)   (o model confere as regras)
 *     3. quartoRepository.buscarParaAtualizar(internacao.getQuarto().getId())
 *            .liberar()
 *     4. save
 *
 *   Internacao buscarPorId(Long id)                -> RecursoNaoEncontradoException
 *   List<Internacao> listar(StatusInternacao status)
 *       -> status null: todas; ATIVA: quem está internado agora (medico/internacoes)
 *   List<Internacao> listarDoPaciente(Long pacienteId)   -> paciente/internacoes e histórico
 */
