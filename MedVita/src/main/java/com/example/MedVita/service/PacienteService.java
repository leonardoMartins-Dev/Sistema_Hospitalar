package com.example.MedVita.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.MedVita.models.Paciente;
import com.example.MedVita.repository.PacienteRepository;

/**
 * SERVIÇO DE PACIENTES (regras de negócio sobre a tabela "pacientes").
 *
 * É a única classe que conversa com o PacienteRepository. Controllers e o
 * login (DatabaseUserDetailsService) sempre passam por aqui, assim as regras
 * abaixo valem em todo o sistema:
 *
 * 1) E-MAIL NORMALIZADO: todo e-mail passa por normalizar() (tira espaços e
 *    deixa minúsculo) antes de ir para o banco. Assim "Leo@Gmail.com " e
 *    "leo@gmail.com" são o mesmo paciente, tanto no cadastro quanto no login.
 *
 * 2) SENHA SEMPRE COM HASH: a senha digitada nunca é salva. Ela passa pelo
 *    PasswordEncoder (BCrypt, definido no SecurityConfig) e só o hash vai
 *    para a coluna password_hash.
 *
 * 3) CPF SÓ COM NÚMEROS: "123.456.789-00" é salvo como "12345678900", para
 *    que buscas e comparações não dependam de como o paciente digitou.
 *
 * Métodos que já existem:
 *   cadastrar()      -> cadastro pela página /register, com nome, e-mail,
 *                       senha, CPF, telefone, endereço e data de nascimento
 *   existe()         -> verifica se o e-mail já está cadastrado
 *   buscarPorEmail() -> busca o paciente (usado no login)
 *   buscarNome()     -> nome do paciente (usado no e-mail de recuperação)
 *   atualizarSenha() -> grava a nova senha no fluxo "esqueci minha senha"
 *
 * =========================================================
 * A CRIAR (ainda não implementado)
 * =========================================================
 *   - No cadastrar(): recusar CPF repetido com
 *     pacienteRepository.existsByCpf(...) e lançar RegraNegocioException.
 *     Hoje só o e-mail é conferido; o banco já recusa CPF repetido
 *     (unique), mas sem uma mensagem amigável.
 *   - List<Paciente> listar()
 *       -> todos os pacientes (tela medico/pacientes)
 *   - List<Paciente> buscar(String termo)
 *       -> campo "Nome, CPF ou e-mail" da tela medico/pacientes; termo vazio
 *          devolve todos
 *   - Paciente buscarPorId(Long id)
 *       -> lança RecursoNaoEncontradoException se não existir (botão
 *          "Ver ficha" da tela medico/pacientes e HistoricoMedicoService)
 */
@Service
public class PacienteService {

        private final PacienteRepository pacienteRepository;
        private final PasswordEncoder passwordEncoder;

        public PacienteService(PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder){
                this.pacienteRepository = pacienteRepository;
                this.passwordEncoder =  passwordEncoder;
        }

        //CRIA O PACIENTE COM OS DADOS DO CADASTRO E SALVA NO DB
        public void cadastrar(String email, String senha, String nome,
                        String cpf, String telefone, String endereco, LocalDate dataNascimento){
                Paciente paciente = new Paciente(normalizar(email), passwordEncoder.encode(senha), nome);
                paciente.setCpf(somenteDigitos(cpf));
                paciente.setTelefone(telefone);
                paciente.setEndereco(endereco);
                paciente.setDataNascimento(dataNascimento);
                pacienteRepository.save(paciente);
        }

        //VERIFICA O PACIENTE PELO EMAIL
        public boolean existe(String email){
                return pacienteRepository.existsByEmail(normalizar(email));
        }

        //BUSCA O PACIENTE PELO EMAIL
        public Optional<Paciente> buscarPorEmail(String email){
                return pacienteRepository.findByEmail(normalizar(email));
        }

        //RETORNA O NOME DO PACIENTE (OU NULL SE NÃO EXISTIR)
        public String buscarNome(String email){
                return buscarPorEmail(email)
                                .map(Paciente::getNome)
                                .orElse(null);
        }

        //TROCA A SENHA E SALVA NO DB
        public void atualizarSenha(String email, String novaSenha){
                Paciente paciente = buscarPorEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Paciente não encontrado: " + email));

                paciente.setPasswordHash(passwordEncoder.encode(novaSenha));

                // Como o paciente já tem id, o save() faz UPDATE em vez de INSERT
                pacienteRepository.save(paciente);
        }

        //PADRONIZA O EMAIL: SEM ESPAÇOS E EM MINÚSCULO
        private String normalizar(String email){
                return email == null ? null : email.trim().toLowerCase();
        }

        //DEIXA SÓ OS NÚMEROS DO CPF (123.456.789-00 -> 12345678900)
        private String somenteDigitos(String valor){
                return valor == null ? null : valor.replaceAll("\\D", "");
        }
}
