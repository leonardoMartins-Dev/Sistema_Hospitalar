package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: CATEGORIA DO PROFISSIONAL
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * VALORES
 *   CLINICO_GERAL("Médico clínico geral")
 *   ESPECIALISTA("Médico especialista")
 *   CIRURGIAO("Cirurgião")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * Os NOMES são iguais aos @DiscriminatorValue das subclasses de
 * ProfissionalSaude (coluna "tipo" da tabela profissionais_saude).
 *
 * Usado em:
 *   - ProfissionalSaude.getCategoria() (cada subclasse devolve o seu)
 *   - ProfissionalRequest.tipo (o admin escolhe a categoria no cadastro)
 *   - filtro GET /profissionais?categoria=CLINICO_GERAL
 */

public enum CategoriaProfissional {
    CLINICO_GERAL("Médico clínico geral"),
    ESPECIALISTA("Médico especialista"),
    CIRURGIAO("Cirurgião");

    private final String descricao;

    CategoriaProfissional(String descricao){
        this.descricao=descricao;
    }

    public String getDescricao(){
        return descricao;
    }
}