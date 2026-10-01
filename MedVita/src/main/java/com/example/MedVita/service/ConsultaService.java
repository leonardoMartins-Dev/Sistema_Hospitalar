package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE CONSULTAS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * USA: ConsultaRepository, ProfissionalSaudeService, PacienteService
 *
 * MÉTODOS
 *   agendar(paciente, profissionalId, data, horario, motivo)
 *       1. O profissional existe (RN2).
 *       2. RN3: o profissional já tem consulta nesse dia e horário?
 *          -> RegraNegocioException("Horário indisponível").
 *       3. Salva com status "AGENDADA".
 *   cancelar(id)
 *       Só se estiver "AGENDADA"; muda para "CANCELADA".
 *       A consulta não é apagada (RN6).
 *   realizar(id, observacoes)
 *       O médico registra as observações; muda para "REALIZADA".
 *   listarDoPaciente(pacienteId)                 -> tela paciente/consultas
 *   listarDoProfissional(profissionalId, data)   -> tela medico/consultas
 */
