package com.example.MedVita.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MedVita.dto.CirurgiaRequest;
import com.example.MedVita.dto.EmergenciaRequest;
import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.AtendimentoEmergencia;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.models.ProcedimentoCirurgico;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.StatusEmergencia;
import com.example.MedVita.repository.AtendimentoEmergenciaRepository;
import com.example.MedVita.repository.PacienteRepository;
import com.example.MedVita.repository.ProcedimentoCirurgicoRepository;
import com.example.MedVita.repository.ProfissionalSaudeRepository;

/*
 * =========================================================
 * SERVIÇO DE EMERGÊNCIAS E PROCEDIMENTOS CIRÚRGICOS
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE É
 * O que é ESPECÍFICO de emergência e cirurgia: criar, assumir a emergência,
 * fila por prioridade e lista de procedimentos do cirurgião.
 * Iniciar, concluir e cancelar NÃO ficam aqui: as rotas genéricas do
 * ConsultaController (João Pedro) já fazem isso para qualquer atendimento,
 * chamando os métodos que AtendimentoEmergencia e ProcedimentoCirurgico
 * sobrescrevem. Assim este arquivo e o ConsultaService não se misturam.
 *
 * USA (só repositórios)
 *   AtendimentoEmergenciaRepository, ProcedimentoCirurgicoRepository  (desta parte)
 *   PacienteRepository           (já existe) -> findById
 *   ProfissionalSaudeRepository  (Leonardo Martins) -> findById
 *
 * MÉTODOS
 *   registrarEmergencia(req)             cria a emergência, ainda sem médico
 *   assumirEmergencia(id, profissional)  o médico assume e o atendimento começa
 *   filaEmergencia(status)               CRITICA primeiro, depois por chegada
 *   agendarCirurgia(req)                 valida, confere a RN3 e salva
 *   listarProcedimentosDoCirurgiao(id)   só para quem é cirurgião
 */
@Service
public class EmergenciaCirurgiaService {

    // CRITICA primeiro; empate: quem chegou antes
    private static final Comparator<AtendimentoEmergencia> ORDEM_DA_FILA =
            Comparator.comparing(AtendimentoEmergencia::getPrioridade).reversed()
                    .thenComparing(AtendimentoEmergencia::getData)
                    .thenComparing(AtendimentoEmergencia::getHorario);

    private final AtendimentoEmergenciaRepository emergenciaRepository;
    private final ProcedimentoCirurgicoRepository procedimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public EmergenciaCirurgiaService(
            AtendimentoEmergenciaRepository emergenciaRepository,
            ProcedimentoCirurgicoRepository procedimentoRepository,
            PacienteRepository pacienteRepository,
            ProfissionalSaudeRepository profissionalSaudeRepository) {
        this.emergenciaRepository = emergenciaRepository;
        this.procedimentoRepository = procedimentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalSaudeRepository = profissionalSaudeRepository;
    }

    // =========================================================
    // EMERGÊNCIA
    // =========================================================

    //REGISTRA A CHEGADA DO PACIENTE (SEM MÉDICO AINDA)
    @Transactional
    public AtendimentoEmergencia registrarEmergencia(EmergenciaRequest req) {
        Paciente paciente = buscarPaciente(req.pacienteId());

        AtendimentoEmergencia emergencia =
                new AtendimentoEmergencia(paciente, req.motivo(), req.prioridade());
        emergencia.validar();

        return emergenciaRepository.save(emergencia);
    }

    //UM MÉDICO ASSUME A EMERGÊNCIA E JÁ COMEÇA O ATENDIMENTO
    @Transactional
    public AtendimentoEmergencia assumirEmergencia(Long id, Long profissionalId) {
        AtendimentoEmergencia emergencia = emergenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Emergência " + id + " não encontrada"));

        ProfissionalSaude medico = buscarProfissional(profissionalId);

        emergencia.atribuirProfissional(medico); // o cirurgião é recusado aqui
        emergencia.iniciar();

        return emergenciaRepository.save(emergencia);
    }

    //FILA ORDENADA POR PRIORIDADE (SEM STATUS = QUEM AINDA AGUARDA)
    public List<AtendimentoEmergencia> filaEmergencia(StatusEmergencia status) {
        if (status == null) {
            status = StatusEmergencia.AGUARDANDO_AVALIACAO;
        }

        return emergenciaRepository.findByStatus(status).stream()
                .sorted(ORDEM_DA_FILA)
                .toList();
    }

    // =========================================================
    // CIRURGIA
    // =========================================================

    //AGENDA UM PROCEDIMENTO CIRÚRGICO
    @Transactional
    public ProcedimentoCirurgico agendarCirurgia(CirurgiaRequest req) {
        Paciente paciente = buscarPaciente(req.pacienteId());
        ProfissionalSaude cirurgiao = buscarProfissional(req.cirurgiaoId());

        ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico(
                paciente, cirurgiao, req.data(), req.horario(),
                req.descricaoProcedimento(), req.tipoCirurgia(),
                req.observacoesPreOperatorias());

        // só aceita Cirurgiao, data futura e campos obrigatórios
        procedimento.validar();

        // REGRA 3: o cirurgião não pode ter dois procedimentos no mesmo horário
        boolean horarioOcupado = procedimentoRepository
                .findByProfissionalIdAndDataAndHorario(cirurgiao.getId(), req.data(), req.horario())
                .stream()
                .anyMatch(ProcedimentoCirurgico::ocupaAgenda);

        if (horarioOcupado) {
            throw new RegraNegocioException("O cirurgião já tem procedimento neste horário");
        }

        return procedimentoRepository.save(procedimento);
    }

    //PROCEDIMENTOS DE UM CIRURGIÃO (MAIS RECENTES PRIMEIRO)
    public List<ProcedimentoCirurgico> listarProcedimentosDoCirurgiao(Long cirurgiaoId) {
        ProfissionalSaude profissional = buscarProfissional(cirurgiaoId);

        if (profissional.getCategoria() != CategoriaProfissional.CIRURGIAO) {
            throw new RegraNegocioException(profissional.getNome() + " não é cirurgião");
        }

        return procedimentoRepository.findByProfissionalIdOrderByDataDescHorarioDesc(cirurgiaoId);
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private Paciente buscarPaciente(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o paciente");
        }
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente " + id + " não encontrado"));
    }

    private ProfissionalSaude buscarProfissional(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o profissional");
        }
        return profissionalSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional " + id + " não encontrado"));
    }
}
