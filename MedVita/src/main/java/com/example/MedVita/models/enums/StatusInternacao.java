package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: SITUAÇÃO DA INTERNAÇÃO
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * VALORES
 *   ATIVA("Internado")
 *   ALTA("Alta concedida")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * ATIVA -> ALTA (pelo Internacao.darAlta). ALTA é final.
 * O banco garante que um paciente não tenha duas internações ATIVA ao
 * mesmo tempo (índice único parcial, ver doc da Sprint 2).
 */

public enum StatusInternacao {
    ATIVA("Internado"),
    ALTA("Alta concedida");

    private final String descricao;

    StatusInternacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
