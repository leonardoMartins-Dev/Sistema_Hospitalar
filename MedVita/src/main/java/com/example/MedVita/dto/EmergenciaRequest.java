package com.example.MedVita.dto;

import com.example.MedVita.models.enums.Prioridade;

/*
 * =========================================================
 * DTO: NOVA EMERGÊNCIA  (corpo do POST /consultas/emergencias)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 *   pacienteId
 *   motivo        a queixa do paciente
 *   prioridade    BAIXA | MODERADA | ALTA | CRITICA
 *
 * Não tem médico, data nem horário: a emergência começa sem médico e a hora
 * é a da chegada (preenchida pelo construtor de AtendimentoEmergencia).
 *
 * EXEMPLO
 *   { "pacienteId": 8, "motivo": "Falta de ar", "prioridade": "CRITICA" }
 */
public record EmergenciaRequest(
        Long pacienteId,
        String motivo,
        Prioridade prioridade) {
}
