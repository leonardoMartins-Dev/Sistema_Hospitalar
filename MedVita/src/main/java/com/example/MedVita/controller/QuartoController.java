package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE QUARTOS  (/api/quartos)
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
 *   GET  /api/quartos              -> todos (tela medico/quartos)
 *   GET  /api/quartos/disponiveis  -> só os com vaga (escolher na internação)
 *   POST /api/quartos              -> cadastrar (botão "Cadastrar Quarto")
 *
 * QUEM PODE: MEDICO e ADMIN.
 * USA: QuartoService
 */
