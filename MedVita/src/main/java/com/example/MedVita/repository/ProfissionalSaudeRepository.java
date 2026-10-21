package com.example.MedVita.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MedVita.models.MedicoEspecialista;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.Especialidade;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {
    Optional<ProfissionalSaude> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRegistroProfissional(String registroProfissional);

    // "especialidade" só existe na subclasse, por isso precisa de @Query
    @Query("select m from MedicoEspecialista m where m.especialidade = :especialidade order by m.nome")
    List<MedicoEspecialista> buscarEspecialistas(@Param("especialidade") Especialidade especialidade);
}

/*
 * =========================================================
 * REPOSITÓRIO DE PROFISSIONAIS DA SAÚDE
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * Vai para a main NOS PRIMEIROS DIAS, junto com as classes de profissional:
 * os services de consultas, emergência/cirurgia e internações usam o
 * findById e o findByEmail daqui direto (não usam o ProfissionalSaudeService),
 * para não dependerem das regras desta parte.
 *
 * O QUE VAI SER
 * Uma interface, igual ao PacienteRepository. Um repositório só para as três
 * categorias: o findAll() e o findById() já devolvem cada um como a subclasse
 * certa (MedicoClinicoGeral, MedicoEspecialista ou Cirurgiao).
 *
 *   interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long>
 *
 * MÉTODOS A MAIS
 *   Optional<ProfissionalSaude> findByEmail(String email)   -> login do médico
 *   boolean existsByEmail(String email)                      -> e-mail repetido
 *   boolean existsByRegistroProfissional(String registro)    -> registro repetido
 *
 *   @Query("select m from MedicoEspecialista m where m.especialidade = :especialidade order by m.nome")
 *   List<MedicoEspecialista> buscarEspecialistas(Especialidade especialidade)
 *       -> "consulta dos profissionais por área de atuação"
 *          (especialidade só existe na subclasse, por isso precisa de @Query)
 *
 * FILTRAR POR CATEGORIA
 * Não precisa de query: o service faz findAll() e filtra por
 * p.getCategoria() == categoria. É polimorfismo e a lista é pequena.
 */
