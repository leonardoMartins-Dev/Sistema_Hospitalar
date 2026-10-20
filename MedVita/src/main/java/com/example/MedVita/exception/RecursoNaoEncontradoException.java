package com.example.MedVita.exception;

/*
 * =========================================================
 * EXCEÇÃO: RECURSO NÃO ENCONTRADO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2), nos PRIMEIROS DIAS: todo mundo usa.
 *
 * QUANDO É LANÇADA
 * Quando um service procura algo pelo id e não acha.
 * Ex.: "Quarto 305 não encontrado", "Profissional 12 não encontrado".
 *
 * COMO VAI SER
 *   public class RecursoNaoEncontradoException extends RuntimeException {
 *       public RecursoNaoEncontradoException(String message) { super(message); }
 *   }
 *   (igual à SendEmailException que já existe)
 *
 * RESPOSTA
 * O GlobalExceptionHandler devolve HTTP 404 com {"timestamp", "message"}.
 */
