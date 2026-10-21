package com.example.MedVita.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.MedVita.config.UserConfig;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.models.ProfissionalSaude;

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
 *                  página /admin/medicos).
 *                  A CRIAR (Sprint 2, responsável: Leonardo Martins): se o
 *                  e-mail não for de paciente, procurar com
 *                  profissionalSaudeService.buscarPorEmail(username) e, se
 *                  achar, devolver .roles("MEDICO"). Vale para as três
 *                  categorias (clínico, especialista e cirurgião): todas
 *                  entram na área /medico. O orElseThrow do paciente vira um
 *                  if/else (ou Optional.or) para tentar as duas tabelas antes
 *                  do "não encontrado". O construtor passa a receber também
 *                  o ProfissionalSaudeService.
 *
 * USUÁRIOS DE TESTE (TEMPORÁRIO): enquanto o banco não funciona, um paciente
 * e um médico também vêm do application.properties (app.teste.*), conferidos
 * logo depois do admin e ANTES de consultar o banco. Quando o bloco for
 * apagado do application.properties, os campos ficam vazios e este passo é
 * simplesmente pulado.
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
 * As senhas do admin e dos usuários de teste estão em texto puro no
 * application.properties, então elas são passadas pelo BCrypt uma vez,
 * quando a aplicação sobe (no construtor).
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final PacienteService pacienteService;
    private final ProfissionalSaudeService profissionalSaudeService;
    private final String adminUsername;
    private final String adminPasswordHash;
    private final String pacienteTesteUsername;
    private final String pacienteTestePasswordHash;
    private final String medicoTesteUsername;
    private final String medicoTestePasswordHash;

    public DatabaseUserDetailsService(PacienteService pacienteService, ProfissionalSaudeService profissionalSaudeService, UserConfig userConfig,
            PasswordEncoder passwordEncoder) {
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
        this.adminUsername = userConfig.getAdminUsername();
        this.adminPasswordHash = passwordEncoder.encode(userConfig.getAdminPassword());
        this.pacienteTesteUsername = userConfig.getPacienteTesteUsername();
        this.pacienteTestePasswordHash = passwordEncoder.encode(userConfig.getPacienteTestePassword());
        this.medicoTesteUsername = userConfig.getMedicoTesteUsername();
        this.medicoTestePasswordHash = passwordEncoder.encode(userConfig.getMedicoTestePassword());
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

        // USUÁRIOS DE TESTE (application.properties, temporário).
        // O isBlank() impede que um username vazio entre quando o bloco
        // app.teste.* for apagado.
        if (!pacienteTesteUsername.isBlank() && pacienteTesteUsername.equals(username)) {
            return User.builder()
                    .username(pacienteTesteUsername)
                    .password(pacienteTestePasswordHash)
                    .roles("USER")
                    .build();
        }

        if (!medicoTesteUsername.isBlank() && medicoTesteUsername.equals(username)) {
            return User.builder()
                    .username(medicoTesteUsername)
                    .password(medicoTestePasswordHash)
                    .roles("MEDICO")
                    .build();
        }

        // 2. PACIENTE (tabela pacientes)
        Optional<Paciente> paciente = pacienteService.buscarPorEmail(username);
        if (paciente.isPresent()) {
            return User.builder()
                    .username(paciente.get().getEmail())
                    .password(paciente.get().getPasswordHash())
                    .roles("USER")
                    .build();
        }

        // 3. MÉDICO (tabela profissionais_saude), qualquer uma das 3 categorias
        ProfissionalSaude profissional = profissionalSaudeService.buscarPorEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(profissional.getEmail())
                .password(profissional.getPasswordHash())
                .roles("MEDICO")
                .build();
    }
}
