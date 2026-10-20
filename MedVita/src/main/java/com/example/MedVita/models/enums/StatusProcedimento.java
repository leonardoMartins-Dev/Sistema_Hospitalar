package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: SITUAÇÃO DO PROCEDIMENTO CIRÚRGICO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * VALORES (os quatro do enunciado)
 *   AGENDADO("Agendado")
 *   EM_ANDAMENTO("Em andamento")
 *   CONCLUIDO("Concluído")
 *   CANCELADO("Cancelado")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * MÉTODO boolean podeMudarPara(StatusProcedimento novo)
 *   AGENDADO     -> EM_ANDAMENTO ou CANCELADO
 *   EM_ANDAMENTO -> CONCLUIDO
 *   CONCLUIDO    -> nada (final)
 *   CANCELADO    -> nada (final)
 *
 *   AGENDADO -> CONCLUIDO dá false: é a regra "não será permitido registrar
 *   um procedimento como concluído se ele não tiver sido iniciado".
 */
