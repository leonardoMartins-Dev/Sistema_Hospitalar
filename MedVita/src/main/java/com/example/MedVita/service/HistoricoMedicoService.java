package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DO HISTÓRICO MÉDICO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * USA: PacienteService, AtendimentoRepository, InternacaoRepository
 *
 * MÉTODOS
 *   HistoricoMedico doPaciente(Long pacienteId)
 *     1. paciente = pacienteService.buscarPorId(pacienteId)
 *        (RecursoNaoEncontradoException se não existir)
 *     2. atendimentos = atendimentoRepository.findByPacienteIdOrderByDataDescHorarioDesc(id)
 *            .stream().filter(Atendimento::isConcluido).toList()
 *        isConcluido() é polimórfico: cada modalidade sabe quando está
 *        concluída (CONCLUIDA na consulta, CONCLUIDO na emergência e na cirurgia).
 *     3. internacoes = internacaoRepository.findByPacienteIdOrderByDataEntradaDesc(id)
 *     4. return new HistoricoMedico(paciente, atendimentos, internacoes)
 *
 *   HistoricoMedico doPacienteLogado(String email)
 *     -> pacienteService.buscarPorEmail(email) e chama doPaciente(id)
 *
 * Só leitura: o histórico nunca é editado nem apagado (RN6).
 * Quem pode ver: o próprio paciente, os médicos e o admin.
 */
