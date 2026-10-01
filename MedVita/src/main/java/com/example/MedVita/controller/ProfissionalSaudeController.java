package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE PROFISSIONAIS DA SAÚDE  (/api/profissionais)
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
 *   GET    /api/profissionais                        -> lista (tela admin/medicos)
 *   GET    /api/profissionais?especialidade=...      -> filtro da tela de agendamento
 *   POST   /api/profissionais                        -> cadastrar médico
 *                                                       (formulário de admin/medicos)
 *   DELETE /api/profissionais/{id}                   -> remover médico
 *
 * QUEM PODE: listar, qualquer usuário logado; cadastrar e remover, só ADMIN.
 * USA: ProfissionalSaudeService
 *
 * ATENÇÃO: o campo passwordHash precisa de @JsonIgnore, para a senha nunca
 * aparecer no JSON.
 */
