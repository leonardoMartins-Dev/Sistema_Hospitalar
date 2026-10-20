package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE CONSULTAS  (/consultas)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *              Rotas marcadas com [GONZAGA]: Leonardo Gonzaga
 *
 * O QUE É
 * @RestController @RequestMapping("/consultas"): devolve JSON. Por decisão do
 * grupo, as quatro modalidades (rotina, especializada, emergência e
 * cirurgia) passam por aqui. O "tipo" no JSON diz qual é.
 * As páginas HTML continuam sendo abertas pelo SecureLoginController e
 * chamam estas rotas com fetch (ver static/js/api.js).
 *
 * ATRIBUTOS: ConsultaService, PacienteService, ProfissionalSaudeService (construtor)
 *
 * ROTAS
 *   POST /consultas                         body: AtendimentoRequest -> 201 + atendimento criado
 *       Paciente logado (ROLE_USER): só ROTINA ou ESPECIALIZADA, e o
 *       pacienteId do body é TROCADO pelo id do paciente logado
 *       (pacienteService.buscarPorEmail(authentication.getName())).
 *       EMERGENCIA e CIRURGIA: só MEDICO ou ADMIN.
 *   GET  /consultas?tipo=EMERGENCIA          todos, com filtro opcional (MEDICO, ADMIN)
 *   GET  /consultas/{id}                     um atendimento
 *   GET  /consultas/minhas                   do paciente logado (tela paciente/consultas)
 *   GET  /consultas/agenda?data=2026-11-10   do médico logado no dia (tela medico/consultas)
 *   [GONZAGA] GET /consultas/emergencias?status=AGUARDANDO_AVALIACAO
 *                                            fila de emergência (MEDICO, ADMIN)
 *
 *   PUT  /consultas/{id}/confirmar           paciente dono da consulta ou ADMIN
 *   PUT  /consultas/{id}/iniciar?profissionalId=3
 *                                            MEDICO. profissionalId só é usado na
 *                                            emergência (o médico que assume)
 *   PUT  /consultas/{id}/concluir            body: {"observacoes": "..."}  MEDICO
 *   PUT  /consultas/{id}/cancelar            paciente dono, MEDICO ou ADMIN
 *   PUT  /consultas/{id}/nao-compareceu      MEDICO ou ADMIN
 *   PUT  /consultas/{id}/encaminhar?especialidade=CARDIOLOGIA
 *                                            MEDICO (clínico geral da consulta)
 *   POST /consultas/{id}/reagendar           body: {"data": "...", "horario": "..."}
 *                                            -> 201 + a consulta NOVA
 *
 * BODIES PEQUENOS
 * Para {"observacoes"} e {"data","horario"} não precisa criar arquivo no
 * pacote dto: declarem records dentro do próprio controller, por exemplo
 *   record ConcluirRequest(String observacoes) {}
 *   record ReagendarRequest(LocalDate data, LocalTime horario) {}
 *
 * QUEM PODE
 * Usem @PreAuthorize("hasRole('MEDICO')") etc. nos métodos (precisa do
 * @EnableMethodSecurity no SecurityConfig). Para "paciente dono da
 * consulta", comparem atendimento.getPaciente().getEmail() com
 * authentication.getName().
 *
 * ERROS
 * Regra quebrada (horário ocupado, mudança de situação proibida, médico
 * incompatível...) -> o service/model lança RegraNegocioException e o
 * GlobalExceptionHandler devolve 400 com {"message": "..."}.
 * Id inexistente -> RecursoNaoEncontradoException -> 404.
 *
 * USA: ConsultaService
 */
