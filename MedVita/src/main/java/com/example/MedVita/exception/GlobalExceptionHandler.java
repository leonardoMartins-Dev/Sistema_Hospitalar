package com.example.MedVita.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


/**
 * TRATAMENTO CENTRAL DE ERROS DOS CONTROLLERS.
 *
 * Com @ControllerAdvice, qualquer exceção que "escapar" de um método de
 * controller cai aqui, em vez de mostrar a página de erro padrão do Spring.
 * O método escolhido é o do tipo de exceção mais específico:
 *
 *   - SendEmailException -> falha ao mandar o e-mail de recuperação.
 *     Devolve HTTP 500 com um JSON contendo data/hora e a mensagem do erro.
 *   - Exception (qualquer outra) -> por exemplo, erro de conexão ou de SQL
 *     no Supabase. Devolve HTTP 500 com uma mensagem genérica, para não
 *     expor detalhes internos ao usuário.
 *
 * Os dois métodos registram o erro completo (com stack trace) no console.
 * Sem isso, um erro de banco apareceria para o usuário só como
 * "Ocorreu um erro inesperado" e não haveria como descobrir a causa.
 *
 * =========================================================
 * A CRIAR (Sprint 2, responsável: Leonardo Martins)
 * =========================================================
 * Um método @ExceptionHandler para cada caso, no mesmo formato dos de cima
 * (Map com "timestamp" e "message"):
 *   - RecursoNaoEncontradoException    -> HTTP 404 com a mensagem
 *   - RegraNegocioException            -> HTTP 400 com a mensagem
 *   - DataIntegrityViolationException  -> HTTP 409 com uma mensagem amigável
 *       (ex.: dois agendamentos no mesmo horário passaram juntos e o índice
 *       único do banco recusou o segundo; ou CPF/e-mail repetido)
 *   - HttpMessageNotReadableException  -> HTTP 400, "Dados inválidos na requisição"
 *       (ex.: JSON com "tipo": "XPTO", que não existe no enum)
 * Nesses pode ser log.warn em vez de log.error: é erro do usuário, não do sistema.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SendEmailException.class)
    public ResponseEntity<Object> handleEmailSendException(SendEmailException ex) {
        log.error("Erro ao enviar e-mail", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error("Erro inesperado", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Ocorreu um erro inesperado");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
        @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> handleRegraNegocio(RegraNegocioException ex) {
        log.warn("Regra de negócio: {}", ex.getMessage());
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConflito(DataIntegrityViolationException ex) {
        log.warn("Conflito no banco", ex);
        return resposta(HttpStatus.CONFLICT,
                "A operação conflita com dados já cadastrados (registro repetido ou em uso).");
    }

    // JSON com valor inválido (ex.: "tipo": "XPTO") ou parâmetro inválido (?categoria=XPTO)
    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleDadosInvalidos(Exception ex) {
        log.warn("Dados inválidos: {}", ex.getMessage());
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.");
    }

    // IMPORTANTE: sem este, o @PreAuthorize negado cairia no handleGenericException e viraria 500
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAcessoNegado(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN, "Você não tem permissão para esta ação.");
    }

    private ResponseEntity<Object> resposta(HttpStatus status, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", mensagem);
        return new ResponseEntity<>(body, status);
    }
}