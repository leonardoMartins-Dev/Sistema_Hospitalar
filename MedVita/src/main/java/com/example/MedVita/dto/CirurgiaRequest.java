package com.example.MedVita.dto;

/*
 * =========================================================
 * DTO: NOVO PROCEDIMENTO CIRÚRGICO  (corpo do POST /consultas/cirurgias)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * COMO VAI SER
 *   public record CirurgiaRequest(
 *       Long pacienteId,
 *       Long cirurgiaoId,
 *       LocalDate data,                      data prevista
 *       LocalTime horario,                   horário previsto
 *       String descricaoProcedimento,
 *       TipoCirurgia tipoCirurgia,           ELETIVA | URGENCIA | EMERGENCIA
 *       String observacoesPreOperatorias)
 *
 * EXEMPLO
 *   { "pacienteId": 8, "cirurgiaoId": 9, "data": "2026-11-20", "horario": "07:00",
 *     "descricaoProcedimento": "Apendicectomia", "tipoCirurgia": "URGENCIA",
 *     "observacoesPreOperatorias": "Jejum de 8 horas" }
 */
