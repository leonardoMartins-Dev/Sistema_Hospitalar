package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE PROFISSIONAIS DA SAÚDE  (/profissionais)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/profissionais"): devolve JSON.
 *
 * ATRIBUTO: ProfissionalSaudeService (construtor). Só ele: esta parte não
 * depende de nenhuma outra.
 *
 * ROTAS
 *   GET    /profissionais                              todos (tela admin/medicos)
 *   GET    /profissionais?categoria=CLINICO_GERAL      filtro por categoria
 *   GET    /profissionais?especialidade=CARDIOLOGIA    especialistas da área
 *          Os filtros são @RequestParam(required = false) e vão para
 *          profissionalSaudeService.buscar(categoria, especialidade).
 *   GET    /profissionais/especialidades               [{"valor": "CARDIOLOGIA", "nome": "Cardiologia"}, ...]
 *                                                      (para os selects)
 *   GET    /profissionais/{id}                         um profissional
 *   POST   /profissionais                              body: ProfissionalRequest -> 201   (ADMIN)
 *   DELETE /profissionais/{id}                         -> 204                            (ADMIN)
 *
 * O que NÃO fica aqui (para esta parte não depender das outras):
 *   médicos livres num horário -> GET /consultas/profissionais-disponiveis (João Pedro)
 *   procedimentos do cirurgião -> GET /consultas/procedimentos?cirurgiaoId= (Leonardo Gonzaga)
 *
 * QUEM PODE: listar, qualquer usuário logado; cadastrar e remover, só ADMIN
 * (@PreAuthorize("hasRole('ADMIN')")).
 *
 * JSON DE SAÍDA
 * Cada profissional sai com os campos da subclasse dele, mais "categoria" e
 * "areaAtuacao". O passwordHash nunca sai (@JsonIgnore em ProfissionalSaude).
 */
