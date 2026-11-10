package com.example.MedVita.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.Quarto;
import com.example.MedVita.models.enums.TipoQuarto;
import com.example.MedVita.repository.QuartoRepository;

/*
 * =========================================================
 * SERVIÇO DE QUARTOS
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
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
 *   List<Map<String, String>> listarTipos()
 *       -> [{"valor": "UTI", "nome": "UTI"}, ...] para o select ("valor" vai de volta
 *          no POST; "nome" é o que aparece, vindo de getDescricao())
 *
 * A ocupação NÃO muda aqui: só pelo InternacaoService (internar e dar alta),
 * chamando quarto.ocupar() e quarto.liberar().
 */

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;

    public QuartoService(QuartoRepository quartoRepository) {
        this.quartoRepository = quartoRepository;
    }

    @Transactional
    public Quarto cadastrar(Quarto dados) {
        if (dados == null) {
            throw new RegraNegocioException("Informe os dados do quarto");
        }
        String numero = dados.getNumero() == null ? null : dados.getNumero().trim();
        // objeto novo: a ocupação começa em 0, ignorando o que veio no JSON
        Quarto quarto = new Quarto(numero, dados.getAndar(), dados.getCapacidadeMaxima(), dados.getTipo());
        quarto.validar();
        if (quartoRepository.existsByNumero(numero)) {
            throw new RegraNegocioException("Já existe um quarto com o número " + numero);
        }
        return quartoRepository.save(quarto);
    }

    public List<Quarto> listar(TipoQuarto tipo, Boolean disponivel) {
        List<Quarto> quartos = tipo != null
                ? quartoRepository.findByTipoOrderByAndarAscNumeroAsc(tipo)
                : quartoRepository.findAllByOrderByAndarAscNumeroAsc();
        if (Boolean.TRUE.equals(disponivel)) {
            return quartos.stream().filter(Quarto::temVaga).toList();
        }
        return quartos;
    }

    // RN4: só quartos com vaga podem receber internação
    public List<Quarto> listarDisponiveis() {
        return quartoRepository.buscarDisponiveis();
    }

    public Quarto buscarPorId(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto " + id + " não encontrado"));
    }

    // [{"valor": "UTI", "nome": "UTI", "leitos": "1 a 10 leitos"}, ...] para o select da tela
    public List<Map<String, String>> listarTipos() {
        return Arrays.stream(TipoQuarto.values())
                .map(t -> {
                    Map<String, String> item = new LinkedHashMap<>();
                    item.put("valor", t.name());
                    item.put("nome", t.getDescricao());
                    item.put("leitos", t.getMinLeitos() + " a " + t.getMaxLeitos() + " leitos");
                    return item;
                })
                .toList();
    }
}
