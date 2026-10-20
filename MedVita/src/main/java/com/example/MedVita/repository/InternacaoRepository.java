package com.example.MedVita.repository;

/*
 * =========================================================
 * REPOSITÓRIO DE INTERNAÇÕES
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE VAI SER
 *   interface InternacaoRepository extends JpaRepository<Internacao, Long>
 *
 * MÉTODOS A MAIS
 *   List<Internacao> findByPacienteIdOrderByDataEntradaDesc(Long pacienteId)
 *       -> internações do paciente (tela paciente/internacoes e histórico)
 *
 *   List<Internacao> findByStatusOrderByDataEntradaDesc(StatusInternacao status)
 *       -> quem está internado agora (status ATIVA, tela medico/internacoes)
 *
 *   boolean existsByPacienteIdAndStatus(Long pacienteId, StatusInternacao status)
 *       -> o paciente já está internado? (com ATIVA)
 *
 *   boolean existsByQuartoIdAndStatus(Long quartoId, StatusInternacao status)
 *       -> opcional: conferir se o quarto tem gente antes de mexer nele
 */
