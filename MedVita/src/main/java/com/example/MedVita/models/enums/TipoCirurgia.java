package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: TIPO DE CIRURGIA
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O enunciado pede "tipo de cirurgia" no procedimento cirúrgico.
 *
 * VALORES
 *   ELETIVA("Eletiva")         marcada com antecedência
 *   URGENCIA("Urgência")       precisa ser feita em poucas horas
 *   EMERGENCIA("Emergência")   risco imediato de vida
 *
 * ATRIBUTO: String descricao + getDescricao()
 */
public enum TipoCirurgia {

    ELETIVA("Eletiva"),
    URGENCIA("Urgência"),
    EMERGENCIA("Emergência");

    private final String descricao;

    TipoCirurgia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
