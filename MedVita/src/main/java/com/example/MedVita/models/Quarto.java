package com.example.MedVita.models;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.TipoQuarto;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * =========================================================
 * QUARTO
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
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

@Entity
@Table(name = "quartos")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private int andar;

    @Column(name = "capacidade_maxima", nullable = false)
    private int capacidadeMaxima;

    // Só muda por ocupar()/liberar(). READ_ONLY: o que vier no JSON do POST é ignorado.
    @Column(name = "ocupacao_atual", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int ocupacaoAtual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoQuarto tipo;

    // JPA e Jackson (POST /quartos) precisam do construtor vazio
    protected Quarto() {
    }

    public Quarto(String numero, int andar, int capacidadeMaxima, TipoQuarto tipo) {
        this.numero = numero;
        this.andar = andar;
        this.capacidadeMaxima = capacidadeMaxima;
        this.tipo = tipo;
        this.ocupacaoAtual = 0;
    }

    // REGRAS DO CADASTRO (os limites de leitos ficam no enum TipoQuarto)
    public void validar() {
        if (numero == null || numero.isBlank()) {
            throw new RegraNegocioException("Informe o número do quarto");
        }
        if (tipo == null) {
            throw new RegraNegocioException("Informe o tipo do quarto");
        }
        if (!tipo.aceitaCapacidade(capacidadeMaxima)) {
            throw new RegraNegocioException("Quarto do tipo " + tipo.getDescricao() + " deve ter de "
                    + tipo.getMinLeitos() + " a " + tipo.getMaxLeitos() + " leitos");
        }
    }

    public boolean temVaga() {
        return ocupacaoAtual < capacidadeMaxima;
    }

    public int getLeitosLivres() {
        return capacidadeMaxima - ocupacaoAtual;
    }

    // calculada, não é gravada no banco
    public String getSituacao() {
        return temVaga() ? "Disponível" : "Ocupado";
    }

    // OCUPA UM LEITO (RN4 e RN5). Quem chama: InternacaoService.internar
    public void ocupar() {
        if (!temVaga()) {
            throw new RegraNegocioException("Quarto " + numero + " está lotado");
        }
        ocupacaoAtual++;
    }

    // LIBERA UM LEITO. Quem chama: InternacaoService.darAlta
    public void liberar() {
        if (ocupacaoAtual == 0) {
            throw new RegraNegocioException("Quarto " + numero + " já está vazio");
        }
        ocupacaoAtual--;
    }

    // GETTERS E SETTERS (os setters servem ao Jackson no POST; não existe setOcupacaoAtual)
    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public int getAndar() { return andar; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public int getOcupacaoAtual() { return ocupacaoAtual; }
    public TipoQuarto getTipo() { return tipo; }

    public void setNumero(String numero) { this.numero = numero; }
    public void setAndar(int andar) { this.andar = andar; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public void setTipo(TipoQuarto tipo) { this.tipo = tipo; }
}
