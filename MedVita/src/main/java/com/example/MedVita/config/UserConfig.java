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
    // GETTERS - reCAPTCHA
    // =========================================================

    public String getRecaptchaSiteKey() {
        return recaptchaSiteKey;
    }

    public String getRecaptchaSecretKey() {
        return recaptchaSecretKey;
    }
}
