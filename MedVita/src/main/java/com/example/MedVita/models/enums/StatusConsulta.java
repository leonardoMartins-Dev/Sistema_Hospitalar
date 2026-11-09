package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: SITUAÇÃO DA CONSULTA
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * As 6 situações do enunciado. Substitui o texto livre da Sprint 1
 * ("AGENDADA", "REALIZADA", "CANCELADA"). Vale para ConsultaRotina e
 * ConsultaEspecializada (o atributo fica na classe Consulta).
 *
 * VALORES
 *   AGENDADA("Agendada")
 *   CONFIRMADA("Confirmada")
 *   EM_ATENDIMENTO("Em atendimento")
 *   CONCLUIDA("Concluída")
 *   CANCELADA("Cancelada")
 *   NAO_COMPARECEU("Não compareceu")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * MÉTODO boolean podeMudarPara(StatusConsulta novo)   (um switch em this)
 *
 *   de \ para        CONFIRMADA  EM_ATENDIMENTO  CONCLUIDA  CANCELADA  NAO_COMPARECEU
 *   AGENDADA            sim          não           não        sim          sim
 *   CONFIRMADA          não          sim           não        sim          sim
 *   EM_ATENDIMENTO      não          não           sim        não          não
 *   CONCLUIDA        -- estado final: não muda mais --
 *   CANCELADA        -- estado final (reagendar cria uma consulta NOVA) --
 *   NAO_COMPARECEU   -- estado final (reagendar cria uma consulta NOVA) --
 *
 *   Isso cobre os exemplos do enunciado: concluída não volta para agendada;
 *   cancelada não vira concluída sem um novo agendamento.
 *
 * MÉTODO boolean isFinal() -> CONCLUIDA, CANCELADA ou NAO_COMPARECEU
 *
 * No banco fica o NOME (@Enumerated(EnumType.STRING)), nunca o número.
 */

public enum StatusConsulta {

    AGENDADA("Agendada"),
    CONFIRMADA("Confirmada"),
    EM_ATENDIMENTO("Em atendimento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    NAO_COMPARECEU("Não compareceu");

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeMudarPara(StatusConsulta novo) {
        switch (this) {
            case AGENDADA:
                return novo == CONFIRMADA || novo == CANCELADA || novo == NAO_COMPARECEU;
            case CONFIRMADA:
                return novo == EM_ATENDIMENTO || novo == CANCELADA || novo == NAO_COMPARECEU;
            case EM_ATENDIMENTO:
                return novo == CONCLUIDA;
            default:
                return false;
        }
    }

    public boolean isFinal() {
        return this == CONCLUIDA || this == CANCELADA || this == NAO_COMPARECEU;
    }
}
