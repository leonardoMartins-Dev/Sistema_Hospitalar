package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE PACIENTES  (/pacientes)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/pacientes"): devolve JSON.
 * O cadastro do paciente continua sendo pela página /register
 * (SecureLoginController). Aqui fica só a consulta dos pacientes.
 *
 * ATRIBUTO
 *   private final PacienteService pacienteService;   (recebido no construtor)
 *
 * ROTAS (um método para cada)
 *   GET /pacientes?busca=texto
 *       -> lista da tela medico/pacientes.
 *          Sem "busca": pacienteService.listar().
 *          Com "busca": pacienteService.buscar(texto) (nome, CPF ou e-mail).
 *   GET /pacientes/perfil
 *       -> o paciente logado (tela paciente/perfil):
 *          pacienteService.buscarPorEmail(authentication.getName()).
 *   GET /pacientes/{id}
 *       -> um paciente (botão "Ver ficha"): pacienteService.buscarPorId(id).
 *
 * QUEM PODE
 *   A lista e a busca por id: só MEDICO e ADMIN.
 *   O perfil: o próprio paciente (ROLE_USER).
 *
 * ATENÇÃO: antes de criar este controller, colocar @JsonIgnore no
 * passwordHash do Paciente, para a senha nunca aparecer no JSON.
 */
