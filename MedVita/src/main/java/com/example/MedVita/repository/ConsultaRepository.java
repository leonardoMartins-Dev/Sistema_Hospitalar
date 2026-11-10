package com.example.MedVita.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.Consulta;

/*
 * Repositório de consultas: busca o atendimento já com o tipo certo, sem
 * instanceof no service.
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2), ajuste da revisão final
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {}
