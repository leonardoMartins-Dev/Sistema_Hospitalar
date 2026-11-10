package com.example.MedVita.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.dto.InternacaoRequest;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.Internacao;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.models.enums.StatusInternacao;
import com.example.MedVita.service.InternacaoService;
import com.example.MedVita.service.PacienteService;

/*
 * =========================================================
 * CONTROLLER DE INTERNAÇÕES  (/internacoes)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/internacoes"): devolve JSON.
 *
 * ROTAS
 *   POST /internacoes                body: InternacaoRequest -> 201   (MEDICO)
 *                                    (botão "Registrar Internação" de medico/internacoes)
 *   GET  /internacoes?status=ATIVA   quem está internado agora (MEDICO, ADMIN)
 *                                    sem status: todas
 *   GET  /internacoes/minhas         do paciente logado (tela paciente/internacoes)
 *   GET  /internacoes/{id}           uma internação
 *   PUT  /internacoes/{id}/alta      body: {"dataEfetivaAlta": "2026-11-15",
 *                                           "observacoes": "..."}   (MEDICO)
 *                                    record AltaRequest dentro do controller
 *
 * QUEM PODE: internar e dar alta, só MEDICO (@PreAuthorize).
 *
 * ERROS
 *   quarto lotado, paciente já internado, datas inválidas -> 400
 *   paciente, profissional ou quarto inexistente          -> 404
 *
 * USA: InternacaoService, PacienteService (paciente logado)
 */

@RestController
@RequestMapping("/internacoes")
public class InternacaoController {

    private final InternacaoService internacaoService;
    private final PacienteService pacienteService;

    public InternacaoController(InternacaoService internacaoService, PacienteService pacienteService) {
        this.internacaoService = internacaoService;
        this.pacienteService = pacienteService;
    }

    record AltaRequest(LocalDate dataEfetivaAlta, String observacoes) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEDICO')")
    public Internacao internar(@RequestBody InternacaoRequest req) {
        return internacaoService.internar(req);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public List<Internacao> listar(@RequestParam(required = false) StatusInternacao status) {
        return internacaoService.listar(status);
    }

    // "/minhas" é caminho fixo, então o Spring escolhe esta rota antes da "/{id}"
    @GetMapping("/minhas")
    @PreAuthorize("hasRole('USER')")
    public List<Internacao> minhas(Authentication authentication) {
        return internacaoService.listarDoPaciente(pacienteLogado(authentication).getId());
    }

    @GetMapping("/{id}")
    public Internacao buscarPorId(@PathVariable Long id, Authentication authentication) {
        Internacao internacao = internacaoService.buscarPorId(id);
        // o paciente só vê as próprias internações
        if (isPaciente(authentication)
                && !internacao.getPaciente().getId().equals(pacienteLogado(authentication).getId())) {
            throw new AccessDeniedException("Esta internação não é sua");
        }
        return internacao;
    }

    @PutMapping("/{id}/alta")
    @PreAuthorize("hasRole('MEDICO')")
    public Internacao darAlta(@PathVariable Long id, @RequestBody(required = false) AltaRequest body) {
        LocalDate data = (body == null || body.dataEfetivaAlta() == null) ? LocalDate.now() : body.dataEfetivaAlta();
        return internacaoService.darAlta(id, data, body == null ? null : body.observacoes());
    }

    private Paciente pacienteLogado(Authentication authentication) {
        return pacienteService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RegraNegocioException("Paciente logado não encontrado"));
    }

    private boolean isPaciente(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }
}
