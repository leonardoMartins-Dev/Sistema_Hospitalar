package com.example.MedVita.models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.Prioridade;
import com.example.MedVita.models.enums.StatusEmergencia;
import com.example.MedVita.models.enums.TipoAtendimento;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/*
 * =========================================================
 * ATENDIMENTO DE EMERGÊNCIA  (subclasse de Atendimento)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: Leonardo Gonzaga (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Situações que exigem avaliação imediata.
 *   - Registra a prioridade, com categorias fixas: baixa, moderada, alta e
 *     crítica (enum Prioridade).
 *   - Permite identificar os que aguardam avaliação e os já iniciados ou concluídos.
 *
 * ATRIBUTOS PRÓPRIOS
 *   Prioridade prioridade      coluna "prioridade", obrigatório
 *   StatusEmergencia status    coluna "status", começa em AGUARDANDO_AVALIACAO
 *
 * COMO FUNCIONA (rotas do EmergenciaCirurgiaController, também desta parte)
 *   1. O paciente chega: POST /consultas/emergencias com paciente, motivo (a
 *      queixa) e prioridade. data/horario = agora (o construtor preenche).
 *      Ainda SEM médico: profissional fica null.
 *   2. Um médico assume: PUT /consultas/emergencias/{id}/assumir?profissionalId=3
 *      -> atribuirProfissional(medico) e depois iniciar().
 *   3. O médico conclui: PUT /consultas/{id}/concluir. Essa rota é a genérica
 *      do ConsultaController (João Pedro) e funciona sem ele conhecer esta
 *      classe, porque chama atendimento.concluir() (polimorfismo).
 *
 * POLIMORFISMO
 *   cancelar() SEMPRE lança RegraNegocioException: é o mesmo método da
 *   Consulta, com outro comportamento (emergência não pode ser cancelada).
 *   ocupaAgenda() devolve false: emergência não é agendada, não prende horário.
 *
 * FILA DE EMERGÊNCIA
 *   GET /consultas/emergencias?status=AGUARDANDO_AVALIACAO
 *   Ordenada por prioridade (CRITICA primeiro) e depois por chegada (data, horario).
 *   Ver EmergenciaCirurgiaService.filaEmergencia(...).
 */
@Entity
@DiscriminatorValue("EMERGENCIA")
public class AtendimentoEmergencia extends Atendimento {

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade")
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEmergencia status;

    //CONSTRUTORES

    protected AtendimentoEmergencia() {
        // obrigatório para o JPA
    }

    // O paciente chega agora e ainda não tem médico
    public AtendimentoEmergencia(Paciente paciente, String motivo, Prioridade prioridade) {
        super(paciente, null, LocalDate.now(),
                LocalTime.now().withSecond(0).withNano(0), motivo);
        this.prioridade = prioridade;
        this.status = StatusEmergencia.AGUARDANDO_AVALIACAO;
    }

    //TIPO DO ATENDIMENTO (sai no JSON como "tipo")
    @Override
    public TipoAtendimento getTipo() {
        return TipoAtendimento.EMERGENCIA;
    }

    //REGRAS PRÓPRIAS DA EMERGÊNCIA (chamado pelo validar() da classe mãe)
    @Override
    protected void validarRegrasDaModalidade() {
        if (prioridade == null) {
            throw new RegraNegocioException("Informe a prioridade da emergência");
        }
        if (getMotivo() == null || getMotivo().isBlank()) {
            throw new RegraNegocioException("Informe a queixa do paciente");
        }
        // a chegada é agora, então não confere data futura
        if (getProfissional() != null) {
            verificarProfissional();
        }
    }

    //UM MÉDICO ASSUME O ATENDIMENTO
    public void atribuirProfissional(ProfissionalSaude medico) {
        if (status != StatusEmergencia.AGUARDANDO_AVALIACAO) {
            throw new RegraNegocioException("Esta emergência já foi assumida por outro médico");
        }
        // clínico e especialista aceitam; o cirurgião recusa (podeRealizar)
        medico.verificarSePodeRealizar(TipoAtendimento.EMERGENCIA, null);
        setProfissional(medico);
    }

    //SITUAÇÕES

    @Override
    public void iniciar() {
        if (getProfissional() == null) {
            throw new RegraNegocioException("Defina o médico antes de iniciar");
        }
        mudarStatus(StatusEmergencia.EM_ATENDIMENTO);
    }

    @Override
    public void concluir(String observacoes) {
        if (observacoes == null || observacoes.isBlank()) {
            throw new RegraNegocioException("Informe as observações do atendimento");
        }
        mudarStatus(StatusEmergencia.CONCLUIDO);
        setObservacoes(observacoes);
    }

    @Override
    public void cancelar() {
        throw new RegraNegocioException("Atendimento de emergência não pode ser cancelado");
    }

    @Override
    public boolean ocupaAgenda() {
        return false; // emergência não é agendada
    }

    @Override
    public boolean isConcluido() {
        return status == StatusEmergencia.CONCLUIDO;
    }

    public boolean aguardandoAvaliacao() {
        return status == StatusEmergencia.AGUARDANDO_AVALIACAO;
    }

    //QUEM SABE QUAIS MUDANÇAS VALEM É O ENUM
    private void mudarStatus(StatusEmergencia novo) {
        if (!status.podeMudarPara(novo)) {
            throw new RegraNegocioException("Uma emergência " + status.getDescricao()
                    + " não pode passar para " + novo.getDescricao());
        }
        status = novo;
    }

    //GETTERS

    public Prioridade getPrioridade() { return prioridade; }
    public StatusEmergencia getStatus() { return status; }
}
