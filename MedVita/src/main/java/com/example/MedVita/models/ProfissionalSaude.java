package com.example.MedVita.models;

import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity //DECLARA COM ENTIDADE
@Table(name="profissionais_saude")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // as 3 subclasses na mesma tabela)
@DiscriminatorColumn(name = "tipo") 
public abstract class ProfissionalSaude{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String nome;

    @Column(name="registro_profissional", nullable = false, unique = true)
    private String registroProfissional;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore   // a senha (mesmo em hash) nunca sai no JSON
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    //JPA precisa de um construtor vazio
    protected ProfissionalSaude(){
    }

    protected ProfissionalSaude(String nome, String registroProfissional, String telefone,
            String email, String passwordHash) {
        this.nome = nome;
        this.registroProfissional = registroProfissional;
        this.telefone = telefone;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // ===== POLIMORFISMO: cada categoria responde do seu jeito =====

    public abstract CategoriaProfissional getCategoria();

    public abstract String getAreaAtuacao();

    public abstract boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade);

    public abstract boolean podeEncaminhar();

    // Escrito uma vez só, vale para as 3 subclasses. Não tem nenhum "if que tipo
    // de médico é": quem decide é o podeRealizar() de cada subclasse..
    public void verificarSePodeRealizar(TipoAtendimento tipo, Especialidade especialidade) {
        if (!podeRealizar(tipo, especialidade)) {
            throw new RegraNegocioException(nome + " (" + getAreaAtuacao()
                    + ") não pode realizar " + tipo.getDescricao());
        }
    }

