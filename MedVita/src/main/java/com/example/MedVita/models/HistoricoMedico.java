package com.example.MedVita.models;

import java.util.List;

/*
 * =========================================================
 * HISTÓRICO MÉDICO
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Henrique (Sprint 2)
 *
 * O QUE É
 * O histórico de um paciente: atendimentos concluídos, internações e as
 * observações registradas neles (RN6).
 *
 * NÃO É UMA TABELA
 * Não tem @Entity. O histórico já está no banco: são os atendimentos e as
 * internações do paciente, que nunca são apagados. Esta classe só junta tudo
 * num objeto para devolver no JSON.
 *
 * O QUE MUDOU NA SPRINT 2
 * List<Consulta> virou List<Atendimento>: o histórico agora mostra todas as
 * modalidades concluídas (rotina, especializada, emergência e cirurgia).
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 * CAMPOS (private final + getters, sem setters: é só leitura)
 *   Paciente paciente
 *   List<Atendimento> atendimentos   só os concluídos (atendimento.isConcluido()),
 *                                    do mais recente para o mais antigo
 *   List<Internacao> internacoes     todas, da mais recente para a mais antiga
 *
 * CONSTRUTOR
 *   public HistoricoMedico(paciente, atendimentos, internacoes)
 *
 * No JSON, cada atendimento traz "tipo" (getTipo()), então a tela sabe
 * mostrar "Consulta de rotina", "Procedimento cirúrgico" etc.
 *
 * QUEM MONTA: HistoricoMedicoService.
 * TELA: paciente/historico (e o médico, ao consultar um paciente)
 */

public class HistoricoMedico {

    private final Paciente paciente;
    private final List<Atendimento> atendimentos;   // só os concluídos, do mais recente ao mais antigo
    private final List<Internacao> internacoes;     // todas, da mais recente à mais antiga

    public HistoricoMedico(Paciente paciente, List<Atendimento> atendimentos, List<Internacao> internacoes) {
        this.paciente = paciente;
        this.atendimentos = List.copyOf(atendimentos);
        this.internacoes = List.copyOf(internacoes);
    }

    public Paciente getPaciente() { return paciente; }
    public List<Atendimento> getAtendimentos() { return atendimentos; }
    public List<Internacao> getInternacoes() { return internacoes; }
}
