package com.example.MedVita.dto;

/*
 * =========================================================
 * DTO: CADASTRO DE PROFISSIONAL  (corpo do POST /profissionais)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * POR QUE EXISTE
 *   - ProfissionalSaude é abstrata: o Jackson não sabe criar o objeto a
 *     partir do JSON.
 *   - O formulário manda a "senha", e a entidade só tem "passwordHash".
 *   O DTO recebe o JSON como ele vem e o ProfissionalSaudeService decide qual
 *   subclasse criar.
 *
 * COMO VAI SER (um record)
 *   public record ProfissionalRequest(
 *       CategoriaProfissional tipo,     CLINICO_GERAL | ESPECIALISTA | CIRURGIAO
 *       String nome,
 *       String registroProfissional,
 *       String telefone,
 *       String email,
 *       String senha,
 *       Especialidade especialidade,    só quando tipo = ESPECIALISTA
 *       String areaCirurgica)           só quando tipo = CIRURGIAO
 *
 * EXEMPLO DE JSON
 *   { "tipo": "ESPECIALISTA", "nome": "Ana Souza",
 *     "registroProfissional": "CRM 123456-MG", "telefone": "31999990000",
 *     "email": "ana@medvita.com", "senha": "1234", "especialidade": "CARDIOLOGIA" }
 *
 * Valor de enum que não existe ("tipo": "XPTO") faz o Jackson lançar
 * HttpMessageNotReadableException, e o GlobalExceptionHandler devolve 400.
 */
