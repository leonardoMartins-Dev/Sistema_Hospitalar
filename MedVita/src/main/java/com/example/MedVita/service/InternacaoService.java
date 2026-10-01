package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE INTERNAÇÕES
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * USA: InternacaoRepository, QuartoRepository, PacienteService,
 *      ProfissionalSaudeService
 *
 * MÉTODOS
 *   internar(pacienteId, profissionalId, quartoId, dataEntrada,
 *            dataPrevistaAlta, observacoes)
 *       1. Paciente, profissional e quarto existem.
 *       2. O paciente já está internado? -> RegraNegocioException.
 *       3. RN4 e RN5: o quarto tem vaga (ocupacaoAtual < capacidadeMaxima)?
 *          Se não -> RegraNegocioException("Quarto ocupado").
 *       4. Soma 1 na ocupação do quarto e salva a internação.
 *       Com @Transactional, os passos 3 e 4 acontecem juntos ou nada acontece.
 *   darAlta(id, dataEfetivaAlta, observacoes)
 *       Preenche a data efetiva de alta e tira 1 da ocupação do quarto
 *       (também @Transactional).
 *   listarInternados()             -> tela medico/internacoes
 *   listarDoPaciente(pacienteId)   -> tela paciente/internacoes
 */
