package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE CONSULTAS  (/api/consultas)
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
 *   POST /api/consultas                  -> agendar (paciente logado; tela
 *                                           paciente/agendar-consulta)
 *   GET  /api/consultas/minhas           -> consultas do paciente logado
 *                                           (tela paciente/consultas)
 *   GET  /api/consultas?data=2026-10-15  -> agenda do médico logado no dia
 *                                           (tela medico/consultas)
 *   PUT  /api/consultas/{id}/cancelar    -> o paciente cancela
 *   PUT  /api/consultas/{id}/realizar    -> o médico registra as observações
 *
 * Se o horário estiver ocupado (RN3), o service lança RegraNegocioException
 * e o GlobalExceptionHandler devolve a mensagem de erro.
 * USA: ConsultaService
 */
