package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE QUARTOS  (/quartos)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/quartos"): devolve JSON.
 *
 * ROTAS
 *   GET  /quartos                          todos (tela medico/quartos)
 *   GET  /quartos?tipo=UTI                 filtro por tipo
 *   GET  /quartos?disponivel=true          só os com vaga (select da internação)
 *        (tipo e disponivel podem ir juntos; @RequestParam(required = false))
 *   GET  /quartos/tipos                    [{"valor": "UTI", "nome": "UTI"}, ...] (para o select)
 *   GET  /quartos/{id}                     um quarto
 *   POST /quartos                          body: {numero, andar, capacidadeMaxima, tipo} -> 201
 *
 * QUEM PODE: MEDICO e ADMIN.
 *
 * ERROS
 *   número repetido ou capacidade fora do limite do tipo -> 400 (RegraNegocioException)
 *
 * USA: QuartoService
 */
