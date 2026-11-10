package com.example.MedVita.controller;

import java.time.LocalDate;
import java.time.LocalTime;
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

import com.example.MedVita.dto.ConsultaRequest;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.Atendimento;
import com.example.MedVita.models.Consulta;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;
import com.example.MedVita.repository.ProfissionalSaudeRepository;
import com.example.MedVita.service.ConsultaService;
import com.example.MedVita.service.PacienteService;

/*
 * =========================================================
 * CONTROLLER DE CONSULTAS  (/consultas)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE É
 * @RestController @RequestMapping("/consultas"): devolve JSON.
 * As páginas HTML continuam sendo abertas pelo SecureLoginController e
 * chamam estas rotas com fetch (ver static/js/api.js).
 *
 * Emergência e cirurgia também ficam em /consultas, mas as rotas específicas
 * delas estão no EmergenciaCirurgiaController (Leonardo Gonzaga), em outro
 * arquivo. O Spring aceita dois controllers no mesmo @RequestMapping desde
 * que as rotas não sejam iguais: /consultas/emergencias (de lá) tem
 * prioridade sobre /consultas/{id} (daqui) porque é um caminho fixo.
 *
 * ATRIBUTOS: ConsultaService, PacienteService (paciente logado; já existe)
 *
 * ROTAS
 *   POST /consultas                         body: ConsultaRequest -> 201 + consulta criada
 *       Paciente logado (ROLE_USER): o pacienteId do body é TROCADO pelo id do
 *       paciente logado (pacienteService.buscarPorEmail(authentication.getName())).
 *       ADMIN pode agendar para qualquer paciente.
 *   GET  /consultas?tipo=ROTINA              todos os atendimentos, filtro opcional (MEDICO, ADMIN)
 *   GET  /consultas/{id}                     um atendimento (de qualquer modalidade)
 *   GET  /consultas/minhas                   do paciente logado (tela paciente/consultas)
 *   GET  /consultas/agenda?data=2026-11-10   do médico logado no dia (tela medico/consultas)
 *   GET  /consultas/profissionais-disponiveis?especialidade=CARDIOLOGIA&data=...&horario=...
 *                                            médicos livres no horário (select da tela de
 *                                            agendamento). Sem especialidade = clínicos gerais.
 *
 *   ROTAS GENÉRICAS (valem para as 4 modalidades, por polimorfismo)
 *   PUT  /consultas/{id}/iniciar             MEDICO
 *   PUT  /consultas/{id}/concluir            body: {"observacoes": "..."}  MEDICO
 *   PUT  /consultas/{id}/cancelar            paciente dono, MEDICO ou ADMIN
 *
 *   SÓ CONSULTAS
 *   PUT  /consultas/{id}/confirmar           paciente dono da consulta ou ADMIN
 *   PUT  /consultas/{id}/nao-compareceu      MEDICO ou ADMIN
 *   PUT  /consultas/{id}/encaminhar?especialidade=CARDIOLOGIA
 *                                            MEDICO (clínico geral da consulta)
 *   POST /consultas/{id}/reagendar           body: {"data": "...", "horario": "..."}
 *                                            -> 201 + a consulta NOVA
 *
 * BODIES PEQUENOS
 * Para {"observacoes"} e {"data","horario"} não precisa criar arquivo no
 * pacote dto: declarem records dentro do próprio controller, por exemplo
 *   record ConcluirRequest(String observacoes) {}
 *   record ReagendarRequest(LocalDate data, LocalTime horario) {}
 *
 * QUEM PODE
 * Usem @PreAuthorize("hasRole('MEDICO')") etc. nos métodos (precisa do
 * @EnableMethodSecurity no SecurityConfig; enquanto ele não estiver na main,
 * as anotações só são ignoradas, então não trava ninguém). Para "paciente
 * dono da consulta", comparem atendimento.getPaciente().getEmail() com
 * authentication.getName().
 *
 * ERROS
 * Regra quebrada (horário ocupado, mudança de situação proibida, médico
 * incompatível...) -> RegraNegocioException -> 400 com {"message": "..."}.
 * Id inexistente -> RecursoNaoEncontradoException -> 404.
 *
 * USA: ConsultaService
 */

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final PacienteService pacienteService;
    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public ConsultaController(ConsultaService consultaService, PacienteService pacienteService,
            ProfissionalSaudeRepository profissionalSaudeRepository) {
        this.consultaService = consultaService;
        this.pacienteService = pacienteService;
        this.profissionalSaudeRepository = profissionalSaudeRepository;
    }

    record ConcluirRequest(String observacoes) {}

    record ReagendarRequest(LocalDate data, LocalTime horario) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Consulta criar(@RequestBody ConsultaRequest req, Authentication authentication) {
        if (!isAdmin(authentication)) {
            req = req.comPaciente(pacienteLogado(authentication).getId());
        }
        return consultaService.criar(req);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public List<Atendimento> listar(@RequestParam(required = false) TipoAtendimento tipo) {
        return consultaService.listar(tipo);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('USER')")
    public List<Atendimento> minhas(Authentication authentication) {
        return consultaService.listarDoPaciente(pacienteLogado(authentication).getId());
    }

    @GetMapping("/agenda")
    @PreAuthorize("hasRole('MEDICO')")
    public List<Atendimento> agenda(@RequestParam(required = false) LocalDate data,
            Authentication authentication) {
        return consultaService.agendaDoProfissional(medicoLogado(authentication).getId(),
                data == null ? LocalDate.now() : data);
    }

    @GetMapping("/profissionais-disponiveis")
    public List<ProfissionalSaude> profissionaisDisponiveis(
            @RequestParam(required = false) Especialidade especialidade,
            @RequestParam(required = false) LocalDate data,
            @RequestParam(required = false) LocalTime horario) {
        return consultaService.profissionaisDisponiveis(especialidade, data, horario);
    }

    @GetMapping("/{id}")
    public Atendimento buscarPorId(@PathVariable Long id, Authentication authentication) {
        Atendimento atendimento = consultaService.buscarPorId(id);
        if (isPaciente(authentication)) {
            exigirDono(atendimento, authentication);
        }
        return atendimento;
    }

    @PutMapping("/{id}/iniciar")
    @PreAuthorize("hasRole('MEDICO')")
    public Atendimento iniciar(@PathVariable Long id) {
        return consultaService.iniciar(id);
    }

    @PutMapping("/{id}/concluir")
    @PreAuthorize("hasRole('MEDICO')")
    public Atendimento concluir(@PathVariable Long id, @RequestBody(required = false) ConcluirRequest body) {
        return consultaService.concluir(id, body == null ? null : body.observacoes());
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('USER', 'MEDICO', 'ADMIN')")
    public Atendimento cancelar(@PathVariable Long id, Authentication authentication) {
        if (isPaciente(authentication)) {
            exigirDono(consultaService.buscarPorId(id), authentication);
        }
        return consultaService.cancelar(id);
    }

    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Consulta confirmar(@PathVariable Long id, Authentication authentication) {
        if (isPaciente(authentication)) {
            exigirDono(consultaService.buscarPorId(id), authentication);
        }
        return consultaService.confirmar(id);
    }

    @PutMapping("/{id}/nao-compareceu")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public Consulta naoCompareceu(@PathVariable Long id) {
        return consultaService.registrarFalta(id);
    }

    @PutMapping("/{id}/encaminhar")
    @PreAuthorize("hasRole('MEDICO')")
    public Consulta encaminhar(@PathVariable Long id, @RequestParam Especialidade especialidade,
            Authentication authentication) {
        return consultaService.encaminhar(id, especialidade, authentication.getName());
    }

    @PostMapping("/{id}/reagendar")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Consulta reagendar(@PathVariable Long id, @RequestBody ReagendarRequest body,
            Authentication authentication) {
        if (isPaciente(authentication)) {
            exigirDono(consultaService.buscarPorId(id), authentication);
        }
        if (body == null) {
            throw new RegraNegocioException("Informe a nova data e o novo horário");
        }
        return consultaService.reagendar(id, body.data(), body.horario());
    }

    private Paciente pacienteLogado(Authentication authentication) {
        return pacienteService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RegraNegocioException("Paciente logado não encontrado"));
    }

    private ProfissionalSaude medicoLogado(Authentication authentication) {
        return profissionalSaudeRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RegraNegocioException("Profissional logado não encontrado"));
    }

    private void exigirDono(Atendimento atendimento, Authentication authentication) {
        if (!consultaService.pertenceAoPaciente(atendimento, authentication.getName())) {
            throw new AccessDeniedException("Esta consulta não é sua");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return temPapel(authentication, "ROLE_ADMIN");
    }

    private boolean isPaciente(Authentication authentication) {
        return temPapel(authentication, "ROLE_USER");
    }

    private boolean temPapel(Authentication authentication, String papel) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(papel));
    }
}
