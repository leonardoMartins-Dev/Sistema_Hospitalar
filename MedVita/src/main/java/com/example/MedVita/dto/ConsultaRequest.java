package com.example.MedVita.dto;

/*
 * =========================================================
 * DTO: NOVA CONSULTA  (corpo do POST /consultas)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * POR QUE EXISTE
 * Consulta é abstrata e o JSON chega com ids (pacienteId, profissionalId),
 * não com objetos. O ConsultaService lê o "tipo", busca o paciente e o
 * profissional e cria a subclasse certa.
 *
 * Só serve para as CONSULTAS. Emergência e cirurgia têm DTOs próprios
 * (EmergenciaRequest e CirurgiaRequest, do Leonardo Gonzaga), para nenhum dos
 * dois precisar mexer no arquivo do outro.
 *
 * COMO VAI SER
 *   public record ConsultaRequest(
 *       TipoAtendimento tipo,          ROTINA | ESPECIALIZADA (outro valor -> 400)
 *       Long pacienteId,               ignorado quando quem agenda é o próprio
 *                                      paciente (o controller usa o paciente logado)
 *       Long profissionalId,
 *       LocalDate data,
 *       LocalTime horario,
 *       String motivo,
 *       Especialidade especialidade)   só ESPECIALIZADA
 *
 * EXEMPLOS DE JSON
 *   { "tipo": "ROTINA", "profissionalId": 3, "data": "2026-11-10",
 *     "horario": "14:00", "motivo": "Check-up anual" }
 *
 *   { "tipo": "ESPECIALIZADA", "profissionalId": 5, "data": "2026-11-12",
 *     "horario": "09:30", "motivo": "Dor no peito", "especialidade": "CARDIOLOGIA" }
 */
