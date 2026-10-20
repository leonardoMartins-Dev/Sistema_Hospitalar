package com.example.MedVita.models;

/*
 * =========================================================
 * HISTÓRICO MÉDICO
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
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
