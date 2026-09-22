package com.example.MedVita.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    // =========================================================
    // USUÁRIO
    // =========================================================

    @Value("${app.user.username}")
    private String userUsername;

    @Value("${app.user.password}")
    private String userPassword;

    @Value("${app.user.name}")
    private String userName;


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
    // MÉDICO
    // =========================================================

    @Value("${app.medico.username}")
    private String medicoUsername;

    @Value("${app.medico.password}")
    private String medicoPassword;

    @Value("${app.medico.name}")
    private String medicoName;


    // =========================================================
    // GOOGLE reCAPTCHA
    // =========================================================

    @Value("${recaptcha.site-key}")
    private String recaptchaSiteKey;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;


    // =========================================================
    // GETTERS - USUÁRIO
    // =========================================================

    public String getUserUsername() {
        return userUsername;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }


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
    // GETTERS - MÉDICO
    // =========================================================

    public String getMedicoUsername() {
        return medicoUsername;
    }

    public String getMedicoPassword() {
        return medicoPassword;
    }

    public String getMedicoName() {
        return medicoName;
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