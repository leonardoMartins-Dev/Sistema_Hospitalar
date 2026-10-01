package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE INTERNAÇÕES  (/api/internacoes)
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
 *   POST /api/internacoes            -> internar (botão "Registrar Internação"
 *                                       da tela medico/internacoes)
 *   GET  /api/internacoes            -> quem está internado agora
 *                                       (tela medico/internacoes)
 *   GET  /api/internacoes/minhas     -> internações do paciente logado
 *                                       (tela paciente/internacoes)
 *   PUT  /api/internacoes/{id}/alta  -> dar alta
 *
 * QUEM PODE: internar e dar alta, só MEDICO.
 * USA: InternacaoService
 */
