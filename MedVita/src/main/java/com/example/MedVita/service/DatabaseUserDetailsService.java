package com.example.MedVita.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.MedVita.config.UserConfig;
import com.example.MedVita.models.Paciente;

/**
 * PONTE ENTRE O SPRING SECURITY E OS USUÁRIOS DO SISTEMA.
 *
 * O Spring Security não sabe nada sobre o nosso banco; ele só sabe chamar
 * um UserDetailsService (interface do próprio Spring, por isso o nome em
 * inglês). Esta classe implementa essa interface.
 *
 * DE ONDE VEM CADA USUÁRIO:
 *   1. ADMIN    -> do application.properties (app.admin.*). Não fica no
 *                  banco e não tem cadastro.
 *   2. PACIENTE -> da tabela "pacientes" do Supabase (cadastro pela página
 *                  /register).
 *   3. MÉDICO   -> da tabela "profissionais_saude" (cadastro pelo admin na
 *                  página /admin/medicos). AINDA NÃO IMPLEMENTADO: quando o
 *                  ProfissionalSaude existir, entra aqui um passo a mais,
 *                  antes do "não encontrado", devolvendo .roles("MEDICO").
 *
 * COMO O LOGIN FUNCIONA:
 *   1. O usuário envia o formulário de /login (campos "username" e "password").
 *   2. O RecaptchaFilter valida o captcha.
 *   3. O Spring Security chama loadUserByUsername(username) desta classe.
 *   4. Procuramos o usuário na ordem acima. Não achou em lugar nenhum?
 *      Lançamos UsernameNotFoundException -> login falha.
 *   5. Devolvemos um UserDetails (o "User" do Spring Security, objeto só de
 *      login) com usuário, hash da senha e papel.
 *   6. O próprio Spring compara a senha digitada com o hash usando o
 *      PasswordEncoder (BCrypt) do SecurityConfig. Nós nunca comparamos
 *      senhas manualmente.
 *   7. Se bater, o successHandler do SecurityConfig redireciona para
 *      /admin (ROLE_ADMIN), /medico (ROLE_MEDICO) ou /home (ROLE_USER).
 *
 * A senha do admin está em texto puro no application.properties, então ela
 * é passada pelo BCrypt uma vez, quando a aplicação sobe (no construtor).
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final PacienteService pacienteService;
    private final String adminUsername;
    private final String adminPasswordHash;

    public DatabaseUserDetailsService(PacienteService pacienteService, UserConfig userConfig,
            PasswordEncoder passwordEncoder) {
        this.pacienteService = pacienteService;
        this.adminUsername = userConfig.getAdminUsername();
        this.adminPasswordHash = passwordEncoder.encode(userConfig.getAdminPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. ADMIN (application.properties)
        if (adminUsername.equals(username)) {
            return User.builder()
                    .username(adminUsername)
                    .password(adminPasswordHash)
                    .roles("ADMIN")
                    .build();
        }

        // 2. PACIENTE (tabela pacientes)
        Paciente paciente = pacienteService.buscarPorEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + username));

        return User.builder()
                .username(paciente.getEmail())
                .password(paciente.getPasswordHash())
                .roles("USER")
                .build();
    }
}
