package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DO HISTÓRICO MÉDICO  (/api/historico)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * @RestController: devolve dados em JSON (a API REST pedida no enunciado).
 * As páginas HTML continuam sendo abertas pelo SecureLoginController; elas
 * buscam e enviam os dados para os endereços abaixo.
 *
 * ROTAS
 *   GET /api/historico               -> histórico do paciente logado
 *                                       (tela paciente/historico)
 *   GET /api/historico/{pacienteId}  -> histórico de um paciente
 *                                       (médico e admin)
 *
 * Só leitura: não existe POST, PUT nem DELETE aqui (RN6).
 * USA: HistoricoMedicoService
 */
