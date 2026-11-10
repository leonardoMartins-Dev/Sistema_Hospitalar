package com.example.MedVita.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.ConsultaRotina;

/*
 * Repositório de consultas de rotina: busca o atendimento já com o tipo certo, sem
 * instanceof no service.
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2), ajuste da revisão final
 */
public interface ConsultaRotinaRepository extends JpaRepository<ConsultaRotina, Long> {}
