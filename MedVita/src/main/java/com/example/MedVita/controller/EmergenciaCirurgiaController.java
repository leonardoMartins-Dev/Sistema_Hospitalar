package com.example.MedVita.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.dto.CirurgiaRequest;
import com.example.MedVita.dto.EmergenciaRequest;
import com.example.MedVita.models.AtendimentoEmergencia;
import com.example.MedVita.models.ProcedimentoCirurgico;
import com.example.MedVita.models.enums.StatusEmergencia;
import com.example.MedVita.service.EmergenciaCirurgiaService;

/*
 * =========================================================
 * CONTROLLER DE EMERGÊNCIAS E CIRURGIAS  (também em /consultas)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O mesmo endereço do ConsultaController (decisão do grupo: tudo em
 * /consultas), mas em outro arquivo, para o João Pedro e o Leonardo Gonzaga
 * não mexerem no mesmo controller. Os caminhos fixos daqui
 * (/consultas/emergencias...) têm prioridade sobre o /consultas/{id} de lá.
 *
 * ROTAS
 *   POST /consultas/emergencias                    body: EmergenciaRequest -> 201
 *   GET  /consultas/emergencias?status=AGUARDANDO_AVALIACAO
 *   PUT  /consultas/emergencias/{id}/assumir?profissionalId=3
 *   POST /consultas/cirurgias                      body: CirurgiaRequest -> 201
 *   GET  /consultas/procedimentos?cirurgiaoId=9
 *
 * O QUE NÃO PRECISA FAZER AQUI
 * Iniciar a cirurgia, concluir (emergência e cirurgia) e cancelar a cirurgia
 * usam as rotas genéricas do ConsultaController:
 *   PUT /consultas/{id}/iniciar   PUT /consultas/{id}/concluir   PUT /consultas/{id}/cancelar
 */
@RestController
@RequestMapping("/consultas")
public class EmergenciaCirurgiaController {

    private final EmergenciaCirurgiaService emergenciaCirurgiaService;

    public EmergenciaCirurgiaController(EmergenciaCirurgiaService emergenciaCirurgiaService) {
        this.emergenciaCirurgiaService = emergenciaCirurgiaService;
    }

    // =========================================================
    // EMERGÊNCIAS
    // =========================================================

    @PostMapping("/emergencias")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public AtendimentoEmergencia registrarEmergencia(@RequestBody EmergenciaRequest req) {
        return emergenciaCirurgiaService.registrarEmergencia(req);
    }

    @GetMapping("/emergencias")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public List<AtendimentoEmergencia> filaEmergencia(
            @RequestParam(required = false) StatusEmergencia status) {
        return emergenciaCirurgiaService.filaEmergencia(status);
    }

    @PutMapping("/emergencias/{id}/assumir")
    @PreAuthorize("hasRole('MEDICO')")
    public AtendimentoEmergencia assumirEmergencia(
            @PathVariable Long id,
            @RequestParam Long profissionalId) {
        return emergenciaCirurgiaService.assumirEmergencia(id, profissionalId);
    }

    // =========================================================
    // CIRURGIAS
    // =========================================================

    @PostMapping("/cirurgias")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ProcedimentoCirurgico agendarCirurgia(@RequestBody CirurgiaRequest req) {
        return emergenciaCirurgiaService.agendarCirurgia(req);
    }

    @GetMapping("/procedimentos")
    public List<ProcedimentoCirurgico> listarProcedimentos(@RequestParam Long cirurgiaoId) {
        return emergenciaCirurgiaService.listarProcedimentosDoCirurgiao(cirurgiaoId);
    }
}