    // ===== GETTERS E SETTERS =====
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRegistroProfissional() { return registroProfissional; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}

/*
 * =========================================================
 * PROFISSIONAL DA SAÚDE  (classe ABSTRATA, topo da herança)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * O QUE MUDOU NA SPRINT 2
 * O hospital tem três categorias de profissional, com atividades diferentes.
 * Esta classe virou ABSTRATA: ninguém é "só profissional da saúde", todo
 * mundo é de uma das três subclasses:
 *
 *   ProfissionalSaude (abstrata)    nome, registro, telefone, e-mail, senha
 *     ├── MedicoClinicoGeral        rotina e emergência; pode encaminhar
 *     ├── MedicoEspecialista        + especialidade; consultas da especialidade e emergência
 *     └── Cirurgiao                 + areaCirurgica; procedimentos cirúrgicos
 *
 * Aqui ficam só os dados COMUNS. O que cada categoria pode fazer fica nas
 * subclasses (polimorfismo).
 *
 * NÃO DEPENDE DE NINGUÉM DO GRUPO
 * Esta hierarquia não conhece as classes de Atendimento: o podeRealizar()
 * recebe só o TIPO do atendimento (enum TipoAtendimento) e a especialidade.
 * Por isso dá para fazer tudo daqui sem esperar a parte de ninguém. Já os
 * outros dependem desta: Atendimento e Internacao têm um ProfissionalSaude,
 * então estas 4 classes, os 3 enums (Especialidade, CategoriaProfissional,
 * TipoAtendimento), o ProfissionalSaudeRepository e as 2 exceções vão para
 * a main NOS PRIMEIROS DIAS.
 *
 * =========================================================
 * BANCO: UMA TABELA PARA AS TRÊS CATEGORIAS (SINGLE_TABLE)
 * =========================================================
 *   @Entity
 *   @Table(name = "profissionais_saude")
 *   @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
 *   @DiscriminatorColumn(name = "tipo")
 *
 * A coluna "tipo" diz ao Hibernate qual subclasse criar ao ler a linha:
 * "CLINICO_GERAL", "ESPECIALISTA" ou "CIRURGIAO" (os mesmos nomes do enum
 * CategoriaProfissional). Ninguém preenche "tipo" na mão: o Hibernate grava
 * sozinho, a partir do @DiscriminatorValue de cada subclasse.
 *
 * TABELA profissionais_saude (schema completo no doc da Sprint 2)
 *   id                     bigint  gerado pelo banco
 *   tipo                   text    CLINICO_GERAL | ESPECIALISTA | CIRURGIAO
 *   nome                   text    obrigatório
 *   registro_profissional  text    obrigatório e único (ex.: CRM 123456-MG)
 *   telefone               text    obrigatório
 *   email                  text    obrigatório e único (login do médico)
 *   password_hash          text    senha em BCrypt
 *   especialidade          text    só do MedicoEspecialista (null nos outros)
 *   area_cirurgica         text    só do Cirurgiao (null nos outros)
 *
 * =========================================================
 * A CRIAR
 * =========================================================
 * ATRIBUTOS (private, com getters; setters só do que pode mudar)
 *   Long id                       @Id @GeneratedValue(strategy = IDENTITY)
 *   String nome
 *   String registroProfissional   @Column(name = "registro_profissional")
 *   String telefone
 *   String email
 *   String passwordHash           @Column(name = "password_hash") + @JsonIgnore
 *                                 (a senha nunca pode sair no JSON)
 *
 * CONSTRUTORES
 *   protected ProfissionalSaude()   vazio, obrigatório para o JPA
 *   protected ProfissionalSaude(nome, registroProfissional, telefone, email, passwordHash)
 *   São protected porque a classe é abstrata: só as subclasses chamam (super(...)).
 *
 * MÉTODOS ABSTRATOS (cada subclasse implementa do seu jeito)
 *   CategoriaProfissional getCategoria()
 *       -> CLINICO_GERAL, ESPECIALISTA ou CIRURGIAO.
 *   String getAreaAtuacao()
 *       -> texto para a tela: "Clínica geral", "Cardiologia", "Cirurgia cardíaca"...
 *   boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade)
 *       -> O MÉTODO DO POLIMORFISMO. Diz se este profissional pode ser o
 *          responsável por um atendimento daquele tipo. "especialidade" só
 *          importa na consulta especializada (nos outros tipos vem null).
 *            MedicoClinicoGeral: ROTINA ou EMERGENCIA
 *            MedicoEspecialista: EMERGENCIA, ou ESPECIALIZADA da MESMA especialidade
 *            Cirurgiao:          CIRURGIA
 *   boolean podeEncaminhar()
 *       -> só o MedicoClinicoGeral devolve true ("pode encaminhar pacientes
 *          para atendimento especializado"). Quem usa: ConsultaRotina.registrarEncaminhamento.
 *
 * MÉTODO CONCRETO (escrito uma vez aqui, vale para as três subclasses)
 *   void verificarSePodeRealizar(TipoAtendimento tipo, Especialidade especialidade)
 *       if (!podeRealizar(tipo, especialidade)) lança RegraNegocioException com
 *       uma mensagem como "Ana Souza (Ortopedia) não pode realizar Consulta
 *       especializada". Use getAreaAtuacao() e tipo.getDescricao().
 *       Quem chama: Atendimento.verificarProfissional(). Repare que aqui não
 *       tem nenhum "if que tipo de médico é esse": quem decide é o
 *       podeRealizar() da subclasse. É isso que mostra o polimorfismo na
 *       apresentação.
 *
 * JSON (o que o GET /profissionais devolve)
 *   O Jackson usa os getters, então além dos atributos saem "categoria"
 *   (getCategoria) e "areaAtuacao" (getAreaAtuacao). As subclasses acrescentam
 *   os campos delas (ex.: "especialidade"). "passwordHash" NÃO sai (@JsonIgnore).
 *   Para RECEBER o cadastro, o controller usa o dto/ProfissionalRequest, porque
 *   o Jackson não consegue criar uma classe abstrata.
 *
 * LOGIN DO MÉDICO
 *   As três categorias entram pela mesma tela de login, com o papel MEDICO
 *   (ver DatabaseUserDetailsService).
 *
 * RELACIONAMENTOS
 *   1 profissional -> vários atendimentos (como responsável)   RN2
 *   1 profissional -> várias internações  (como responsável)
 *   Quem guarda a ligação são Atendimento e Internacao (coluna profissional_id).
 *
 * TELAS: admin/medicos (cadastro e lista), paciente/agendar-consulta (escolher médico)
 */
