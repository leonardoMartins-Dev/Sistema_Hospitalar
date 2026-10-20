package com.example.MedVita.models;

/*
 * =========================================================
 * CONSULTA  (classe ABSTRATA, filha de Atendimento)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE MUDOU
 * Na Sprint 1 era a classe concreta, com o status em texto ("AGENDADA",
 * "REALIZADA", "CANCELADA"). Agora é a mãe das duas consultas (rotina e
 * especializada) e guarda o que só consultas têm: as SEIS situações do
 * enunciado e as regras de mudança entre elas.
 *
 *   @Entity                     (abstrata, sem @DiscriminatorValue)
 *   public abstract class Consulta extends Atendimento
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 * ATRIBUTO
 *   StatusConsulta status   @Enumerated(EnumType.STRING) @Column(name = "status")
 *                           começa em AGENDADA
 *   NUNCA String: o enunciado pede para evitar "valores textuais livres".
 *
 * MÉTODOS DE SITUAÇÃO (todos passam por mudarStatus)
 *   confirmar()                   AGENDADA -> CONFIRMADA
 *   iniciar()                     CONFIRMADA -> EM_ATENDIMENTO
 *                                 (antes, chama verificarProfissional())
 *   concluir(String observacoes)  EM_ATENDIMENTO -> CONCLUIDA
 *                                 observações obrigatórias: o médico "deve
 *                                 registrar as informações referentes às
 *                                 consultas realizadas"
 *   cancelar()                    AGENDADA ou CONFIRMADA -> CANCELADA
 *   registrarNaoComparecimento()  AGENDADA ou CONFIRMADA -> NAO_COMPARECEU
 *
 *   private void mudarStatus(StatusConsulta novo)
 *       if (!status.podeMudarPara(novo))
 *           throw new RegraNegocioException("Uma consulta " + status.getDescricao()
 *                   + " não pode passar para " + novo.getDescricao());
 *       status = novo;
 *   Quem sabe quais mudanças valem é o próprio enum (StatusConsulta.podeMudarPara).
 *   Ex.: concluída nunca volta para agendada; cancelada nunca vira concluída.
 *
 *   ocupaAgenda()  -> status != CANCELADA   (cancelar libera o horário)
 *   isConcluido()  -> status == CONCLUIDA
 *
 * REAGENDAMENTO (decisão do grupo: cancelada é estado FINAL)
 *   public Consulta reagendar(LocalDate novaData, LocalTime novoHorario)
 *       1. Só se o status for CANCELADA ou NAO_COMPARECEU. Senão:
 *          RegraNegocioException("Só consultas canceladas ou com falta podem ser reagendadas").
 *       2. Consulta nova = copiar();      cada subclasse cria uma do próprio tipo
 *       3. nova.setData(novaData); nova.setHorario(novoHorario);
 *       4. return nova;                   começa AGENDADA; quem salva é o ConsultaService
 *       A consulta antiga NÃO muda: continua cancelada no histórico (RN6).
 *       A nova consulta é o "novo agendamento" que o enunciado exige.
 *
 *   protected abstract Consulta copiar();
 *       ConsultaRotina devolve new ConsultaRotina(mesmo paciente, profissional, motivo);
 *       ConsultaEspecializada devolve new ConsultaEspecializada(..., mesma especialidade).
 *       Polimorfismo de novo: o reagendar() não precisa saber qual é o tipo.
 *
 * REGRA DE NEGÓCIO 3 (o médico não pode ter dois atendimentos no mesmo horário)
 *   Continua valendo: o ConsultaService confere antes de salvar, usando
 *   ocupaAgenda(), e o banco tem o índice único parcial como garantia final.
 */
