package com.example.MedVita.exception;

/*
 * =========================================================
 * EXCEÇÃO: REGRA DE NEGÓCIO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2), nos PRIMEIROS DIAS: todo mundo usa.
 *
 * QUANDO É LANÇADA
 * Quando um pedido fere uma regra do hospital. É uma exceção só para todas
 * as regras; o que muda é a mensagem. Na Sprint 2 ela também é lançada pelos
 * MODELS (ex.: Consulta.mudarStatus, Quarto.ocupar). Exemplos:
 *   - "O profissional já tem atendimento neste horário."           (RN3)
 *   - "Quarto 305 está lotado."                                    (RN4 e RN5)
 *   - "Este paciente já está internado."
 *   - "Uma consulta Concluída não pode passar para Agendada."      (situações)
 *   - "Não é possível concluir um procedimento que não foi iniciado."
 *   - "Ana Souza (Ortopedia) não pode realizar Consulta especializada."
 *   - "Quarto do tipo Apartamento deve ter de 1 a 2 leitos."
 *   - "Atendimento de emergência não pode ser cancelado."
 *   - "Já existe um profissional com este e-mail."
 *
 * COMO VAI SER
 *   public class RegraNegocioException extends RuntimeException {
 *       public RegraNegocioException(String message) { super(message); }
 *   }
 *
 * RESPOSTA
 * O GlobalExceptionHandler devolve HTTP 400 com a mensagem, e a página mostra
 * o erro para o usuário.
 */
