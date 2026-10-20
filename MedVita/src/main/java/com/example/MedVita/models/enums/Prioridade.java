package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: PRIORIDADE DA EMERGÊNCIA
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * As categorias "previamente definidas pelo sistema" do enunciado.
 *
 * VALORES (declarados NESTA ORDEM, da menor para a maior)
 *   BAIXA("Baixa")
 *   MODERADA("Moderada")
 *   ALTA("Alta")
 *   CRITICA("Crítica")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * ORDENAR A FILA
 * Enum já é Comparable pela ordem de declaração, então dá para ordenar a
 * fila com CRITICA primeiro assim:
 *   Comparator.comparing(AtendimentoEmergencia::getPrioridade).reversed()
 *             .thenComparing(AtendimentoEmergencia::getData)
 *             .thenComparing(AtendimentoEmergencia::getHorario)
 * Não ordenem pelo banco (ORDER BY prioridade): com EnumType.STRING ele
 * ordena pelo texto, em ordem alfabética.
 */
