package com.example.MedVita.exception;

/*
 * =========================================================
 * EXCEÇÃO: RECURSO NÃO ENCONTRADO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * QUANDO É LANÇADA
 * Quando um service procura algo pelo id e não acha.
 * Ex.: "Quarto 305 não encontrado".
 *
 * COMO VAI SER
 * extends RuntimeException, igual à SendEmailException que já existe.
 *
 * RESPOSTA
 * O GlobalExceptionHandler devolve HTTP 404 com a mensagem.
 */
