package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE INTERNAÇÕES  (/internacoes)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/internacoes"): devolve JSON.
 *
 * ROTAS
 *   POST /internacoes                body: InternacaoRequest -> 201   (MEDICO)
 *                                    (botão "Registrar Internação" de medico/internacoes)
 *   GET  /internacoes?status=ATIVA   quem está internado agora (MEDICO, ADMIN)
 *                                    sem status: todas
 *   GET  /internacoes/minhas         do paciente logado (tela paciente/internacoes)
 *   GET  /internacoes/{id}           uma internação
 *   PUT  /internacoes/{id}/alta      body: {"dataEfetivaAlta": "2026-11-15",
 *                                           "observacoes": "..."}   (MEDICO)
 *                                    record AltaRequest dentro do controller
 *
 * QUEM PODE: internar e dar alta, só MEDICO (@PreAuthorize).
 *
 * ERROS
 *   quarto lotado, paciente já internado, datas inválidas -> 400
 *   paciente, profissional ou quarto inexistente          -> 404
 *
 * USA: InternacaoService, PacienteService (paciente logado)
 */
