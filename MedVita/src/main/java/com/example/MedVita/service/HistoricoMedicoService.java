package com.example.MedVita.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.models.Atendimento;
import com.example.MedVita.models.HistoricoMedico;
import com.example.MedVita.models.Internacao;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.repository.AtendimentoRepository;
import com.example.MedVita.repository.InternacaoRepository;
import com.example.MedVita.repository.PacienteRepository;

/*
 * =========================================================
 * SERVIÇO DO HISTÓRICO MÉDICO
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * USA
 *   InternacaoRepository                     (desta parte)
 *   PacienteRepository, PacienteService.buscarPorEmail   (já existem)
 *   AtendimentoRepository                    (João Pedro)
 *
 * FAÇA POR ÚLTIMO: é a única coisa desta parte que depende de outra pessoa
 * (precisa da classe Atendimento e do AtendimentoRepository do João Pedro na
 * main, o que acontece logo no começo).
 *
 * MÉTODOS
 *   HistoricoMedico doPaciente(Long pacienteId)
 *     1. paciente = pacienteRepository.findById(pacienteId)
 *        (RecursoNaoEncontradoException se não existir)
 *     2. atendimentos = atendimentoRepository.findByPacienteIdOrderByDataDescHorarioDesc(id)
 *            .stream().filter(Atendimento::isConcluido).toList()
 *        isConcluido() é polimórfico: cada modalidade sabe quando está
 *        concluída (CONCLUIDA na consulta, CONCLUIDO na emergência e na cirurgia).
 *     3. internacoes = internacaoRepository.findByPacienteIdOrderByDataEntradaDesc(id)
 *     4. return new HistoricoMedico(paciente, atendimentos, internacoes)
 *
 *   HistoricoMedico doPacienteLogado(String email)
 *     -> pacienteService.buscarPorEmail(email) (já existe) e chama doPaciente(id)
 *
 * Só leitura: o histórico nunca é editado nem apagado (RN6).
 * Quem pode ver: o próprio paciente, os médicos e o admin.
 */

@Service
public class HistoricoMedicoService {

    private final PacienteRepository pacienteRepository;
    private final PacienteService pacienteService;
    private final AtendimentoRepository atendimentoRepository;
    private final InternacaoRepository internacaoRepository;

    public HistoricoMedicoService(PacienteRepository pacienteRepository, PacienteService pacienteService,
            AtendimentoRepository atendimentoRepository, InternacaoRepository internacaoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.pacienteService = pacienteService;
        this.atendimentoRepository = atendimentoRepository;
        this.internacaoRepository = internacaoRepository;
    }

    @Transactional(readOnly = true)
    public HistoricoMedico doPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente " + pacienteId + " não encontrado"));

        // isConcluido() é polimórfico: cada modalidade sabe quando está concluída
        List<Atendimento> atendimentos = atendimentoRepository
                .findByPacienteIdOrderByDataDescHorarioDesc(pacienteId)
                .stream()
                .filter(Atendimento::isConcluido)
                .toList();

        List<Internacao> internacoes = internacaoRepository.findByPacienteIdOrderByDataEntradaDesc(pacienteId);

        return new HistoricoMedico(paciente, atendimentos, internacoes);
    }

    @Transactional(readOnly = true)
    public HistoricoMedico doPacienteLogado(String email) {
        Paciente paciente = pacienteService.buscarPorEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
        return doPaciente(paciente.getId());
    }
}
