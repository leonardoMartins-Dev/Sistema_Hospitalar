package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE QUARTOS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * USA: QuartoRepository
 *
 * MÉTODOS
 *   Quarto cadastrar(Quarto quarto)
 *       - número repetido -> RegraNegocioException
 *       - quarto.validar()   (tipo obrigatório e limite de leitos do tipo)
 *       - a ocupação começa em 0 (ignore o que vier no JSON)
 *       - save
 *   List<Quarto> listar(TipoQuarto tipo, Boolean disponivel)
 *       - tipo informado -> findByTipoOrderByAndarAscNumeroAsc(tipo)
 *       - senão          -> findAllByOrderByAndarAscNumeroAsc()
 *       - disponivel = true -> filtra por quarto.temVaga()
 *   List<Quarto> listarDisponiveis()   -> repository.buscarDisponiveis() (RN4)
 *   Quarto buscarPorId(Long id)        -> RecursoNaoEncontradoException
 *   List<TipoQuarto> listarTipos()     -> Arrays.asList(TipoQuarto.values())
 *
 * A ocupação NÃO muda aqui: só pelo InternacaoService (internar e dar alta),
 * chamando quarto.ocupar() e quarto.liberar().
 */
