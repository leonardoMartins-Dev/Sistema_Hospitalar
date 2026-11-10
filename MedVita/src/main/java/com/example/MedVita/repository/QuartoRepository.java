package com.example.MedVita.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MedVita.models.Quarto;
import com.example.MedVita.models.enums.TipoQuarto;

import jakarta.persistence.LockModeType;

/*
 * =========================================================
 * REPOSITÓRIO DE QUARTOS
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE VAI SER
 *   interface QuartoRepository extends JpaRepository<Quarto, Long>
 *
 * MÉTODOS A MAIS
 *   boolean existsByNumero(String numero)
 *       -> não deixar cadastrar número repetido
 *
 *   List<Quarto> findAllByOrderByAndarAscNumeroAsc()
 *       -> lista da tela medico/quartos, em ordem
 *
 *   List<Quarto> findByTipoOrderByAndarAscNumeroAsc(TipoQuarto tipo)
 *       -> filtro por tipo
 *
 *   @Query("select q from Quarto q where q.ocupacaoAtual < q.capacidadeMaxima order by q.andar, q.numero")
 *   List<Quarto> buscarDisponiveis()
 *       -> quartos com vaga (select da tela de internação). Precisa de @Query
 *          porque compara duas colunas entre si.
 *
 *   @Lock(LockModeType.PESSIMISTIC_WRITE)
 *   @Query("select q from Quarto q where q.id = :id")
 *   Optional<Quarto> buscarParaAtualizar(Long id)
 *       -> usado SÓ pelo InternacaoService ao internar/dar alta. Trava a linha
 *          do quarto (SELECT ... FOR UPDATE) até o fim da transação, para duas
 *          internações ao mesmo tempo não ocuparem o mesmo último leito
 *          (é o que a JustificativaBancoRelacional promete na RN5).
 */

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    boolean existsByNumero(String numero);

    List<Quarto> findAllByOrderByAndarAscNumeroAsc();

    List<Quarto> findByTipoOrderByAndarAscNumeroAsc(TipoQuarto tipo);

    // compara duas colunas entre si, por isso precisa de @Query
    @Query("select q from Quarto q where q.ocupacaoAtual < q.capacidadeMaxima order by q.andar, q.numero")
    List<Quarto> buscarDisponiveis();

    // SELECT ... FOR UPDATE: trava a linha até o fim da transação (RN5)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Quarto q where q.id = :id")
    Optional<Quarto> buscarParaAtualizar(@Param("id") Long id);
}
