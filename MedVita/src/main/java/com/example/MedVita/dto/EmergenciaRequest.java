package com.example.MedVita.dto;

/*
 * =========================================================
 * DTO: NOVA EMERGÊNCIA  (corpo do POST /consultas/emergencias)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * COMO VAI SER
 *   public record EmergenciaRequest(
 *       Long pacienteId,
 *       String motivo,             a queixa do paciente
 *       Prioridade prioridade)     BAIXA | MODERADA | ALTA | CRITICA
 *
 * Não tem médico, data nem horário: a emergência começa sem médico e a hora
 * é a da chegada (preenchida pelo construtor de AtendimentoEmergencia).
 *
 * EXEMPLO
 *   { "pacienteId": 8, "motivo": "Falta de ar", "prioridade": "CRITICA" }
 */
