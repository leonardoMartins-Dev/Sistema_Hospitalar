package com.example.MedVita.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.Atendimento;

/*
 * =========================================================
 * REPOSITÓRIO DE ATENDIMENTOS  (antigo ConsultaRepository)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE VAI SER
 * Uma interface, igual ao PacienteRepository, que enxerga as quatro
 * modalidades (a tabela é uma só, "atendimentos"). O findById já devolve a
 * subclasse certa: o Hibernate lê a coluna "tipo" e cria uma ConsultaRotina,
 * uma AtendimentoEmergencia etc., sem este repositório conhecer essas classes.
 *
 *   interface AtendimentoRepository extends JpaRepository<Atendimento, Long>
 *
 * Vai para a main LOGO NO COMEÇO, junto com a classe Atendimento: o
 * HistoricoMedicoService (Henrique) usa o findByPacienteId... daqui.
 *
 * MÉTODOS A MAIS
 *   List<Atendimento> findByPacienteIdOrderByDataDescHorarioDesc(Long pacienteId)
 *       -> atendimentos do paciente (tela paciente/consultas e histórico)
 *
 *   List<Atendimento> findByProfissionalIdAndDataOrderByHorario(Long profissionalId, LocalDate data)
 *       -> agenda do médico no dia (tela medico/consultas)
 *
 *   List<Atendimento> findByProfissionalIdAndDataAndHorario(Long profissionalId,
 *                                                           LocalDate data, LocalTime horario)
 *       -> REGRA 3: o service pega o que existe naquele horário e confere com
 *          .stream().anyMatch(Atendimento::ocupaAgenda)
 *          (cada modalidade diz se ocupa a agenda; sem SQL com status na mão)
 *
 * Emergência e cirurgia têm repositórios próprios (Leonardo Gonzaga):
 * AtendimentoEmergenciaRepository e ProcedimentoCirurgicoRepository.
 */

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByPacienteIdOrderByDataDescHorarioDesc(Long pacienteId);

    List<Atendimento> findByProfissionalIdAndDataOrderByHorario(Long profissionalId, LocalDate data);

    List<Atendimento> findByProfissionalIdAndDataAndHorario(Long profissionalId, LocalDate data,
            LocalTime horario);
}
