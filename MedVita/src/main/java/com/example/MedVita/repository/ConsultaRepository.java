package com.example.MedVita.repository;

/*
 * =========================================================
 * REPOSITÓRIO DE CONSULTAS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE VAI SER
 * Uma interface, igual ao PacienteRepository: o Spring Data JPA cria a
 * implementação sozinho. Já vem com save, findById, findAll e deleteById.
 *
 *   interface ConsultaRepository extends JpaRepository<Consulta, Long>
 *
 * MÉTODOS A MAIS
 *   existsByProfissionalIdAndDataAndHorarioAndStatusNot(profissionalId, data,
 *           horario, "CANCELADA")
 *         -> Regra de Negócio 3: o horário já está ocupado?
 *   findByPacienteId(pacienteId)                       -> consultas do paciente
 *   findByPacienteIdAndStatus(pacienteId, "REALIZADA") -> histórico
 *   findByProfissionalIdAndData(profissionalId, data)  -> agenda do médico no dia
 */
