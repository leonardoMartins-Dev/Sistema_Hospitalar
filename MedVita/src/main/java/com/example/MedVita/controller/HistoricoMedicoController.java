package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DO HISTÓRICO MÉDICO  (/historico)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/historico"): devolve JSON.
 * Não está na lista de endpoints do PDF da Sprint 2, mas o histórico é a RN6
 * da Sprint 1 e a tela paciente/historico usa. O grupo decidiu manter.
 *
 * ROTAS
 *   GET /historico               -> histórico do paciente logado
 *                                   (tela paciente/historico)
 *                                   historicoMedicoService.doPacienteLogado(authentication.getName())
 *   GET /historico/{pacienteId}  -> histórico de um paciente (MEDICO e ADMIN)
 *
 * Só leitura: não existe POST, PUT nem DELETE aqui (RN6).
 * USA: HistoricoMedicoService
 */
