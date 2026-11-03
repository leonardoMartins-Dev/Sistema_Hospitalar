package com.example.MedVita.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.AtendimentoEmergencia;
import com.example.MedVita.models.enums.StatusEmergencia;

/*
 * =========================================================
 * REPOSITÓRIO DE ATENDIMENTOS DE EMERGÊNCIA
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O Spring Data aceita repositório de uma SUBCLASSE: ele busca na mesma
 * tabela "atendimentos", só nas linhas com tipo = 'EMERGENCIA'. Com isso dá
 * para usar os campos que só existem aqui (status, prioridade) direto no nome
 * do método, sem @Query, e sem mexer no AtendimentoRepository do João Pedro.
 *
 * MÉTODOS A MAIS
 *   findByStatus(status) -> fila de emergência. A ordenação por prioridade é
 *   feita em Java (ver enum Prioridade): o banco ordenaria pelo texto.
 */
public interface AtendimentoEmergenciaRepository extends JpaRepository<AtendimentoEmergencia, Long> {

    List<AtendimentoEmergencia> findByStatus(StatusEmergencia status);

}
