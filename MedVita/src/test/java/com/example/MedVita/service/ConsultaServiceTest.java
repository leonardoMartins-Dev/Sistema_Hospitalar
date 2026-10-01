package com.example.MedVita.service;

/*
 * =========================================================
 * TESTES DO ConsultaService
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * COMO VAI SER
 * Teste com JUnit 5 e Mockito: os repositories viram objetos falsos (mocks),
 * então o teste não precisa do Supabase e testa só a regra de negócio.
 *
 * CASOS
 *   - agendar em horário livre -> salva como "AGENDADA"
 *   - agendar em horário ocupado do mesmo profissional
 *     -> RegraNegocioException (RN3)
 *   - agendar no horário de uma consulta cancelada -> permitido
 *   - cancelar uma consulta já realizada -> RegraNegocioException
 */
