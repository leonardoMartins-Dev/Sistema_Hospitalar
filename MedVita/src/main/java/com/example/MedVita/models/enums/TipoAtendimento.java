package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: TIPO (MODALIDADE) DE ATENDIMENTO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * POR QUE FICA COM A PARTE DE PROFISSIONAIS
 * O ProfissionalSaude.podeRealizar(tipo, especialidade) recebe este enum.
 * Deixando o enum junto dos profissionais, a hierarquia de profissionais
 * não depende de nenhuma classe de atendimento. Vai para a main nos
 * primeiros dias, junto com o ProfissionalSaude.
 *
 * VALORES
 *   ROTINA("Consulta de rotina")
 *   ESPECIALIZADA("Consulta especializada")
 *   EMERGENCIA("Atendimento de emergência")
 *   CIRURGIA("Procedimento cirúrgico")
 *
 * ATRIBUTO: String descricao + getDescricao()
 *
 * Os NOMES são iguais aos @DiscriminatorValue das subclasses de Atendimento
 * (coluna "tipo" da tabela atendimentos).
 *
 * Usado em:
 *   - Atendimento.getTipo() (cada subclasse devolve o seu; sai no JSON)
 *   - ProfissionalSaude.podeRealizar(tipo, especialidade)
 *   - ConsultaRequest.tipo (no POST /consultas: ROTINA ou ESPECIALIZADA)
 *   - filtro GET /consultas?tipo=EMERGENCIA
 */
