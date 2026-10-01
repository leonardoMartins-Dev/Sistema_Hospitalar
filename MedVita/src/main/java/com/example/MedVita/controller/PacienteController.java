package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE PACIENTES  (/api/pacientes)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * @RestController: devolve dados em JSON (a API REST pedida no enunciado).
 * As páginas HTML continuam sendo abertas pelo SecureLoginController; elas
 * buscam os dados nos endereços abaixo.
 *
 * O cadastro do paciente continua sendo pela página /register
 * (SecureLoginController). Aqui fica só a consulta dos pacientes.
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 * ATRIBUTO
 *   private final PacienteService pacienteService;  (recebido no construtor)
 *
 * ROTAS (um método para cada)
 *   GET /api/pacientes?busca=texto
 *       -> lista de pacientes da tela medico/pacientes.
 *          Sem "busca": pacienteService.listar().
 *          Com "busca": pacienteService.buscar(texto) (nome, CPF ou e-mail).
 *   GET /api/pacientes/{id}
 *       -> um paciente (botão "Ver ficha"): pacienteService.buscarPorId(id).
 *   GET /api/pacientes/perfil
 *       -> o paciente logado (tela paciente/perfil):
 *          pacienteService.buscarPorEmail(authentication.getName()).
 *
 * QUEM PODE
 *   A lista e a busca por id: só MEDICO e ADMIN.
 *   O perfil: o próprio paciente.
 *
 * ATENÇÃO: antes de criar este controller, colocar @JsonIgnore no
 * passwordHash do Paciente, para a senha nunca aparecer no JSON.
 */
