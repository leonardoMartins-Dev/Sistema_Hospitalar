package com.example.MedVita.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MedVita.dto.ConsultaRequest;
import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.Atendimento;
import com.example.MedVita.models.Consulta;
import com.example.MedVita.models.ConsultaEspecializada;
import com.example.MedVita.models.ConsultaRotina;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;
import com.example.MedVita.repository.AtendimentoRepository;
import com.example.MedVita.repository.PacienteRepository;
import com.example.MedVita.repository.ProfissionalSaudeRepository;

/*
 * =========================================================
 * SERVIÇO DE CONSULTAS
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * Cria as consultas (rotina e especializada) e cuida dos fluxos GENÉRICOS
 * de qualquer atendimento (iniciar, concluir, cancelar, buscar). Os fluxos
 * genéricos funcionam também para emergência e cirurgia SEM este service
 * conhecer essas classes: ele só chama os métodos abstratos de Atendimento.
 * O que é só de emergência ou cirurgia fica no EmergenciaCirurgiaService
 * (Leonardo Gonzaga), em outro arquivo.
 *
 * USA (só repositórios, para não depender do service de ninguém)
 *   AtendimentoRepository          (desta parte)
 *   PacienteRepository             (já existe) -> findById
 *   PacienteService.buscarPorEmail (já existe) -> paciente logado
 *   ProfissionalSaudeRepository    (Leonardo Martins) -> findById, findByEmail,
 *                                   buscarEspecialistas
 *
 * =========================================================
 * CRIAR UMA CONSULTA
 * =========================================================
 *   @Transactional
 *   Consulta criar(ConsultaRequest req)
 *     1. paciente = pacienteRepository.findById(req.pacienteId())
 *                   .orElseThrow(() -> new RecursoNaoEncontradoException(...))
 *        profissional = profissionalSaudeRepository.findById(...) (idem)
 *     2. Cria a subclasse certa (o único switch por tipo deste service):
 *          ROTINA        -> new ConsultaRotina(paciente, profissional, data, horario, motivo)
 *          ESPECIALIZADA -> new ConsultaEspecializada(..., req.especialidade())
 *          outro tipo    -> RegraNegocioException("Emergência e cirurgia usam
 *                           /consultas/emergencias e /consultas/cirurgias")
 *     3. consulta.validar()   (a modalidade confere as próprias regras)
 *     4. horarioOcupado(...) -> RegraNegocioException("O profissional já tem
 *        atendimento neste horário")   (RN3)
 *     5. return atendimentoRepository.save(consulta)
 *
 *   private boolean horarioOcupado(Long profissionalId, LocalDate data, LocalTime horario)
 *       atendimentoRepository.findByProfissionalIdAndDataAndHorario(...)
 *           .stream().anyMatch(Atendimento::ocupaAgenda)
 *
 * =========================================================
 * FLUXOS GENÉRICOS (valem para as 4 modalidades; nenhum if por tipo)
 * =========================================================
 * Todos são @Transactional, buscam com buscarPorId(id), chamam o método do
 * model e salvam. Quem decide se pode é o model, que lança
 * RegraNegocioException se a mudança não for permitida.
 *   iniciar(Long id)                       -> atendimento.iniciar()
 *   concluir(Long id, String observacoes)  -> atendimento.concluir(observacoes)
 *   cancelar(Long id)                      -> atendimento.cancelar()
 *
 * SÓ PARA CONSULTAS
 *   private Consulta comoConsulta(Atendimento a)
 *       if (!(a instanceof Consulta c)) lança RegraNegocioException(
 *           "Esta operação só vale para consultas");
 *   confirmar(Long id)          -> comoConsulta(a).confirmar()
 *   registrarFalta(Long id)     -> comoConsulta(a).registrarNaoComparecimento()
 *   reagendar(Long id, LocalDate data, LocalTime horario)
 *       Consulta nova = comoConsulta(a).reagendar(data, horario);
 *       nova.validar(); confere a RN3; salva a NOVA. A antiga não muda.
 *   encaminhar(Long id, Especialidade especialidade, String emailMedicoLogado)
 *       - o atendimento tem que ser ConsultaRotina; senão RegraNegocioException
 *       - solicitante = profissionalSaudeRepository.findByEmail(emailMedicoLogado)
 *       - rotina.registrarEncaminhamento(especialidade, solicitante); salva
 *         (o model confere se o solicitante é o médico da consulta e se
 *         solicitante.podeEncaminhar())
 *
 * =========================================================
 * LEITURAS
 * =========================================================
 *   Atendimento buscarPorId(Long id)  -> RecursoNaoEncontradoException se não existir
 *   List<Atendimento> listar(TipoAtendimento tipo)
 *       -> tipo null: todos; senão filtra por a.getTipo() == tipo
 *   List<Atendimento> listarDoPaciente(Long pacienteId)       -> tela paciente/consultas
 *   List<Atendimento> agendaDoProfissional(Long profissionalId, LocalDate data)
 *                                                             -> tela medico/consultas
 *   List<ProfissionalSaude> profissionaisDisponiveis(Especialidade especialidade,
 *                                                    LocalDate data, LocalTime horario)
 *       -> select de médicos da tela de agendamento ("profissionais disponíveis
 *          por área de atuação"):
 *          - especialidade null (consulta de rotina): profissionalSaudeRepository.findAll()
 *            filtrado por p.getCategoria() == CLINICO_GERAL
 *          - senão: profissionalSaudeRepository.buscarEspecialistas(especialidade)
 *          - tira quem está com horarioOcupado(p.getId(), data, horario)
 *          Fica aqui (e não em profissionais) porque quem conhece a agenda são
 *          os atendimentos.
 *
 * CONCORRÊNCIA
 * Se dois pedidos passarem juntos pela RN3, o índice único do banco recusa o
 * segundo com DataIntegrityViolationException. O GlobalExceptionHandler
 * transforma isso em 409 com uma mensagem amigável.
 */

