package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: SITUAÇÃO DO ATENDIMENTO DE EMERGÊNCIA
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O enunciado pede para identificar os atendimentos que "ainda aguardam
 * avaliação e aqueles que já foram iniciados ou concluídos".
 *
 * VALORES
 *   AGUARDANDO_AVALIACAO("Aguardando avaliação")
 *   EM_ATENDIMENTO("Em atendimento")
 *   CONCLUIDO("Concluído")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * MÉTODO boolean podeMudarPara(StatusEmergencia novo)
 *   AGUARDANDO_AVALIACAO -> EM_ATENDIMENTO
 *   EM_ATENDIMENTO       -> CONCLUIDO
 *   CONCLUIDO            -> nada (final)
 *   Não existe cancelamento de emergência.
 */
