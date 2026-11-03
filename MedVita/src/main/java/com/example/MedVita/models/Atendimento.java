package com.example.MedVita.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * =========================================================
 * ATENDIMENTO  (classe ABSTRATA, topo da herança dos atendimentos)
 * =========================================================
 *
 * STATUS: IMPLEMENTADO.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * O QUE É
 * Na Sprint 2 o hospital passou a ter quatro modalidades de atendimento, com
 * informações em comum e regras diferentes. O enunciado pede para não
 * duplicar o que é comum e para cada modalidade ter a própria validação.
 * O comum fica aqui; o que muda fica nas subclasses:
 *
 *   Atendimento (abstrata)            paciente, profissional, data, horário,
 *    │                                motivo, observações, validar()
 *    ├── Consulta (abstrata)          + StatusConsulta (as 6 situações)       João Pedro
 *    │    ├── ConsultaRotina          + encaminhamento                        João Pedro
 *    │    └── ConsultaEspecializada   + especialidade                         João Pedro
 *    ├── AtendimentoEmergencia        + prioridade, StatusEmergencia          Leonardo Gonzaga
 *    └── ProcedimentoCirurgico        + descrição, tipo, obs. pré/pós,        Leonardo Gonzaga
 *                                       início/fim efetivos, StatusProcedimento
 *
 * QUEM DEPENDE DESTA CLASSE
 * As duas subclasses do Leonardo Gonzaga herdam daqui, e o histórico do
 * Henrique lê os atendimentos. Por isso esta classe (e o
 * AtendimentoRepository) vai para a main LOGO NO COMEÇO, mesmo que as
 * consultas ainda não estejam prontas. Ela só depende do ProfissionalSaude e
 * do enum TipoAtendimento (Leonardo Martins) e do Paciente (que já existe).
 *
 * Todas as modalidades ficam em /consultas (decisão do grupo): as consultas
 * pelo ConsultaController (João Pedro); emergência e cirurgia pelo
 * EmergenciaCirurgiaController (Leonardo Gonzaga). As rotas genéricas
 * (iniciar, concluir, cancelar, buscar por id) do ConsultaController valem
 * para as quatro, graças aos métodos abstratos abaixo.
 *
 * =========================================================
 * BANCO: UMA TABELA SÓ (SINGLE_TABLE)
 * =========================================================
 *   @Entity
 *   @Table(name = "atendimentos")      (substitui a antiga tabela "consultas")
 *   @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
 *   @DiscriminatorColumn(name = "tipo")
 *
 * Coluna "tipo": ROTINA | ESPECIALIZADA | EMERGENCIA | CIRURGIA (os mesmos
 * nomes do enum TipoAtendimento). As colunas de uma subclasse ficam null nas
 * linhas das outras. Lista completa das colunas no doc da Sprint 2.
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 * ATRIBUTOS (private + getters; setters protected quando a subclasse precisar)
 *   Long id                          @Id @GeneratedValue(strategy = IDENTITY)
 *   Paciente paciente                @ManyToOne(optional = false) @JoinColumn(name = "paciente_id")
 *   ProfissionalSaude profissional   @ManyToOne @JoinColumn(name = "profissional_id")
 *                                    obrigatório em todas as modalidades, MENOS na
 *                                    emergência que ainda aguarda avaliação (o médico
 *                                    entra quando o atendimento começa)
 *   LocalDate data
 *   LocalTime horario
 *        O significado muda com a modalidade:
 *          consulta   -> dia e hora agendados
 *          emergência -> dia e hora da chegada do paciente
 *          cirurgia   -> dia e hora PREVISTOS (os efetivos ficam na subclasse)
 *   String motivo         motivo da consulta / queixa da emergência / indicação da cirurgia
 *   String observacoes    o que o médico registra ao concluir
 *
 * CONSTRUTORES
 *   protected Atendimento()   (JPA)
 *   protected Atendimento(paciente, profissional, data, horario, motivo)
 *
 * VALIDAÇÃO: MÉTODO TEMPLATE (o comum aqui, o específico na subclasse)
 *   public final void validar() {
 *       // regras comuns a todas as modalidades
 *       paciente obrigatório            -> RegraNegocioException
 *       data e horário obrigatórios     -> RegraNegocioException
 *       // regras da modalidade
 *       validarRegrasDaModalidade();
 *   }
 *   protected abstract void validarRegrasDaModalidade();
 *
 *   É "final" para nenhuma subclasse pular as regras comuns.
 *   Quem chama: o service, ANTES de salvar um atendimento novo.
 *
 *   protected void verificarProfissional()
 *       Ajuda para as subclasses: profissional obrigatório e
 *       profissional.verificarSePodeRealizar(getTipo(), getEspecialidadeExigida()).
 *       O polimorfismo vai nos dois sentidos: o atendimento diz o próprio tipo
 *       (getTipo), e o médico decide (podeRealizar) se aceita esse tipo.
 *
 *   public Especialidade getEspecialidadeExigida()
 *       Devolve null aqui. Só a ConsultaEspecializada sobrescreve, devolvendo
 *       a especialidade dela. É o que o MedicoEspecialista compara com a dele.
 *
 *   protected void verificarDataFutura()
 *       Ajuda para consultas e cirurgias: data + horário não podem estar no
 *       passado (LocalDateTime.of(data, horario).isBefore(LocalDateTime.now())).
 *
 * MÉTODOS ABSTRATOS (cada modalidade tem o próprio ciclo de situações)
 *   TipoAtendimento getTipo()       -> sai no JSON como "tipo"
 *   void iniciar()                  -> começa o atendimento
 *   void concluir(String observacoes)
 *   void cancelar()                 -> na emergência, lança exceção (não pode cancelar)
 *   boolean ocupaAgenda()           -> esse atendimento prende o horário do médico? (RN3)
 *                                      consulta/cirurgia canceladas e emergências: false
 *   boolean isConcluido()           -> usado pelo histórico médico
 *
 *   Quem chama atendimento.iniciar(), atendimento.concluir(...) etc. não sabe
 *   qual é a modalidade, e cada subclasse responde com a regra dela. NÃO usem
 *   if/switch por tipo nesses fluxos: é isso que o professor vai procurar
 *   como polimorfismo.
 *
 * Os models lançam RegraNegocioException (pacote exception) quando uma regra
 * é quebrada. O GlobalExceptionHandler transforma em HTTP 400.
 *
 * TELAS: paciente/agendar-consulta, paciente/consultas, medico/consultas,
 *        medico/emergencias, medico/cirurgias, paciente/historico
 */

