package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE EMERGÊNCIAS E CIRURGIAS  (também em /consultas)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/consultas"): o mesmo endereço do
 * ConsultaController (decisão do grupo: tudo em /consultas), mas em outro
 * arquivo, para o João Pedro e o Leonardo Gonzaga não mexerem no mesmo
 * controller.
 *
 * ATRIBUTO: EmergenciaCirurgiaService (construtor)
 *
 * ROTAS
 *   POST /consultas/emergencias                    body: EmergenciaRequest -> 201
 *                                                  (MEDICO, ADMIN)
 *   GET  /consultas/emergencias?status=AGUARDANDO_AVALIACAO
 *                                                  fila por prioridade (MEDICO, ADMIN)
 *   PUT  /consultas/emergencias/{id}/assumir?profissionalId=3
 *                                                  o médico assume e o atendimento
 *                                                  começa (MEDICO)
 *   POST /consultas/cirurgias                      body: CirurgiaRequest -> 201
 *                                                  (MEDICO, ADMIN)
 *   GET  /consultas/procedimentos?cirurgiaoId=9    procedimentos de um cirurgião
 *
 * O QUE NÃO PRECISA FAZER AQUI
 * Iniciar a cirurgia, concluir (emergência e cirurgia) e cancelar a cirurgia
 * usam as rotas genéricas do ConsultaController:
 *   PUT /consultas/{id}/iniciar   PUT /consultas/{id}/concluir   PUT /consultas/{id}/cancelar
 * Elas chamam atendimento.iniciar() etc., e as suas classes respondem com as
 * regras delas (ex.: concluir cirurgia não iniciada dá erro).
 *
 * QUEM PODE: @PreAuthorize em cada método.
 * USA: EmergenciaCirurgiaService
 */
