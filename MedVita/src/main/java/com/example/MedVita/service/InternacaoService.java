package com.example.MedVita.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MedVita.dto.InternacaoRequest;
import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.Internacao;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.Quarto;
import com.example.MedVita.models.enums.StatusInternacao;
import com.example.MedVita.repository.InternacaoRepository;
import com.example.MedVita.repository.PacienteRepository;
import com.example.MedVita.repository.ProfissionalSaudeRepository;
import com.example.MedVita.repository.QuartoRepository;

/*
 * =========================================================
 * SERVIÇO DE INTERNAÇÕES
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * USA (só repositórios, para não depender do service de ninguém)
 *   InternacaoRepository, QuartoRepository   (desta parte)
 *   PacienteRepository                       (já existe) -> findById
 *   PacienteService.buscarPorEmail           (já existe) -> paciente logado
 *   ProfissionalSaudeRepository              (Leonardo Martins) -> findById
 *
 * MÉTODOS
 *   @Transactional
 *   Internacao internar(InternacaoRequest req)
 *     1. paciente = pacienteRepository.findById(...)
 *        profissional = profissionalSaudeRepository.findById(...)
 *        (os dois com .orElseThrow(() -> new RecursoNaoEncontradoException(...)))
 *        quarto = quartoRepository.buscarParaAtualizar(quartoId)   (trava a linha)
 *                 -> RecursoNaoEncontradoException se não existir
 *     2. internacaoRepository.existsByPacienteIdAndStatus(pacienteId, ATIVA)
 *        -> RegraNegocioException("Este paciente já está internado")
 *     3. internacao = new Internacao(...); internacao.validar()
 *     4. quarto.ocupar()   lança RegraNegocioException se estiver lotado (RN4 e RN5)
 *     5. save da internação. O quarto é salvo sozinho no fim da transação
 *        (o Hibernate percebe que a ocupação mudou), mas pode chamar
 *        quartoRepository.save(quarto) para deixar claro.
 *     Com @Transactional, os passos 4 e 5 acontecem juntos ou nada acontece.
 *
 *   @Transactional
 *   Internacao darAlta(Long id, LocalDate dataEfetivaAlta, String observacoes)
 *     1. internacao = buscarPorId(id)
 *     2. internacao.darAlta(dataEfetivaAlta, observacoes)   (o model confere as regras)
 *     3. quartoRepository.buscarParaAtualizar(internacao.getQuarto().getId())
 *            .liberar()
 *     4. save
 *
 *   Internacao buscarPorId(Long id)                -> RecursoNaoEncontradoException
 *   List<Internacao> listar(StatusInternacao status)
 *       -> status null: todas; ATIVA: quem está internado agora (medico/internacoes)
 *   List<Internacao> listarDoPaciente(Long pacienteId)   -> paciente/internacoes e histórico
 */

@Service
public class InternacaoService {

    private final InternacaoRepository internacaoRepository;
    private final QuartoRepository quartoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public InternacaoService(InternacaoRepository internacaoRepository, QuartoRepository quartoRepository,
            PacienteRepository pacienteRepository, ProfissionalSaudeRepository profissionalSaudeRepository) {
        this.internacaoRepository = internacaoRepository;
        this.quartoRepository = quartoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalSaudeRepository = profissionalSaudeRepository;
    }

    @Transactional
    public Internacao internar(InternacaoRequest req) {
        if (req == null || req.pacienteId() == null || req.profissionalId() == null || req.quartoId() == null) {
            throw new RegraNegocioException("Informe o paciente, o profissional responsável e o quarto");
        }

        // 1. busca tudo (o quarto com trava, para ninguém ocupar o mesmo leito ao mesmo tempo)
        Paciente paciente = pacienteRepository.findById(req.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente " + req.pacienteId() + " não encontrado"));
        ProfissionalSaude profissional = profissionalSaudeRepository.findById(req.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Profissional " + req.profissionalId() + " não encontrado"));
        Quarto quarto = quartoRepository.buscarParaAtualizar(req.quartoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Quarto " + req.quartoId() + " não encontrado"));

        // 2. um paciente internado por vez
        if (internacaoRepository.existsByPacienteIdAndStatus(paciente.getId(), StatusInternacao.ATIVA)) {
            throw new RegraNegocioException("Este paciente já está internado");
        }

        // 3. regras da própria internação
        Internacao internacao = new Internacao(paciente, profissional, quarto,
                req.dataEntrada(), req.dataPrevistaAlta(), req.observacoes());
        internacao.validar();

        // 4. ocupa o leito (lança exceção se o quarto estiver lotado: RN4 e RN5)
        quarto.ocupar();

        // 5. os dois são gravados juntos ou nada é gravado (@Transactional)
        quartoRepository.save(quarto);
        return internacaoRepository.save(internacao);
    }

    @Transactional
    public Internacao darAlta(Long id, LocalDate dataEfetivaAlta, String observacoes) {
        Internacao internacao = buscarPorId(id);
        internacao.darAlta(dataEfetivaAlta, observacoes);   // o model confere as regras

        Quarto quarto = quartoRepository.buscarParaAtualizar(internacao.getQuarto().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto da internação não encontrado"));
        quarto.liberar();
        quartoRepository.save(quarto);

        return internacaoRepository.save(internacao);
    }

    public Internacao buscarPorId(Long id) {
        return internacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Internação " + id + " não encontrada"));
    }

    // status null: todas; ATIVA: quem está internado agora
    public List<Internacao> listar(StatusInternacao status) {
        return status == null
                ? internacaoRepository.findAllByOrderByDataEntradaDesc()
                : internacaoRepository.findByStatusOrderByDataEntradaDesc(status);
    }

    public List<Internacao> listarDoPaciente(Long pacienteId) {
        return internacaoRepository.findByPacienteIdOrderByDataEntradaDesc(pacienteId);
    }
}
