package com.example.MedVita.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.models.Quarto;
import com.example.MedVita.models.enums.TipoQuarto;
import com.example.MedVita.service.QuartoService;

/*
 * =========================================================
 * CONTROLLER DE QUARTOS  (/quartos)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/quartos"): devolve JSON.
 *
 * ROTAS
 *   GET  /quartos                          todos (tela medico/quartos)
 *   GET  /quartos?tipo=UTI                 filtro por tipo
 *   GET  /quartos?disponivel=true          só os com vaga (select da internação)
 *        (tipo e disponivel podem ir juntos; @RequestParam(required = false))
 *   GET  /quartos/tipos                    [{"valor": "UTI", "nome": "UTI"}, ...] (para o select)
 *   GET  /quartos/{id}                     um quarto
 *   POST /quartos                          body: {numero, andar, capacidadeMaxima, tipo} -> 201
 *
 * QUEM PODE: MEDICO e ADMIN.
 *
 * ERROS
 *   número repetido ou capacidade fora do limite do tipo -> 400 (RegraNegocioException)
 *
 * USA: QuartoService
 */

@RestController
@RequestMapping("/quartos")
@PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")   // vale para todas as rotas
public class QuartoController {

    private final QuartoService quartoService;

    public QuartoController(QuartoService quartoService) {
        this.quartoService = quartoService;
    }

    @GetMapping
    public List<Quarto> listar(@RequestParam(required = false) TipoQuarto tipo,
            @RequestParam(required = false) Boolean disponivel) {
        return quartoService.listar(tipo, disponivel);
    }

    // "/tipos" é caminho fixo, então o Spring escolhe esta rota antes da "/{id}"
    @GetMapping("/tipos")
    public List<Map<String, String>> tipos() {
        return quartoService.listarTipos();
    }

    @GetMapping("/{id}")
    public Quarto buscarPorId(@PathVariable Long id) {
        return quartoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quarto cadastrar(@RequestBody Quarto quarto) {
        return quartoService.cadastrar(quarto);
    }
}
