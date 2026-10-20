package com.example.MedVita.dto;

/*
 * =========================================================
 * DTO: NOVA INTERNAÇÃO  (corpo do POST /internacoes)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * POR QUE EXISTE
 * O JSON chega com ids. O InternacaoService busca paciente, profissional e
 * quarto e monta a Internacao.
 *
 * COMO VAI SER
 *   public record InternacaoRequest(
 *       Long pacienteId,
 *       Long profissionalId,
 *       Long quartoId,
 *       LocalDate dataEntrada,
 *       LocalDate dataPrevistaAlta,
 *       String observacoes)
 *
 * EXEMPLO
 *   { "pacienteId": 8, "profissionalId": 3, "quartoId": 2,
 *     "dataEntrada": "2026-11-10", "dataPrevistaAlta": "2026-11-15",
 *     "observacoes": "Pós-operatório" }
 */
