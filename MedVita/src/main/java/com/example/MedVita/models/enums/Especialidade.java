package com.example.MedVita.models.enums;

/*
 * =========================================================
 * ENUM: ESPECIALIDADE MÉDICA
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * Usado em: MedicoEspecialista.especialidade,
 *           ConsultaEspecializada.especialidade,
 *           ConsultaRotina.encaminhamento.
 *
 * VALORES (nome no banco -> texto na tela)
 *   CARDIOLOGIA("Cardiologia")
 *   DERMATOLOGIA("Dermatologia")
 *   GINECOLOGIA("Ginecologia")
 *   NEUROLOGIA("Neurologia")
 *   ORTOPEDIA("Ortopedia")
 *   PEDIATRIA("Pediatria")
 *
 * ATRIBUTO: String nome + getNome()
 *
 * ATENÇÃO: "Clínica Geral" NÃO é especialidade. Virou uma categoria de
 * profissional (MedicoClinicoGeral). Os selects de admin/medicos e
 * paciente/agendar-consulta precisam mudar por causa disso.
 * Para os selects não ficarem com os valores escritos à mão no HTML, eles
 * podem buscar a lista em GET /profissionais/especialidades.
 *
 * No banco fica o NOME do valor (@Enumerated(EnumType.STRING)), nunca o
 * número (ORDINAL): se alguém mudar a ordem do enum, os dados antigos não quebram.
 */
