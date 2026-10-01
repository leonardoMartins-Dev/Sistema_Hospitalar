package com.example.MedVita.repository;

/*
 * =========================================================
 * REPOSITÓRIO DE INTERNAÇÕES
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE VAI SER
 * Uma interface, igual ao PacienteRepository: o Spring Data JPA cria a
 * implementação sozinho. Já vem com save, findById, findAll e deleteById.
 *
 *   interface InternacaoRepository extends JpaRepository<Internacao, Long>
 *
 * MÉTODOS A MAIS
 *   findByPacienteId(pacienteId)
 *         -> internações do paciente (tela e histórico)
 *   findByDataEfetivaAltaIsNull()
 *         -> quem está internado agora (tela medico/internacoes)
 *   existsByPacienteIdAndDataEfetivaAltaIsNull(pacienteId)
 *         -> o paciente já está internado?
 */