@Service
public class ConsultaService {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public ConsultaService(AtendimentoRepository atendimentoRepository,
            PacienteRepository pacienteRepository,
            ProfissionalSaudeRepository profissionalSaudeRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalSaudeRepository = profissionalSaudeRepository;
    }

    @Transactional
    public Consulta criar(ConsultaRequest req) {
        if (req.tipo() == null) {
            throw new RegraNegocioException("Informe o tipo da consulta");
        }
        Paciente paciente = buscarPaciente(req.pacienteId());
        ProfissionalSaude profissional = buscarProfissional(req.profissionalId());

        Consulta consulta;
        switch (req.tipo()) {
            case ROTINA:
                consulta = new ConsultaRotina(paciente, profissional, req.data(), req.horario(), req.motivo());
                break;
            case ESPECIALIZADA:
                consulta = new ConsultaEspecializada(paciente, profissional, req.data(), req.horario(),
                        req.motivo(), req.especialidade());
                break;
            default:
                throw new RegraNegocioException(
                        "Emergência e cirurgia usam /consultas/emergencias e /consultas/cirurgias");
        }

        consulta.validar();
        verificarHorarioLivre(profissional.getId(), req.data(), req.horario());
        return atendimentoRepository.save(consulta);
    }

    @Transactional
    public Atendimento iniciar(Long id) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.iniciar();
        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public Atendimento concluir(Long id, String observacoes) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.concluir(observacoes);
        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public Atendimento cancelar(Long id) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.cancelar();
        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public Consulta confirmar(Long id) {
        Consulta consulta = comoConsulta(buscarPorId(id));
        consulta.confirmar();
        return atendimentoRepository.save(consulta);
    }

    @Transactional
    public Consulta registrarFalta(Long id) {
        Consulta consulta = comoConsulta(buscarPorId(id));
        consulta.registrarNaoComparecimento();
        return atendimentoRepository.save(consulta);
    }

    @Transactional
    public Consulta reagendar(Long id, LocalDate data, LocalTime horario) {
        Consulta nova = comoConsulta(buscarPorId(id)).reagendar(data, horario);
        nova.validar();
        verificarHorarioLivre(nova.getProfissional().getId(), data, horario);
        return atendimentoRepository.save(nova);
    }

    @Transactional
    public ConsultaRotina encaminhar(Long id, Especialidade especialidade, String emailMedicoLogado) {
        if (!(buscarPorId(id) instanceof ConsultaRotina rotina)) {
            throw new RegraNegocioException("Só consultas de rotina podem ter encaminhamento");
        }
        ProfissionalSaude solicitante = profissionalSaudeRepository.findByEmail(emailMedicoLogado)
                .orElseThrow(() -> new RegraNegocioException("Profissional logado não encontrado"));
        rotina.registrarEncaminhamento(especialidade, solicitante);
        return atendimentoRepository.save(rotina);
    }

    public Atendimento buscarPorId(Long id) {
        return atendimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento " + id + " não encontrado"));
    }

    public List<Atendimento> listar(TipoAtendimento tipo) {
        return atendimentoRepository.findAll().stream()
                .filter(a -> tipo == null || a.getTipo() == tipo)
                .toList();
    }

    public List<Atendimento> listarDoPaciente(Long pacienteId) {
        return atendimentoRepository.findByPacienteIdOrderByDataDescHorarioDesc(pacienteId);
    }

    public List<Atendimento> agendaDoProfissional(Long profissionalId, LocalDate data) {
        return atendimentoRepository.findByProfissionalIdAndDataOrderByHorario(profissionalId, data);
    }

    public List<ProfissionalSaude> profissionaisDisponiveis(Especialidade especialidade, LocalDate data,
            LocalTime horario) {
        Stream<ProfissionalSaude> candidatos = especialidade == null
                ? profissionalSaudeRepository.findAll().stream()
                        .filter(p -> p.getCategoria() == CategoriaProfissional.CLINICO_GERAL)
                : profissionalSaudeRepository.buscarEspecialistas(especialidade).stream()
                        .map(ProfissionalSaude.class::cast);

        if (data == null || horario == null) {
            return candidatos.toList();
        }
        return candidatos.filter(p -> !horarioOcupado(p.getId(), data, horario)).toList();
    }

    public boolean pertenceAoPaciente(Atendimento atendimento, String email) {
        return atendimento.getPaciente() != null
                && atendimento.getPaciente().getEmail().equalsIgnoreCase(email);
    }

    private Consulta comoConsulta(Atendimento atendimento) {
        if (!(atendimento instanceof Consulta consulta)) {
            throw new RegraNegocioException("Esta operação só vale para consultas");
        }
        return consulta;
    }

    private void verificarHorarioLivre(Long profissionalId, LocalDate data, LocalTime horario) {
        if (horarioOcupado(profissionalId, data, horario)) {
            throw new RegraNegocioException("O profissional já tem atendimento neste horário");
        }
    }

    private boolean horarioOcupado(Long profissionalId, LocalDate data, LocalTime horario) {
        return atendimentoRepository.findByProfissionalIdAndDataAndHorario(profissionalId, data, horario)
                .stream()
                .anyMatch(Atendimento::ocupaAgenda);
    }

    private Paciente buscarPaciente(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o paciente");
        }
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente " + id + " não encontrado"));
    }

    private ProfissionalSaude buscarProfissional(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o profissional");
        }
        return profissionalSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional " + id + " não encontrado"));
    }
}
