package com.example.MedVita.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * LEITURA DAS CONFIGURAÇÕES PERSONALIZADAS DO application.properties.
 *
 * Centraliza em um só lugar os valores que o resto do código precisa ler do
 * application.properties (que por sua vez pode puxar do arquivo .env).
 * Cada campo com @Value("${chave}") recebe o valor da chave quando a
 * aplicação sobe; se a chave não existir, a aplicação nem inicia.
 *
 * O que tem aqui:
 *   - ADMINISTRADOR (app.admin.*): usuário, senha e nome do admin. O admin
 *     NÃO fica no banco; o DatabaseUserDetailsService confere o login dele
 *     com estes valores.
 *   - USUÁRIOS DE TESTE (app.teste.*): um paciente e um médico TEMPORÁRIOS,
 *     para entrar no site enquanto o banco não funciona. Têm valor padrão
 *     vazio (o ":" no @Value), então dá para apagar o bloco do
 *     application.properties sem quebrar a aplicação: vazio = desativado.
 *   - reCAPTCHA (recaptcha.*): a site key vai para a página de login (via
 *     SecureLoginController) e a secret key é usada pelo RecaptchaService para
 *     validar o captcha no Google.
 */
@Configuration
public class UserConfig {

    // =========================================================
    // ADMINISTRADOR
    // =========================================================

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;


    // =========================================================
    // USUÁRIOS DE TESTE (temporário, enquanto o banco não funciona)
    // =========================================================

    @Value("${app.teste.paciente.username:}")
    private String pacienteTesteUsername;

    @Value("${app.teste.paciente.password:}")
    private String pacienteTestePassword;

    @Value("${app.teste.medico.username:}")
    private String medicoTesteUsername;

    @Value("${app.teste.medico.password:}")
    private String medicoTestePassword;


    // =========================================================
    // GOOGLE reCAPTCHA
    // =========================================================

    @Value("${recaptcha.site-key}")
    private String recaptchaSiteKey;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;


    // =========================================================
    // GETTERS - ADMINISTRADOR
    // =========================================================

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getAdminName() {
        return adminName;
    }


    // =========================================================
    // GETTERS - USUÁRIOS DE TESTE
    // =========================================================

    public String getPacienteTesteUsername() {
        return pacienteTesteUsername;
    }

    public String getPacienteTestePassword() {
        return pacienteTestePassword;
    }

    public String getMedicoTesteUsername() {
        return medicoTesteUsername;
    }

    public String getMedicoTestePassword() {
        return medicoTestePassword;
    }


    // =========================================================
    // GETTERS - reCAPTCHA
    // =========================================================

    public String getRecaptchaSiteKey() {
        return recaptchaSiteKey;
    }

    public String getRecaptchaSecretKey() {
        return recaptchaSecretKey;
    }
}
