package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE PROFISSIONAIS DA SAÚDE
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 *
 * USA: ProfissionalSaudeRepository, PasswordEncoder (BCrypt)
 *
 * MÉTODOS
 *   cadastrar(nome, registro, especialidade, telefone, email, senha)
 *       Só o ADMIN, pela página admin/medicos.
 *       E-mail ou registro repetido -> RegraNegocioException.
 *       Salva a senha em BCrypt, igual ao PacienteService faz com o paciente.
 *   listar()                         -> tela admin/medicos
 *   listarPorEspecialidade(especialidade) -> select da tela de agendamento
 *   buscarPorId(id)                  -> RecursoNaoEncontradoException se não existir
 *   buscarPorEmail(email)            -> login do médico e médico logado
 *   remover(id)                      -> botão de remover da tela admin/medicos.
 *                                       Só se o médico não tiver consultas nem
 *                                       internações; senão RegraNegocioException
 *                                       (o histórico não pode ser perdido, RN6).
 */
