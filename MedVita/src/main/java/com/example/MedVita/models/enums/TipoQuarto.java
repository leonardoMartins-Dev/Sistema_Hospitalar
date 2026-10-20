package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: TIPO DE QUARTO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * A entrega 4 da Sprint 2 pede regras para os "tipos de quartos". O grupo
 * decidiu: cada tipo tem um mínimo e um máximo de leitos, e o cadastro do
 * quarto respeita esse limite.
 *
 * VALORES (descricao, minLeitos, maxLeitos)
 *   ENFERMARIA("Enfermaria", 2, 6)     quarto coletivo
 *   APARTAMENTO("Apartamento", 1, 2)   quarto privativo
 *   UTI("UTI", 1, 10)                  terapia intensiva
 *
 * ATRIBUTOS: String descricao, int minLeitos, int maxLeitos (+ getters)
 *
 * MÉTODO boolean aceitaCapacidade(int capacidade)
 *   -> capacidade >= minLeitos && capacidade <= maxLeitos
 *   Quem usa: Quarto.validar(), no cadastro do quarto.
 *
 * Também serve de filtro: GET /quartos?tipo=UTI&disponivel=true
 * A lista para o select da tela sai em GET /quartos/tipos.
 */
