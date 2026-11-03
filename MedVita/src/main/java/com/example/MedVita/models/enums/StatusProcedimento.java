package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: SITUAÇÃO DO PROCEDIMENTO CIRÚRGICO
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
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
public enum StatusProcedimento {

    AGENDADO("Agendado"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusProcedimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    //DIZ SE A MUDANÇA DE SITUAÇÃO É PERMITIDA
    public boolean podeMudarPara(StatusProcedimento novo) {
        switch (this) {
            case AGENDADO:
                return novo == EM_ANDAMENTO || novo == CANCELADO;
            case EM_ANDAMENTO:
                return novo == CONCLUIDO;
            default:
                return false; // CONCLUIDO e CANCELADO são finais
        }
    }
}
