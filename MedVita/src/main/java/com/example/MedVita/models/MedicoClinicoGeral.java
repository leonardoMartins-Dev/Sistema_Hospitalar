package com.example.MedVita.models;

/*
 * =========================================================
 * MÉDICO CLÍNICO GERAL  (subclasse de ProfissionalSaude)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Realiza consultas de rotina e avaliações iniciais dos pacientes.
 *   - Pode encaminhar pacientes para atendimento especializado.
 *   - Deve registrar as informações referentes às consultas realizadas.
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("CLINICO_GERAL")
 *   public class MedicoClinicoGeral extends ProfissionalSaude
 *
 *   Não tem atributo próprio: usa só os dados comuns da superclasse.
 *   Na tabela, especialidade e area_cirurgica ficam null.
 *
 * CONSTRUTORES
 *   protected MedicoClinicoGeral()   (JPA)
 *   public MedicoClinicoGeral(nome, registroProfissional, telefone, email, passwordHash)
 *       -> super(...)
 *
 * MÉTODOS
 *   getCategoria()   -> CategoriaProfissional.CLINICO_GERAL
 *   getAreaAtuacao() -> "Clínica geral"
 *   podeRealizar(TipoAtendimento tipo, Especialidade especialidade)
 *       -> tipo == ROTINA || tipo == EMERGENCIA   (a especialidade não importa)
 *   podeEncaminhar() -> true
 *
 * COMO O ENCAMINHAMENTO FUNCIONA
 * Quem grava o encaminhamento é a ConsultaRotina
 * (registrarEncaminhamento(especialidade, solicitante)), que pergunta
 * solicitante.podeEncaminhar(). Assim esta classe não precisa conhecer a
 * ConsultaRotina, e a sua parte não depende da parte de consultas.
 *
 * "Registrar as informações das consultas realizadas" já é garantido pela
 * Consulta: concluir(observacoes) exige observações (ver Consulta.java).
 */
