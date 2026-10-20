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
@DiscriminatorValue("CLINICO_GERAL")
public class MedicoClinicoGeral extends ProfissionalSaude {
    protected MedicoClinicoGeral(){

    }
    public MedicoClinicoGeral(String nome, String registroProfissional, String telefone,
            String email, String passwordHash) {
        super(nome, registroProfissional, telefone, email, passwordHash);
    }

    @Override 
    public CategoriaProfissional getCategoria(){
        return CategoriaProfissional.CLINICO_GERAL;
    }
    @Override 
    public String getAreaAtuacao(){
        return "Clínica geral";
    }
    // Consultas de rotina e emergências
    @Override
    public boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade){
        return  tipo == TipoAtendimento.ROTINA || tipo == TipoAtendimento.EMERGENCIA;
    }
    //"Pode encamminhar pacientes para atendimento especializados"
    @Override 
    public boolean podeEncaminhar(){
        return true;
    }

}


/*
 * =========================================================
 * MÉDICO CLÍNICO GERAL  (subclasse de ProfissionalSaude)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Realiza consultas de rotina e avaliações iniciais dos pacientes.
 *   - Pode encaminhar pacientes para atendimento especializado.
 *   - Deve registrar as informações referentes às consultas realizadas.
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("CLINICO_GERAL")
 *   public class MedicoClinicoGeral extends ProfissionalSaude
 *
 *   Não tem atributo próprio: usa só os dados comuns da superclasse.
 *   Na tabela, especialidade e area_cirurgica ficam null.
 *
 * CONSTRUTORES
 *   protected MedicoClinicoGeral()   (JPA)
 *   public MedicoClinicoGeral(nome, registroProfissional, telefone, email, passwordHash)
 *       -> super(...)
 *
 * MÉTODOS
 *   getCategoria()   -> CategoriaProfissional.CLINICO_GERAL
 *   getAreaAtuacao() -> "Clínica geral"
 *   podeRealizar(TipoAtendimento tipo, Especialidade especialidade)
 *       -> tipo == ROTINA || tipo == EMERGENCIA   (a especialidade não importa)
 *   podeEncaminhar() -> true
 *
 * COMO O ENCAMINHAMENTO FUNCIONA
 * Quem grava o encaminhamento é a ConsultaRotina
 * (registrarEncaminhamento(especialidade, solicitante)), que pergunta
 * solicitante.podeEncaminhar(). Assim esta classe não precisa conhecer a
 * ConsultaRotina, e a sua parte não depende da parte de consultas.
 *
 * "Registrar as informações das consultas realizadas" já é garantido pela
 * Consulta: concluir(observacoes) exige observações (ver Consulta.java).
 */
