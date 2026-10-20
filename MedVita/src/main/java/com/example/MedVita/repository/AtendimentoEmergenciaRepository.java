package com.example.MedVita.repository;

/*
 * =========================================================
 * REPOSITÓRIO DE ATENDIMENTOS DE EMERGÊNCIA
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE VAI SER
 *   interface AtendimentoEmergenciaRepository extends JpaRepository<AtendimentoEmergencia, Long>
 *
 * O Spring Data aceita repositório de uma SUBCLASSE: ele busca na mesma
 * tabela "atendimentos", só nas linhas com tipo = 'EMERGENCIA'. Com isso dá
 * para usar os campos que só existem aqui (status, prioridade) direto no nome
 * do método, sem @Query, e sem mexer no AtendimentoRepository do João Pedro.
 *
 * MÉTODOS A MAIS
 *   List<AtendimentoEmergencia> findByStatus(StatusEmergencia status)
 *       -> fila de emergência. A ordenação por prioridade é feita em Java (ver
 *          enum Prioridade): o banco ordenaria pelo texto, em ordem alfabética.
 */
