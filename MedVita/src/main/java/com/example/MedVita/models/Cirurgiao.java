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
@DiscriminatorValue("CIRURGIAO")
public class Cirurgiao extends ProfissionalSaude {
    @Column(name = "area_cirurgica")
    private String areaCirurgica;

    protected Cirurgiao(){
    }
    public Cirurgiao(String nome, String registroProfissional, String telefone,
            String email, String passwordHash, String areaCirurgica) {
        super(nome, registroProfissional, telefone, email, passwordHash);
        this.areaCirurgica = areaCirurgica;
    }

    @Override
    public CategoriaProfissional getCategoria() {
        return CategoriaProfissional.CIRURGIAO;
    }

    @Override
    public String getAreaAtuacao() {
        return areaCirurgica;
    }
    
     // Só procedimentos cirúrgicos (decisão do grupo)
    @Override
    public boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade) {
        return tipo == TipoAtendimento.CIRURGIA;
    }

    @Override
    public boolean podeEncaminhar() {
        return false;
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }

}
/*
 * =========================================================
 * CIRURGIÃO  (subclasse de ProfissionalSaude)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE É (enunciado da Sprint 2)
 *   - Realiza procedimentos cirúrgicos.
 *   - Além das informações comuns, tem informações da sua atuação cirúrgica.
 *   - O sistema registra os procedimentos realizados por ele (data, paciente
 *     e descrição do procedimento).
 *
 * COMO VAI SER
 *   @Entity
 *   @DiscriminatorValue("CIRURGIAO")
 *   public class Cirurgiao extends ProfissionalSaude
 *
 * ATRIBUTO PRÓPRIO
 *   String areaCirurgica   coluna "area_cirurgica", obrigatório nesta subclasse
 *                          ex.: "Cirurgia geral", "Cirurgia cardíaca", "Neurocirurgia"
 *   É a "informação relacionada à atuação cirúrgica" pedida no enunciado.
 *
 * CONSTRUTORES
 *   protected Cirurgiao()   (JPA)
 *   public Cirurgiao(nome, registroProfissional, telefone, email, passwordHash, areaCirurgica)
 *
 * MÉTODOS
 *   getCategoria()   -> CategoriaProfissional.CIRURGIAO
 *   getAreaAtuacao() -> areaCirurgica
 *   podeRealizar(TipoAtendimento tipo, Especialidade especialidade)
 *       -> tipo == CIRURGIA   (não atende consulta nem emergência, decisão do grupo)
 *   podeEncaminhar() -> false
 *
 * PROCEDIMENTOS REALIZADOS PELO CIRURGIÃO
 *   O registro é o próprio ProcedimentoCirurgico, que aponta para o cirurgião
 *   no campo "profissional". A lista sai pela parte de emergência e cirurgia:
 *     GET /consultas/procedimentos?cirurgiaoId=9
 *
 *   NÃO criem aqui uma List<ProcedimentoCirurgico> com @OneToMany: além de
 *   fazer esta parte depender da outra, o JSON entraria em loop (cirurgião ->
 *   procedimento -> cirurgião -> ...) e daria LazyInitializationException (o
 *   open-in-view está desligado). No diagrama de classes a associação
 *   Cirurgiao 1 --- 0..* ProcedimentoCirurgico aparece normalmente; ela só não
 *   vira atributo Java deste lado.
 */
