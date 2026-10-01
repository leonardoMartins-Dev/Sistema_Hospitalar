package com.example.MedVita.models;

/*
 * =========================================================
 * HISTÓRICO MÉDICO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * Requisito 6 do enunciado: consultar o histórico de um paciente, com as
 * consultas realizadas, as internações e as informações registradas nos
 * atendimentos (as observações).
 *
 * NÃO É UMA TABELA
 * Diferente dos outros models, este não tem @Entity. O histórico já está no
 * banco: são as consultas e internações do paciente, que nunca são apagadas
 * (RN6). Esta classe só junta tudo num objeto para mostrar na tela.
 *
 * CAMPOS
 *   Paciente paciente
 *   List<Consulta> consultas       só as REALIZADAS (com as observações médicas)
 *   List<Internacao> internacoes   todas (com as observações)
 *
 * QUEM MONTA: HistoricoMedicoService.
 * TELA: paciente/historico (e o médico, ao consultar um paciente)
 */
