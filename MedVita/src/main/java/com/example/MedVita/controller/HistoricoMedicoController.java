package com.example.MedVita.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.models.HistoricoMedico;
import com.example.MedVita.service.HistoricoMedicoService;

/*
 * =========================================================
 * CONTROLLER DO HISTÓRICO MÉDICO  (/historico)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/historico"): devolve JSON.
 * Não está na lista de endpoints do PDF da Sprint 2, mas o histórico é a RN6
 * da Sprint 1 e a tela paciente/historico usa. O grupo decidiu manter.
 *
 * ROTAS
 *   GET /historico               -> histórico do paciente logado
 *                                   (tela paciente/historico)
 *                                   historicoMedicoService.doPacienteLogado(authentication.getName())
 *   GET /historico/{pacienteId}  -> histórico de um paciente (MEDICO e ADMIN)
 *
 * Só leitura: não existe POST, PUT nem DELETE aqui (RN6).
 * USA: HistoricoMedicoService
 */

@RestController
@RequestMapping("/historico")
public class HistoricoMedicoController {

    private final HistoricoMedicoService historicoMedicoService;

    public HistoricoMedicoController(HistoricoMedicoService historicoMedicoService) {
        this.historicoMedicoService = historicoMedicoService;
    }

    // histórico do paciente logado (tela paciente/historico)
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public HistoricoMedico meuHistorico(Authentication authentication) {
        return historicoMedicoService.doPacienteLogado(authentication.getName());
    }

    // histórico de um paciente qualquer (médico e admin)
    @GetMapping("/{pacienteId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public HistoricoMedico doPaciente(@PathVariable Long pacienteId) {
        return historicoMedicoService.doPaciente(pacienteId);
    }
}
