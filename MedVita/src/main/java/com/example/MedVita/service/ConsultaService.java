package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE CONSULTAS
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: João Pedro (Sprint 2)
 *
 * Cria as consultas (rotina e especializada) e cuida dos fluxos GENÉRICOS
 * de qualquer atendimento (iniciar, concluir, cancelar, buscar). Os fluxos
 * genéricos funcionam também para emergência e cirurgia SEM este service
 * conhecer essas classes: ele só chama os métodos abstratos de Atendimento.
 * O que é só de emergência ou cirurgia fica no EmergenciaCirurgiaService
 * (Leonardo Gonzaga), em outro arquivo.
 *
 * USA (só repositórios, para não depender do service de ninguém)
 *   AtendimentoRepository          (desta parte)
 *   PacienteRepository             (já existe) -> findById
 *   PacienteService.buscarPorEmail (já existe) -> paciente logado
 *   ProfissionalSaudeRepository    (Leonardo Martins) -> findById, findByEmail,
 *                                   buscarEspecialistas
 *
 * =========================================================
 * CRIAR UMA CONSULTA
 * =========================================================
 *   @Transactional
 *   Consulta criar(ConsultaRequest req)
 *     1. paciente = pacienteRepository.findById(req.pacienteId())
 *                   .orElseThrow(() -> new RecursoNaoEncontradoException(...))
 *        profissional = profissionalSaudeRepository.findById(...) (idem)
 *     2. Cria a subclasse certa (o único switch por tipo deste service):
 *          ROTINA        -> new ConsultaRotina(paciente, profissional, data, horario, motivo)
 *          ESPECIALIZADA -> new ConsultaEspecializada(..., req.especialidade())
 *          outro tipo    -> RegraNegocioException("Emergência e cirurgia usam
 *                           /consultas/emergencias e /consultas/cirurgias")
 *     3. consulta.validar()   (a modalidade confere as próprias regras)
 *     4. horarioOcupado(...) -> RegraNegocioException("O profissional já tem
 *        atendimento neste horário")   (RN3)
 *     5. return atendimentoRepository.save(consulta)
 *
 *   private boolean horarioOcupado(Long profissionalId, LocalDate data, LocalTime horario)
 *       atendimentoRepository.findByProfissionalIdAndDataAndHorario(...)
 *           .stream().anyMatch(Atendimento::ocupaAgenda)
 *
 * =========================================================
 * FLUXOS GENÉRICOS (valem para as 4 modalidades; nenhum if por tipo)
 * =========================================================
 * Todos são @Transactional, buscam com buscarPorId(id), chamam o método do
 * model e salvam. Quem decide se pode é o model, que lança
 * RegraNegocioException se a mudança não for permitida.
 *   iniciar(Long id)                       -> atendimento.iniciar()
 *   concluir(Long id, String observacoes)  -> atendimento.concluir(observacoes)
 *   cancelar(Long id)                      -> atendimento.cancelar()
 *
 * SÓ PARA CONSULTAS
 *   private Consulta comoConsulta(Atendimento a)
 *       if (!(a instanceof Consulta c)) lança RegraNegocioException(
 *           "Esta operação só vale para consultas");
 *   confirmar(Long id)          -> comoConsulta(a).confirmar()
 *   registrarFalta(Long id)     -> comoConsulta(a).registrarNaoComparecimento()
 *   reagendar(Long id, LocalDate data, LocalTime horario)
 *       Consulta nova = comoConsulta(a).reagendar(data, horario);
 *       nova.validar(); confere a RN3; salva a NOVA. A antiga não muda.
 *   encaminhar(Long id, Especialidade especialidade, String emailMedicoLogado)
 *       - o atendimento tem que ser ConsultaRotina; senão RegraNegocioException
 *       - solicitante = profissionalSaudeRepository.findByEmail(emailMedicoLogado)
 *       - rotina.registrarEncaminhamento(especialidade, solicitante); salva
 *         (o model confere se o solicitante é o médico da consulta e se
 *         solicitante.podeEncaminhar())
 *
 * =========================================================
 * LEITURAS
 * =========================================================
 *   Atendimento buscarPorId(Long id)  -> RecursoNaoEncontradoException se não existir
 *   List<Atendimento> listar(TipoAtendimento tipo)
 *       -> tipo null: todos; senão filtra por a.getTipo() == tipo
 *   List<Atendimento> listarDoPaciente(Long pacienteId)       -> tela paciente/consultas
 *   List<Atendimento> agendaDoProfissional(Long profissionalId, LocalDate data)
 *                                                             -> tela medico/consultas
 *   List<ProfissionalSaude> profissionaisDisponiveis(Especialidade especialidade,
 *                                                    LocalDate data, LocalTime horario)
 *       -> select de médicos da tela de agendamento ("profissionais disponíveis
 *          por área de atuação"):
 *          - especialidade null (consulta de rotina): profissionalSaudeRepository.findAll()
 *            filtrado por p.getCategoria() == CLINICO_GERAL
 *          - senão: profissionalSaudeRepository.buscarEspecialistas(especialidade)
 *          - tira quem está com horarioOcupado(p.getId(), data, horario)
 *          Fica aqui (e não em profissionais) porque quem conhece a agenda são
 *          os atendimentos.
 *
 * CONCORRÊNCIA
 * Se dois pedidos passarem juntos pela RN3, o índice único do banco recusa o
 * segundo com DataIntegrityViolationException. O GlobalExceptionHandler
 * transforma isso em 409 com uma mensagem amigável.
 */
