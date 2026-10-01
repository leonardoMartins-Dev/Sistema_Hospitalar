package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE QUARTOS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * USA: QuartoRepository
 *
 * MÉTODOS
 *   cadastrar(numero, andar, capacidadeMaxima)
 *       Número repetido ou capacidade menor que 1 -> RegraNegocioException.
 *       O quarto começa com ocupação 0.
 *   listar()              -> tela medico/quartos
 *   listarDisponiveis()   -> só os que têm vaga, para escolher na internação (RN4)
 *   buscarPorId(id)       -> RecursoNaoEncontradoException se não existir
 *
 * A ocupação NÃO muda aqui: só no InternacaoService (internar e dar alta).
 */
