package com.example.MedVita.models;

/*
 * =========================================================
 * INTERNAÇÃO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * O período em que um paciente fica internado num quarto: paciente,
 * profissional responsável, quarto, data de entrada, data prevista de alta,
 * data efetiva de alta e observações.
 *
 * O QUE MUDOU NA SPRINT 2
 * O enunciado fala nas "situações pelas quais uma consulta ou internação pode
 * passar". A internação ganhou o enum StatusInternacao (ATIVA | ALTA) no
 * lugar de "está internado se dataEfetivaAlta for null".
 *
 * TABELA internacoes
 *   id                  bigint  gerado pelo banco
 *   paciente_id         bigint  obrigatório -> pacientes(id)               (RN4)
 *   profissional_id     bigint  obrigatório -> profissionais_saude(id)
 *   quarto_id           bigint  obrigatório -> quartos(id)                 (RN4)
 *   data_entrada        date    obrigatório
 *   data_prevista_alta  date
 *   data_efetiva_alta   date    preenchida na alta
 *   observacoes         text
 *   status              text    NOVO: ATIVA | ALTA (começa ATIVA)
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 *   @Entity @Table(name = "internacoes")
 *
 * ATRIBUTOS
 *   Long id
 *   Paciente paciente                @ManyToOne(optional = false)
 *   ProfissionalSaude profissional   @ManyToOne(optional = false)
 *        qualquer categoria pode ser responsável (o enunciado não restringe)
 *   Quarto quarto                    @ManyToOne(optional = false)
 *   LocalDate dataEntrada, dataPrevistaAlta, dataEfetivaAlta
 *   String observacoes
 *   StatusInternacao status          @Enumerated(STRING), começa ATIVA
 *
 * CONSTRUTORES
 *   protected Internacao()   (JPA)
 *   public Internacao(paciente, profissional, quarto, dataEntrada, dataPrevistaAlta, observacoes)
 *
 * MÉTODOS
 *   validar()
 *       - dataEntrada obrigatória
 *       - dataPrevistaAlta (se tiver) não pode ser antes da dataEntrada
 *   boolean estaInternado()  -> status == ATIVA
 *   darAlta(LocalDate dataEfetivaAlta, String observacoes)
 *       - só se ATIVA; senão RegraNegocioException("Esta internação já foi encerrada")
 *       - dataEfetivaAlta não pode ser antes da dataEntrada
 *       - grava a data, acrescenta as observações e muda o status para ALTA
 *       - quem libera o leito (quarto.liberar()) é o InternacaoService, na
 *         mesma transação
 *
 * TELAS: medico/internacoes, paciente/internacoes
 */
