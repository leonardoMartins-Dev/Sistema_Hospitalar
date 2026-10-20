package com.example.MedVita.service;

/*
 * =========================================================
 * TESTES DO InternacaoService
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
 *   - internar em quarto com vaga -> ocupação do quarto +1
 *   - internar em quarto lotado -> RegraNegocioException (RN5)
 *   - internar paciente que já está internado -> RegraNegocioException
 *   - dar alta -> preenche a data efetiva e a ocupação do quarto -1
 */
