package com.example.MedVita.service;

/*
 * =========================================================
 * TESTES DO ProfissionalSaudeService
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * ADIADO: os testes ficam fora da Sprint 2 (decisão do grupo). Quando forem
 * feitos, atualizar os casos para o modelo novo (Atendimento e os enums de
 * situação no lugar de "AGENDADA"/"REALIZADA" em texto).
 *
 * COMO VAI SER
 * Teste com JUnit 5 e Mockito: os repositories viram objetos falsos (mocks),
 * então o teste não precisa do Supabase e testa só a regra de negócio.
 *
 * CASOS
 *   - cadastrar com e-mail repetido -> RegraNegocioException
 *   - cadastrar com registro repetido -> RegraNegocioException
 *   - a senha é salva em BCrypt, nunca em texto puro
 *   - remover médico com consultas -> RegraNegocioException
 */
