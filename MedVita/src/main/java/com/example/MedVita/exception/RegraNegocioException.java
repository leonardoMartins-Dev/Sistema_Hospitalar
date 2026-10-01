package com.example.MedVita.exception;

/*
 * =========================================================
 * EXCEÇÃO: REGRA DE NEGÓCIO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * QUANDO É LANÇADA
 * Quando um pedido fere uma regra do hospital. É uma exceção só para todas
 * as regras; o que muda é a mensagem. Exemplos:
 *   - "Este profissional já tem consulta neste horário."  (RN3)
 *   - "Quarto sem vaga."                                 (RN4 e RN5)
 *   - "Este paciente já está internado."
 *   - "Só é possível cancelar consultas agendadas."
 *   - "Já existe um médico com este e-mail."
 *
 * COMO VAI SER
 * extends RuntimeException, igual à SendEmailException que já existe.
 *
 * RESPOSTA
 * O GlobalExceptionHandler devolve HTTP 400 com a mensagem, e a página mostra
 * o erro para o usuário.
 */
