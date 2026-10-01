package com.example.MedVita.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MedVita.models.Paciente;

/**
 * REPOSITÓRIO DE PACIENTES (acesso à tabela "pacientes" no Supabase).
 *
 * É só uma interface: o Spring Data JPA cria a implementação sozinho quando a
 * aplicação sobe. Por herdar de JpaRepository<Paciente, Long> (entidade
 * Paciente, ID do tipo Long) ela já vem com os métodos básicos prontos:
 *   save(paciente)  -> INSERT (se o id for null) ou UPDATE (se já tiver id)
 *   findById(id)    -> SELECT pelo id
 *   findAll()       -> SELECT de todos
 *   deleteById(id)  -> DELETE
 *
 * Os métodos declarados abaixo são "query methods": o Spring lê o NOME do
 * método e gera o SQL automaticamente:
 *   findByEmail(email)   -> select * from pacientes where email = ?
 *   existsByEmail(email) -> select count(*) > 0 from pacientes where email = ?
 *
 * Quem usa: PacienteService. Os controllers não falam direto com o
 * repositório; passam sempre pelo service.
 *
 * Para ser encontrado, o pacote desta interface precisa estar coberto pelo
 * @EnableJpaRepositories da classe TelaLoginApplication.
 *
 * =========================================================
 * A CRIAR (ainda não implementado)
 * =========================================================
 *   boolean existsByCpf(String cpf)
 *       -> o cadastro recusar CPF repetido com uma mensagem amigável
 *
 *   List<Paciente> findByNomeContainingIgnoreCaseOrCpfContainingOrEmailContainingIgnoreCase(
 *           String nome, String cpf, String email)
 *       -> campo de busca "Nome, CPF ou e-mail" da tela medico/pacientes
 *          (passa o mesmo texto nos três parâmetros)
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional <Paciente> findByEmail(String email);
    boolean existsByEmail(String email);

}
