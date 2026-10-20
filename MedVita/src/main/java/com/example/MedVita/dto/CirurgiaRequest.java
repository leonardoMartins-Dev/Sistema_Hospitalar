package com.example.MedVita.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.MedVita.models.enums.TipoCirurgia;

/*
 * =========================================================
 * DTO: NOVO PROCEDIMENTO CIRÚRGICO  (corpo do POST /consultas/cirurgias)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 *   pacienteId, cirurgiaoId
 *   data, horario                 data e horário PREVISTOS
 *   descricaoProcedimento
 *   tipoCirurgia                  ELETIVA | URGENCIA | EMERGENCIA
 *   observacoesPreOperatorias
 *
 * EXEMPLO
 *   { "pacienteId": 8, "cirurgiaoId": 9, "data": "2026-11-20", "horario": "07:00",
 *     "descricaoProcedimento": "Apendicectomia", "tipoCirurgia": "URGENCIA",
 *     "observacoesPreOperatorias": "Jejum de 8 horas" }
 */
public record CirurgiaRequest(
        Long pacienteId,
        Long cirurgiaoId,
        LocalDate data,
        LocalTime horario,
        String descricaoProcedimento,
        TipoCirurgia tipoCirurgia,
        String observacoesPreOperatorias) {
}
