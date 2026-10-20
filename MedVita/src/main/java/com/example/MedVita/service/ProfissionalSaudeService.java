package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE PROFISSIONAIS DA SAÚDE
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *
 * USA: ProfissionalSaudeRepository, PacienteService (e-mail repetido; também
 *      desta parte), PasswordEncoder (BCrypt)
 * NÃO usa nada de consultas, emergência ou internações: esta parte não
 * depende de ninguém. Os outros services também não usam este: vão direto
 * no ProfissionalSaudeRepository.
 *
 * MÉTODOS
 *   @Transactional
 *   ProfissionalSaude cadastrar(ProfissionalRequest req)        só o ADMIN
 *     1. Normaliza o e-mail (trim + minúsculo, igual ao PacienteService).
 *     2. E-mail repetido (nesta tabela OU na de pacientes, com
 *        pacienteService.existe(email)) -> RegraNegocioException.
 *        Precisa conferir as duas tabelas porque o login procura nas duas.
 *     3. Registro repetido -> RegraNegocioException.
 *     4. Cria a subclasse certa (o único switch por categoria do sistema):
 *          CLINICO_GERAL -> new MedicoClinicoGeral(...)
 *          ESPECIALISTA  -> especialidade obrigatória -> new MedicoEspecialista(..., esp)
 *          CIRURGIAO     -> areaCirurgica obrigatória -> new Cirurgiao(..., area)
 *        Sempre com passwordEncoder.encode(req.senha()).
 *     5. save.
 *
 *   List<ProfissionalSaude> buscar(CategoriaProfissional categoria, Especialidade especialidade)
 *     Atende o GET /profissionais com filtros opcionais:
 *       - especialidade informada -> repository.buscarEspecialistas(especialidade)
 *         ("consulta dos profissionais por área de atuação")
 *       - senão, findAll(); categoria informada -> filtra por p.getCategoria() == categoria
 *     Quem está LIVRE num horário é outra pergunta, respondida pela parte de
 *     consultas (GET /consultas/profissionais-disponiveis).
 *
 *   ProfissionalSaude buscarPorId(Long id)       -> RecursoNaoEncontradoException
 *   Optional<ProfissionalSaude> buscarPorEmail(String email)
 *       -> login do médico (DatabaseUserDetailsService). Normalize o e-mail.
 *
 *   @Transactional
 *   void remover(Long id)                        só o ADMIN
 *       - buscarPorId(id) (404 se não existir)
 *       - try { repository.delete(p); repository.flush(); }
 *         catch (DataIntegrityViolationException e) {
 *             throw new RegraNegocioException("Profissional com atendimentos ou
 *                 internações não pode ser removido"); }
 *       Quem impede a remoção é a chave estrangeira do banco (RN6, como diz a
 *       JustificativaBancoRelacional). Por isso este service não precisa
 *       consultar atendimentos nem internações, e não depende dessas partes.
 *       O flush() força o DELETE a acontecer dentro do try.
 *
 *   List<Map<String, String>> listarEspecialidades()
 *       -> [{"valor": "CARDIOLOGIA", "nome": "Cardiologia"}, ...] para os selects:
 *          "valor" é o que a tela manda de volta; "nome" é o que aparece.
 */
