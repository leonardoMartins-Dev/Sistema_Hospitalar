package com.example.MedVita.models;

/*
 * =========================================================
 * INTERNAÇÃO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * O período em que um paciente fica internado num quarto.
 * Requisito 4 do enunciado: paciente, profissional responsável, quarto, data
 * de entrada, data prevista de alta, data efetiva de alta e observações.
 *
 * TABELA NO SUPABASE: internacoes  (já criada pelo supabase/schema.sql)
 *   id                  bigint  gerado pelo banco
 *   paciente_id         bigint  obrigatório, aponta para pacientes(id)           (RN4)
 *   profissional_id     bigint  obrigatório, aponta para profissionais_saude(id)
 *   quarto_id           bigint  obrigatório, aponta para quartos(id)             (RN4)
 *   data_entrada        date    obrigatório
 *   data_prevista_alta  date
 *   data_efetiva_alta   date    fica vazia (null) enquanto o paciente está internado
 *   observacoes         text
 *
 * NO JAVA
 *   Paciente paciente, ProfissionalSaude profissional, Quarto quarto   @ManyToOne
 *   LocalDate dataEntrada, dataPrevistaAlta, dataEfetivaAlta
 *   String observacoes
 *   estaInternado() -> true enquanto dataEfetivaAlta for null
 *
 * TELAS: medico/internacoes, paciente/internacoes
 */
