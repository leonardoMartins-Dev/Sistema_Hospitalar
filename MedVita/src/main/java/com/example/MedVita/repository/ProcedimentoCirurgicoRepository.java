package com.example.MedVita.repository;

/*
 * =========================================================
 * REPOSITÓRIO DE PROCEDIMENTOS CIRÚRGICOS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE VAI SER
 *   interface ProcedimentoCirurgicoRepository extends JpaRepository<ProcedimentoCirurgico, Long>
 *
 * Repositório de subclasse, igual ao AtendimentoEmergenciaRepository: busca
 * só as linhas com tipo = 'CIRURGIA' da tabela "atendimentos".
 *
 * MÉTODOS A MAIS
 *   List<ProcedimentoCirurgico> findByProfissionalIdOrderByDataDescHorarioDesc(Long cirurgiaoId)
 *       -> procedimentos de um cirurgião (GET /consultas/procedimentos?cirurgiaoId=)
 *
 *   List<ProcedimentoCirurgico> findByProfissionalIdAndDataAndHorario(Long cirurgiaoId,
 *                                                                      LocalDate data, LocalTime horario)
 *       -> REGRA 3 ao agendar: conferir com .anyMatch(ProcedimentoCirurgico::ocupaAgenda).
 *          Basta olhar os procedimentos: o cirurgião só pode ter procedimentos
 *          (podeRealizar), então não existe consulta dele para dar conflito.
 *          O índice único do banco continua sendo a garantia final.
 */
