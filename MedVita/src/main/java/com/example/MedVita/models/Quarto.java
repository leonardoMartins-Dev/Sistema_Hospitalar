package com.example.MedVita.models;

/*
 * =========================================================
 * QUARTO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * Um quarto do hospital: número, andar, capacidade máxima e situação atual.
 *
 * O QUE MUDOU NA SPRINT 2
 *   - Ganhou o TIPO (enum TipoQuarto: ENFERMARIA, APARTAMENTO, UTI). Cada
 *     tipo tem um limite de leitos que vale no cadastro (regra dos "tipos de
 *     quartos" da entrega 4).
 *   - Ocupar e liberar leito viraram métodos do próprio Quarto, como nos
 *     cartões CRC ("ocupar um leito", "liberar um leito").
 *
 * TABELA quartos
 *   id                 bigint  gerado pelo banco
 *   numero             text    obrigatório e único
 *   andar              int     obrigatório
 *   capacidade_maxima  int     obrigatório (maior que zero)
 *   ocupacao_atual     int     começa em 0
 *   tipo               text    NOVO: ENFERMARIA | APARTAMENTO | UTI
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 *   @Entity @Table(name = "quartos")
 *
 * ATRIBUTOS
 *   Long id
 *   String numero
 *   int andar
 *   int capacidadeMaxima    @Column(name = "capacidade_maxima")
 *   int ocupacaoAtual       @Column(name = "ocupacao_atual"), começa em 0
 *   TipoQuarto tipo         @Enumerated(EnumType.STRING)
 *   NÃO criem setOcupacaoAtual público: a ocupação só muda por ocupar()/liberar().
 *
 * MÉTODOS
 *   validar()            chamado pelo QuartoService.cadastrar
 *       - tipo obrigatório
 *       - !tipo.aceitaCapacidade(capacidadeMaxima) -> RegraNegocioException(
 *         "Quarto do tipo Apartamento deve ter de 1 a 2 leitos")
 *         (os limites ficam no enum TipoQuarto)
 *
 *   boolean temVaga()      -> ocupacaoAtual < capacidadeMaxima
 *   int getLeitosLivres()  -> capacidadeMaxima - ocupacaoAtual
 *   String getSituacao()   -> "Disponível" se temVaga(), senão "Ocupado"
 *                             (calculada, não é gravada no banco)
 *
 *   ocupar()    sem vaga -> RegraNegocioException("Quarto 305 está lotado")  (RN4 e RN5)
 *               senão ocupacaoAtual++
 *   liberar()   se ocupacaoAtual == 0 -> RegraNegocioException (seria um erro de dados)
 *               senão ocupacaoAtual--
 *   Quem chama ocupar/liberar: só o InternacaoService (internar e dar alta).
 *
 * JSON
 *   O POST /quartos recebe {numero, andar, capacidadeMaxima, tipo}. Dá para
 *   usar o próprio Quarto no @RequestBody, porque ele não tem relacionamento.
 *   No GET saem também "situacao" e "leitosLivres" (pelos getters).
 *
 * TELA: medico/quartos (coluna Tipo e filtro por tipo)
 */
