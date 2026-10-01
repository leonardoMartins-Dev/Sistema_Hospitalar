package com.example.MedVita.models;

/*
 * =========================================================
 * CONSULTA
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * Um atendimento marcado entre um paciente e um profissional da saúde.
 * Requisito 3 do enunciado: paciente, profissional responsável, data,
 * horário, motivo e observações médicas.
 *
 * TABELA NO SUPABASE: consultas  (já criada pelo supabase/schema.sql)
 *   id               bigint  gerado pelo banco
 *   paciente_id      bigint  obrigatório, aponta para pacientes(id)           (RN2)
 *   profissional_id  bigint  obrigatório, aponta para profissionais_saude(id) (RN2)
 *   data             date    obrigatório
 *   horario          time    obrigatório
 *   motivo           text    obrigatório (o paciente escreve ao agendar)
 *   observacoes      text    observações médicas (o médico escreve ao atender)
 *   status           text    "AGENDADA", "REALIZADA" ou "CANCELADA"
 *
 * NO JAVA
 *   Paciente paciente                @ManyToOne
 *   ProfissionalSaude profissional   @ManyToOne
 *   LocalDate data, LocalTime horario, String motivo, observacoes, status
 *
 * REGRA DE NEGÓCIO 3
 * Um profissional não pode ter duas consultas no mesmo dia e horário. Quem
 * confere é o ConsultaService, antes de salvar (consulta cancelada não conta).
 *
 * TELAS: paciente/agendar-consulta, paciente/consultas, medico/consultas
 */
