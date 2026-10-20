package com.example.MedVita.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.ProcedimentoCirurgico;

/*
 * =========================================================
 * REPOSITÓRIO DE PROCEDIMENTOS CIRÚRGICOS
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * Repositório de subclasse, igual ao AtendimentoEmergenciaRepository: busca
 * só as linhas com tipo = 'CIRURGIA' da tabela "atendimentos".
 *
 * MÉTODOS A MAIS
 *   findByProfissionalIdOrderByDataDescHorarioDesc(cirurgiaoId)
 *       -> procedimentos de um cirurgião (GET /consultas/procedimentos?cirurgiaoId=)
 *
 *   findByProfissionalIdAndDataAndHorario(cirurgiaoId, data, horario)
 *       -> REGRA 3 ao agendar: o service confere com .anyMatch(ProcedimentoCirurgico::ocupaAgenda).
 *          O cirurgião só pode ter procedimentos (podeRealizar), então não
 *          existe consulta dele para dar conflito. O índice único do banco
 *          continua sendo a garantia final.
 */
public interface ProcedimentoCirurgicoRepository extends JpaRepository<ProcedimentoCirurgico, Long> {

    List<ProcedimentoCirurgico> findByProfissionalIdOrderByDataDescHorarioDesc(Long cirurgiaoId);

    List<ProcedimentoCirurgico> findByProfissionalIdAndDataAndHorario(Long cirurgiaoId,
            LocalDate data, LocalTime horario);

}
