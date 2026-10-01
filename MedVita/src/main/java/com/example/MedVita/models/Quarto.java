package com.example.MedVita.models;

/*
 * =========================================================
 * QUARTO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * O QUE É
 * Um quarto do hospital. Requisito 5 do enunciado: número, andar, capacidade
 * máxima e situação atual (disponível ou ocupado).
 *
 * TABELA NO SUPABASE: quartos  (já criada pelo supabase/schema.sql)
 *   id                 bigint  gerado pelo banco
 *   numero             text    obrigatório e único
 *   andar              int     obrigatório
 *   capacidade_maxima  int     obrigatório (maior que zero)
 *   ocupacao_atual     int     quantos pacientes estão nele agora (começa em 0)
 *
 * SITUAÇÃO ATUAL
 * Não é gravada: é calculada a partir dos números.
 *   getSituacao() -> "Disponível" se ocupacaoAtual < capacidadeMaxima
 *                    "Ocupado"    se ocupacaoAtual == capacidadeMaxima
 *
 * REGRA DE NEGÓCIO 5
 * A ocupação nunca passa da capacidade. Ela só muda quando alguém é internado
 * (+1) ou recebe alta (-1), sempre pelo InternacaoService.
 *
 * TELA: medico/quartos
 */
