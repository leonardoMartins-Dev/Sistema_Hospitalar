package com.example.MedVita.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.Internacao;
import com.example.MedVita.models.enums.StatusInternacao;

/*
 * =========================================================
 * REPOSITÓRIO DE INTERNAÇÕES
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
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

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

    List<Internacao> findAllByOrderByDataEntradaDesc();

    List<Internacao> findByPacienteIdOrderByDataEntradaDesc(Long pacienteId);

    List<Internacao> findByStatusOrderByDataEntradaDesc(StatusInternacao status);

    boolean existsByPacienteIdAndStatus(Long pacienteId, StatusInternacao status);

    boolean existsByQuartoIdAndStatus(Long quartoId, StatusInternacao status);
}
