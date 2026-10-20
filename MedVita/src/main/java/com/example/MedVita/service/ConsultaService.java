package com.example.MedVita.service;

/*
 * =========================================================
 * SERVIÇO DE CONSULTAS  (cuida das QUATRO modalidades de atendimento)
 * =========================================================
 *
 * STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.
 * RESPONSÁVEL: Leonardo Martins (Sprint 2)
 *              Partes marcadas com [GONZAGA]: Leonardo Gonzaga
 *
 * O nome continua ConsultaService porque o endpoint é /consultas (decisão do
 * grupo), mas ele trabalha com Atendimento: rotina, especializada,
 * emergência e cirurgia.
 *
 * USA: AtendimentoRepository, PacienteService, ProfissionalSaudeService
 *
 * =========================================================
 * CRIAR UM ATENDIMENTO
 * =========================================================
 *   @Transactional
 *   Atendimento criar(AtendimentoRequest req)
 *     1. paciente = pacienteService.buscarPorId(req.pacienteId())
 *     2. Cria a subclasse certa. ESTE É O ÚNICO switch POR TIPO DO SERVICE
 *        (criar objeto é o único lugar onde é preciso saber a classe):
 *          ROTINA        -> new ConsultaRotina(paciente, profissional, data, horario, motivo)
 *          ESPECIALIZADA -> new ConsultaEspecializada(..., req.especialidade())
 *          EMERGENCIA    -> [GONZAGA] new AtendimentoEmergencia(paciente, motivo, prioridade)
 *          CIRURGIA      -> [GONZAGA] new ProcedimentoCirurgico(paciente, profissional,
 *                              data, horario, descricao, tipoCirurgia, obsPre)
 *        (profissional = profissionalService.buscarPorId(req.profissionalId()),
 *         menos na emergência)
 *     3. atendimento.validar()   cada modalidade confere as próprias regras
 *     4. Se atendimento.ocupaAgenda() e horarioOcupado(...) ->
 *        RegraNegocioException("O profissional já tem atendimento neste horário")   (RN3)
 *     5. return atendimentoRepository.save(atendimento)
 *
 *   private boolean horarioOcupado(Long profissionalId, LocalDate data, LocalTime horario)
 *       atendimentoRepository.findByProfissionalIdAndDataAndHorario(...)
 *           .stream().anyMatch(Atendimento::ocupaAgenda)
 *
 * =========================================================
 * MUDAR A SITUAÇÃO (polimorfismo: nenhum if por tipo aqui)
 * =========================================================
 * Todos são @Transactional, buscam com buscarPorId(id), chamam o método do
 * model e salvam. Quem decide se pode é o próprio model, que lança
 * RegraNegocioException se a mudança não for permitida.
 *
 *   iniciar(Long id, Long profissionalId)
 *       [GONZAGA] if (atendimento instanceof AtendimentoEmergencia e && profissionalId != null)
 *                     e.atribuirProfissional(profissionalService.buscarPorId(profissionalId));
 *       atendimento.iniciar();
 *   concluir(Long id, String observacoes)   -> atendimento.concluir(observacoes)
 *   cancelar(Long id)                       -> atendimento.cancelar()
 *
 * SÓ PARA CONSULTAS (rotina e especializada)
 *   private Consulta comoConsulta(Atendimento a)
 *       if (!(a instanceof Consulta c)) lança RegraNegocioException(
 *           "Esta operação só vale para consultas");
 *   confirmar(Long id)          -> comoConsulta(a).confirmar()
 *   registrarFalta(Long id)     -> comoConsulta(a).registrarNaoComparecimento()
 *   reagendar(Long id, LocalDate data, LocalTime horario)
 *       Consulta nova = comoConsulta(a).reagendar(data, horario);
 *       nova.validar(); confere a RN3; salva a NOVA. A antiga não muda.
 *
 * SÓ PARA CONSULTA DE ROTINA
 *   encaminhar(Long id, Especialidade especialidade, String emailMedicoLogado)
 *       - o atendimento tem que ser ConsultaRotina; senão RegraNegocioException
 *       - o médico logado (profissionalService.buscarPorEmail) tem que ser
 *         MedicoClinicoGeral; senão RegraNegocioException
 *       - medico.encaminhar(consulta, especialidade); salva
 *
 * =========================================================
 * CONSULTAS (LEITURA)
 * =========================================================
 *   Atendimento buscarPorId(Long id)  -> RecursoNaoEncontradoException se não existir
 *   List<Atendimento> listar(TipoAtendimento tipo)
 *       -> tipo null: todos; senão filtra por a.getTipo() == tipo
 *   List<Atendimento> listarDoPaciente(Long pacienteId)       -> tela paciente/consultas
 *   List<Atendimento> agendaDoProfissional(Long profissionalId, LocalDate data)
 *                                                             -> tela medico/consultas
 *   [GONZAGA] List<AtendimentoEmergencia> filaEmergencia(StatusEmergencia status)
 *       -> status null = AGUARDANDO_AVALIACAO; ordena CRITICA primeiro e
 *          depois por chegada (ver o Comparator no enum Prioridade)
 *   [GONZAGA] List<ProcedimentoCirurgico> listarProcedimentosDoCirurgiao(Long cirurgiaoId)
 *       -> confere que o profissional existe e é Cirurgiao (senão
 *          RegraNegocioException) e chama
 *          atendimentoRepository.buscarProcedimentosDoCirurgiao(...)
 *
 * CONCORRÊNCIA
 * Se dois pedidos passarem juntos pela RN3, o índice único do banco recusa o
 * segundo com DataIntegrityViolationException. O GlobalExceptionHandler
 * transforma isso em 409 com uma mensagem amigável.
 */
