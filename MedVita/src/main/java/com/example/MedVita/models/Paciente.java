package com.example.MedVita.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ENTIDADE JPA DO PACIENTE, SALVA NO SUPABASE (tabela "pacientes").
 *
 * O paciente é o usuário do sistema: quem se cadastra pela página /register.
 * Ele cobre o Requisito 1 do enunciado (nome, CPF, data de nascimento,
 * telefone, endereço e e-mail) e também guarda o login (e-mail + senha).
 * Admin e médico NÃO ficam nesta tabela: o admin vem do
 * application.properties e o médico tem a própria tabela
 * (ProfissionalSaude).
 *
 * Cada objeto desta classe corresponde a UMA LINHA da tabela
 * "public.pacientes" do PostgreSQL do Supabase. O Hibernate (implementação
 * do JPA) faz a tradução automática entre o objeto Java e a linha da tabela:
 *
 *   Campo Java      ->  Coluna no banco
 *   id              ->  id               (bigint, gerado pelo banco)
 *   email           ->  email            (text, único, usado como login)
 *   passwordHash    ->  password_hash    (text, hash BCrypt da senha)
 *   nome            ->  nome             (text)
 *   cpf             ->  cpf              (text, único, só números)
 *   telefone        ->  telefone         (text)
 *   endereco        ->  endereco         (text)
 *   dataNascimento  ->  data_nascimento  (date)
 *
 * RELACIONAMENTOS (Regra de Negócio 1): um paciente pode ter várias
 * consultas e várias internações. Quem guarda a ligação são a Consulta e a
 * Internacao (coluna paciente_id), não esta classe.
 *
 * SENHA: NUNCA é guardada em texto puro. O PacienteService passa a senha
 * pelo BCrypt antes de criar o objeto, e aqui fica só o hash.
 *
 * IMPORTANTE: como spring.jpa.hibernate.ddl-auto=none, o Hibernate NÃO cria
 * nem altera a tabela. Ela é criada no Supabase pelo script
 * supabase/schema.sql. Se mudar algum campo aqui, mude também no banco.
 *
 * =========================================================
 * A CRIAR (ainda não implementado)
 * =========================================================
 * Os atributos do Requisito 1 já estão todos aqui. Falta só:
 *   - @JsonIgnore em cima do atributo passwordHash. Quando o
 *     PacienteController devolver o paciente em JSON, a senha (mesmo em
 *     hash) não pode aparecer.
 */
@Entity //RECONHECE COMO ENTIDADE
@Table (name="pacientes")
public class Paciente {

    @Id  //atribui ID
    @GeneratedValue(strategy = GenerationType.IDENTITY) //O BANCO GERA O VALOR
    private Long id;

    @Column (nullable = false, unique = true) //N pode ser NULL nem repetir
    private String email;

    @Column (name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String endereco;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    // created_at não precisa estar aqui: o banco preenche sozinho (default now())

    //CONSTRUTORES
    public Paciente() {
    }

    public Paciente(String email, String passwordHash, String nome) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nome = nome;
    }

    //GETTERS SETTERS

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

}
