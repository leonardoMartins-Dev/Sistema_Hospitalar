package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE PROFISSIONAIS DA SAÚDE
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * USA: ProfissionalSaudeRepository, AtendimentoRepository (disponibilidade e
 *      remoção), InternacaoRepository (remoção), PacienteService (e-mail
 *      repetido), PasswordEncoder (BCrypt)
 *
 * NÃO usa o ConsultaService: o ConsultaService já usa este service, e um
 * service usando o outro dos dois lados dá dependência circular no Spring.
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
 *   List<ProfissionalSaude> buscar(CategoriaProfissional categoria, Especialidade especialidade,
 *                                  LocalDate data, LocalTime horario)
 *     Atende o GET /profissionais com filtros opcionais:
 *       - especialidade informada -> repository.buscarEspecialistas(especialidade)
 *       - senão, findAll(); categoria informada -> filtra por p.getCategoria() == categoria
 *       - data E horário informados -> tira quem está ocupado naquele horário:
 *           atendimentoRepository.findByProfissionalIdAndDataAndHorario(p.getId(), data, horario)
 *               .stream().anyMatch(Atendimento::ocupaAgenda)
 *         Esse é o "profissionais DISPONÍVEIS por área de atuação" do enunciado.
 *
 *   ProfissionalSaude buscarPorId(Long id)       -> RecursoNaoEncontradoException
 *   Optional<ProfissionalSaude> buscarPorEmail(String email)
 *       -> login do médico (DatabaseUserDetailsService) e "médico logado"
 *          nos controllers. Normalize o e-mail antes de buscar.
 *
 *   @Transactional
 *   void remover(Long id)                        só o ADMIN
 *       Se atendimentoRepository.existsByProfissionalId(id) ou
 *       internacaoRepository.existsByProfissionalId(id) ->
 *       RegraNegocioException("Profissional com atendimentos não pode ser
 *       removido") (RN6: o histórico não pode perder referências).
 *
 *   List<Especialidade> listarEspecialidades()   -> Arrays.asList(Especialidade.values())
 */
