package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: SITUAÇÃO DO ATENDIMENTO DE EMERGÊNCIA
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
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
public enum StatusEmergencia {

    AGUARDANDO_AVALIACAO("Aguardando avaliação"),
    EM_ATENDIMENTO("Em atendimento"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusEmergencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    //DIZ SE A MUDANÇA DE SITUAÇÃO É PERMITIDA
    public boolean podeMudarPara(StatusEmergencia novo) {
        switch (this) {
            case AGUARDANDO_AVALIACAO:
                return novo == EM_ATENDIMENTO;
            case EM_ATENDIMENTO:
                return novo == CONCLUIDO;
            default:
                return false; // CONCLUIDO é final
        }
    }
}
