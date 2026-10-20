package com.example.MedVita.controller;

/*
 * =========================================================
 * CONTROLLER DE CONSULTAS  (/consultas)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/consultas"): devolve JSON.
 * As páginas HTML continuam sendo abertas pelo SecureLoginController e
 * chamam estas rotas com fetch (ver static/js/api.js).
 *
 * Emergência e cirurgia também ficam em /consultas, mas as rotas específicas
 * delas estão no EmergenciaCirurgiaController (Leonardo Gonzaga), em outro
 * arquivo. O Spring aceita dois controllers no mesmo @RequestMapping desde
 * que as rotas não sejam iguais: /consultas/emergencias (de lá) tem
 * prioridade sobre /consultas/{id} (daqui) porque é um caminho fixo.
 *
 * ATRIBUTOS: ConsultaService, PacienteService (paciente logado; já existe)
 *
 * ROTAS
 *   POST /consultas                         body: ConsultaRequest -> 201 + consulta criada
 *       Paciente logado (ROLE_USER): o pacienteId do body é TROCADO pelo id do
 *       paciente logado (pacienteService.buscarPorEmail(authentication.getName())).
 *       ADMIN pode agendar para qualquer paciente.
 *   GET  /consultas?tipo=ROTINA              todos os atendimentos, filtro opcional (MEDICO, ADMIN)
 *   GET  /consultas/{id}                     um atendimento (de qualquer modalidade)
 *   GET  /consultas/minhas                   do paciente logado (tela paciente/consultas)
 *   GET  /consultas/agenda?data=2026-11-10   do médico logado no dia (tela medico/consultas)
 *   GET  /consultas/profissionais-disponiveis?especialidade=CARDIOLOGIA&data=...&horario=...
 *                                            médicos livres no horário (select da tela de
 *                                            agendamento). Sem especialidade = clínicos gerais.
 *
 *   ROTAS GENÉRICAS (valem para as 4 modalidades, por polimorfismo)
 *   PUT  /consultas/{id}/iniciar             MEDICO
 *   PUT  /consultas/{id}/concluir            body: {"observacoes": "..."}  MEDICO
 *   PUT  /consultas/{id}/cancelar            paciente dono, MEDICO ou ADMIN
 *
 *   SÓ CONSULTAS
 *   PUT  /consultas/{id}/confirmar           paciente dono da consulta ou ADMIN
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
 * @EnableMethodSecurity no SecurityConfig; enquanto ele não estiver na main,
 * as anotações só são ignoradas, então não trava ninguém). Para "paciente
 * dono da consulta", comparem atendimento.getPaciente().getEmail() com
 * authentication.getName().
 *
 * ERROS
 * Regra quebrada (horário ocupado, mudança de situação proibida, médico
 * incompatível...) -> RegraNegocioException -> 400 com {"message": "..."}.
 * Id inexistente -> RecursoNaoEncontradoException -> 404.
 *
 * USA: ConsultaService
 */
