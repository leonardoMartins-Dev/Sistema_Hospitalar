package com.example.MedVita.models;

/*
 * =========================================================
 * PROFISSIONAL DA SAÚDE
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * Quem atende no hospital (o médico). Requisito 2 do enunciado: nome,
 * registro profissional, especialidade, telefone e e-mail.
 * Quem cadastra é o ADMIN, pela página admin/medicos, que já existe.
 *
 * TABELA NO SUPABASE: profissionais_saude  (já criada pelo supabase/schema.sql)
 *   id                     bigint   gerado pelo banco
 *   nome                   text     obrigatório
 *   registro_profissional  text     obrigatório e único (ex.: CRM 123456-MG)
 *   especialidade          text     obrigatório (ex.: "Cardiologia")
 *   telefone               text     obrigatório
 *   email                  text     obrigatório e único (é o login do médico)
 *   password_hash          text     senha do médico, em BCrypt
 *
 * LOGIN DO MÉDICO
 * O médico entra pela mesma tela de login. O admin define a senha inicial no
 * cadastro (falta um campo "senha" no formulário de admin/medicos). O
 * DatabaseUserDetailsService passa a procurar o e-mail também nesta tabela
 * e, se achar, dá o papel MEDICO (o médico vai para /medico).
 *
 * RELACIONAMENTOS
 *   1 profissional -> várias consultas   (RN2)
 *   1 profissional -> várias internações (como responsável)
 */
