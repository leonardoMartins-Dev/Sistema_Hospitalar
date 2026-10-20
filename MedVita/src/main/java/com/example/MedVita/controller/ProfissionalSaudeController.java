package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE PROFISSIONAIS DA SAÚDE  (/profissionais)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/profissionais"): devolve JSON.
 *
 * ATRIBUTOS: ProfissionalSaudeService, ConsultaService (só para /procedimentos)
 *
 * ROTAS
 *   GET    /profissionais                              todos (tela admin/medicos)
 *   GET    /profissionais?categoria=CLINICO_GERAL      filtro por categoria
 *   GET    /profissionais?especialidade=CARDIOLOGIA    especialistas da área
 *   GET    /profissionais?especialidade=CARDIOLOGIA&data=2026-11-10&horario=14:00
 *                                                      só os LIVRES naquele horário
 *                                                      (select da tela de agendamento)
 *          Todos os filtros são @RequestParam(required = false) e vão para
 *          profissionalSaudeService.buscar(categoria, especialidade, data, horario).
 *   GET    /profissionais/especialidades               lista do enum (para os selects)
 *   GET    /profissionais/{id}                         um profissional
 *   GET    /profissionais/{id}/procedimentos           procedimentos do cirurgião
 *                                                      -> consultaService.listarProcedimentosDoCirurgiao(id)
 *   POST   /profissionais                              body: ProfissionalRequest -> 201   (ADMIN)
 *   DELETE /profissionais/{id}                         -> 204                            (ADMIN)
 *
 * QUEM PODE: listar, qualquer usuário logado; cadastrar e remover, só ADMIN
 * (@PreAuthorize("hasRole('ADMIN')")).
 *
 * JSON DE SAÍDA
 * Cada profissional sai com os campos da subclasse dele, mais "categoria" e
 * "areaAtuacao". O passwordHash nunca sai (@JsonIgnore em ProfissionalSaude).
 *
 * USA: ProfissionalSaudeService
 */
