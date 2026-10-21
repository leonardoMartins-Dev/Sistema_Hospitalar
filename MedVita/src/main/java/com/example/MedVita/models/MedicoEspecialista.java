package com.example.MedVita.models;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity 
@DiscriminatorValue("ESPECIALISTA")
public class MedicoEspecialista extends ProfissionalSaude {
    @Enumerated(EnumType.STRING) //grava "CARDIOLOGIA, nao o numero
    @Column (name = "especialidade")
    private Especialidade especialidade;

    protected MedicoEspecialista(){
    }
    public MedicoEspecialista(String nome, String registroProfissional, String telefone,
            String email, String passwordHash, Especialidade especialidade) {
        super(nome, registroProfissional, telefone, email, passwordHash);
        this.especialidade = especialidade;
    }

    @Override
    public CategoriaProfissional getCategoria() {
        return CategoriaProfissional.ESPECIALISTA;
    }

    @Override
    public String getAreaAtuacao() {
        return especialidade.getNome();
    }

    public boolean atendeEspecialidade(Especialidade outra) {
        return especialidade == outra;
    }

    // Emergência, ou consulta especializada DA MESMA especialidade
    @Override 
    public boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidadePedida){
        if (tipo==TipoAtendimento.EMERGENCIA){
            return true;
        }
        return tipo == TipoAtendimento.ESPECIALIZADA && atendeEspecialidade(especialidadePedida);
    }

    @Override
    public boolean podeEncaminhar(){
        return false;
    }
    public Especialidade gEspecialidade(){
        return especialidade;
    }
}

/*
 * =========================================================
 * MÉDICO ESPECIALISTA  (subclasse de ProfissionalSaude)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Atua em uma área específica (cardiologia, neurologia, ortopedia...).
 *   - Realiza atendimentos relacionados à sua especialidade.
 *   - O sistema registra a especialidade e permite consultar os
 *     profissionais disponíveis por área de atuação.
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("ESPECIALISTA")
 *   public class MedicoEspecialista extends ProfissionalSaude
 *
 * ATRIBUTO PRÓPRIO
 *   Especialidade especialidade   @Enumerated(EnumType.STRING), coluna "especialidade"
 *                                 obrigatório nesta subclasse
 *   É enum, não String: assim "Cardiologia" e "cardiologia" não viram duas
 *   especialidades diferentes.
 *
 * CONSTRUTORES
 *   protected MedicoEspecialista()   (JPA)
 *   public MedicoEspecialista(nome, registroProfissional, telefone, email,
 *                             passwordHash, especialidade)
 *
 * MÉTODOS
 *   getCategoria()   -> CategoriaProfissional.ESPECIALISTA
 *   getAreaAtuacao() -> especialidade.getNome()   (ex.: "Cardiologia")
 *   boolean atendeEspecialidade(Especialidade e)  -> this.especialidade == e
 *   podeRealizar(TipoAtendimento tipo, Especialidade especialidadePedida)
 *       -> true  se tipo == EMERGENCIA
 *          true  se tipo == ESPECIALIZADA && atendeEspecialidade(especialidadePedida)
 *          false no resto (rotina, cirurgia, outra especialidade)
 *       É a regra "deve estar associada a um profissional cuja área de atuação
 *       seja compatível com o atendimento solicitado".
 *   podeEncaminhar() -> false
 *
 * PROFISSIONAIS POR ÁREA DE ATUAÇÃO
 *   GET /profissionais?especialidade=CARDIOLOGIA   (lista por área, desta parte)
 *   Para saber quem está LIVRE num dia e horário, a tela de agendamento usa
 *   GET /consultas/profissionais-disponiveis (parte de consultas), porque
 *   quem conhece a agenda são os atendimentos.
 */
