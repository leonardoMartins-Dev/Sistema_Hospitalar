package com.example.MedVita.repository;

/*
 * =========================================================
 * REPOSITÓRIO DE PROFISSIONAIS DA SAÚDE
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE VAI SER
 * Uma interface, igual ao PacienteRepository: o Spring Data JPA cria a
 * implementação sozinho. Já vem com save, findById, findAll e deleteById.
 *
 *   interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long>
 *
 * MÉTODOS A MAIS
 *   findByEmail(email)                    -> login do médico
 *   existsByEmail(email)                  -> não deixar cadastrar e-mail repetido
 *   existsByRegistroProfissional(registro)-> não deixar cadastrar registro repetido
 *   findByEspecialidade(especialidade)    -> filtro da tela de agendamento
 */
