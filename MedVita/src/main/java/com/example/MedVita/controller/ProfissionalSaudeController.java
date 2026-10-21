package com.example.MedVita.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.dto.ProfissionalRequest;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.service.ProfissionalSaudeService;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSaudeController {

    private final ProfissionalSaudeService profissionalSaudeService;

    public ProfissionalSaudeController(ProfissionalSaudeService profissionalSaudeService) {
        this.profissionalSaudeService = profissionalSaudeService;
    }

    // GET /profissionais, /profissionais?categoria=CIRURGIAO, /profissionais?especialidade=CARDIOLOGIA
    @GetMapping
    public List<ProfissionalSaude> listar(
            @RequestParam(name = "categoria", required = false) CategoriaProfissional categoria,
            @RequestParam(name = "especialidade", required = false) Especialidade especialidade) {
        return profissionalSaudeService.buscar(categoria, especialidade);
    }

    @GetMapping("/especialidades")
    public List<Map<String, String>> especialidades() {
        return profissionalSaudeService.listarEspecialidades();
    }

    @GetMapping("/{id}")
    public ProfissionalSaude buscarPorId(@PathVariable Long id) {
        return profissionalSaudeService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfissionalSaude> cadastrar(@RequestBody ProfissionalRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profissionalSaudeService.cadastrar(req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        profissionalSaudeService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

/*
 * =========================================================
 * CONTROLLER DE PROFISSIONAIS DA SAÚDE  (/profissionais)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/profissionais"): devolve JSON.
 *
 * ATRIBUTO: ProfissionalSaudeService (construtor). Só ele: esta parte não
 * depende de nenhuma outra.
 *
 * ROTAS
 *   GET    /profissionais                              todos (tela admin/medicos)
 *   GET    /profissionais?categoria=CLINICO_GERAL      filtro por categoria
 *   GET    /profissionais?especialidade=CARDIOLOGIA    especialistas da área
 *          Os filtros são @RequestParam(required = false) e vão para
 *          profissionalSaudeService.buscar(categoria, especialidade).
 *   GET    /profissionais/especialidades               [{"valor": "CARDIOLOGIA", "nome": "Cardiologia"}, ...]
 *                                                      (para os selects)
 *   GET    /profissionais/{id}                         um profissional
 *   POST   /profissionais                              body: ProfissionalRequest -> 201   (ADMIN)
 *   DELETE /profissionais/{id}                         -> 204                            (ADMIN)
 *
 * O que NÃO fica aqui (para esta parte não depender das outras):
 *   médicos livres num horário -> GET /consultas/profissionais-disponiveis (João Pedro)
 *   procedimentos do cirurgião -> GET /consultas/procedimentos?cirurgiaoId= (Leonardo Gonzaga)
 *
 * QUEM PODE: listar, qualquer usuário logado; cadastrar e remover, só ADMIN
 * (@PreAuthorize("hasRole('ADMIN')")).
 *
 * JSON DE SAÍDA
 * Cada profissional sai com os campos da subclasse dele, mais "categoria" e
 * "areaAtuacao". O passwordHash nunca sai (@JsonIgnore em ProfissionalSaude).
 */