@Entity
@Table(name = "atendimentos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    private LocalDate data;
    private LocalTime horario;
    private String motivo;
    private String observacoes;

    protected Atendimento() {
    }

    protected Atendimento(Paciente paciente, ProfissionalSaude profissional, LocalDate data,
            LocalTime horario, String motivo) {
        this.paciente = paciente;
        this.profissional = profissional;
        this.data = data;
        this.horario = horario;
        this.motivo = motivo;
    }

    public final void validar() {
        if (paciente == null) {
            throw new RegraNegocioException("Informe o paciente");
        }
        if (data == null || horario == null) {
            throw new RegraNegocioException("Informe a data e o horário");
        }
        validarRegrasDaModalidade();
    }

    protected abstract void validarRegrasDaModalidade();

    protected void verificarProfissional() {
        if (profissional == null) {
            throw new RegraNegocioException("Informe o profissional");
        }
        profissional.verificarSePodeRealizar(getTipo(), getEspecialidadeExigida());
    }

    public Especialidade getEspecialidadeExigida() {
        return null;
    }

    protected void verificarDataFutura() {
        if (LocalDateTime.of(data, horario).isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("A data e o horário não podem estar no passado");
        }
    }

    public abstract TipoAtendimento getTipo();
    public abstract void iniciar();
    public abstract void concluir(String observacoes);
    public abstract void cancelar();
    public abstract boolean ocupaAgenda();
    public abstract boolean isConcluido();

    //GETTERS

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public ProfissionalSaude getProfissional() { return profissional; }
    public LocalDate getData() { return data; }
    public LocalTime getHorario() { return horario; }
    public String getMotivo() { return motivo; }
    public String getObservacoes() { return observacoes; }

    //SETTERS

    protected void setProfissional(ProfissionalSaude profissional) { this.profissional = profissional; }
    protected void setData(LocalDate data) { this.data = data; }
    protected void setHorario(LocalTime horario) { this.horario = horario; }
    protected void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
