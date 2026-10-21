package com.example.MedVita.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public List<Paciente> listar(@RequestParam(name = "busca", required = false) String busca) {
        return pacienteService.buscar(busca);
    }

    // "/perfil" é um caminho fixo, então o Spring escolhe esta rota antes da "/{id}"
    @GetMapping("/perfil")
    @PreAuthorize("hasRole('USER')")
    public Paciente perfil(Authentication authentication) {
        return pacienteService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public Paciente buscarPorId(@PathVariable Long id) {
        return pacienteService.buscarPorId(id);
    }
}

/*
 * =========================================================
 * CONTROLLER DE PACIENTES  (/pacientes)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/pacientes"): devolve JSON.
 * O cadastro do paciente continua sendo pela página /register
 * (SecureLoginController). Aqui fica só a consulta dos pacientes.
 *
 * ATRIBUTO
 *   private final PacienteService pacienteService;   (recebido no construtor)
 *
 * ROTAS (um método para cada)
 *   GET /pacientes?busca=texto
 *       -> lista da tela medico/pacientes.
 *          Sem "busca": pacienteService.listar().
 *          Com "busca": pacienteService.buscar(texto) (nome, CPF ou e-mail).
 *   GET /pacientes/perfil
 *       -> o paciente logado (tela paciente/perfil):
 *          pacienteService.buscarPorEmail(authentication.getName()).
 *   GET /pacientes/{id}
 *       -> um paciente (botão "Ver ficha"): pacienteService.buscarPorId(id).
 *
 * QUEM PODE
 *   A lista e a busca por id: só MEDICO e ADMIN.
 *   O perfil: o próprio paciente (ROLE_USER).
 *
 * ATENÇÃO: antes de criar este controller, colocar @JsonIgnore no
 * passwordHash do Paciente, para a senha nunca aparecer no JSON.
 */
