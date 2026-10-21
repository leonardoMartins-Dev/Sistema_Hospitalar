package com.example.MedVita.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MedVita.dto.ProfissionalRequest;
import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.Cirurgiao;
import com.example.MedVita.models.MedicoClinicoGeral;
import com.example.MedVita.models.MedicoEspecialista;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.repository.ProfissionalSaudeRepository;

@Service
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository profissionalRepository;
    private final PacienteService pacienteService;
    private final PasswordEncoder passwordEncoder;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalRepository,
            PacienteService pacienteService, PasswordEncoder passwordEncoder) {
        this.profissionalRepository = profissionalRepository;
        this.pacienteService = pacienteService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ProfissionalSaude cadastrar(ProfissionalRequest req) {
        if (req.tipo() == null) {
            throw new RegraNegocioException("Escolha a categoria do profissional");
        }
        obrigatorio(req.nome(), "nome");
        obrigatorio(req.registroProfissional(), "registro profissional");
        obrigatorio(req.telefone(), "telefone");
        obrigatorio(req.email(), "e-mail");
        obrigatorio(req.senha(), "senha");

        String email = normalizar(req.email());
        String registro = req.registroProfissional().trim();

        // o login procura nas duas tabelas, então o e-mail não pode existir em nenhuma
        if (profissionalRepository.existsByEmail(email) || pacienteService.existe(email)) {
            throw new RegraNegocioException("Já existe um usuário com este e-mail");
        }
        if (profissionalRepository.existsByRegistroProfissional(registro)) {
            throw new RegraNegocioException("Já existe um profissional com este registro");
        }

        String hash = passwordEncoder.encode(req.senha());

        // O único switch por categoria do sistema: para criar o objeto é preciso saber a classe
        ProfissionalSaude profissional = switch (req.tipo()) {
            case CLINICO_GERAL -> new MedicoClinicoGeral(req.nome(), registro, req.telefone(), email, hash);
            case ESPECIALISTA -> {
                if (req.especialidade() == null) {
                    throw new RegraNegocioException("Informe a especialidade");
                }
                yield new MedicoEspecialista(req.nome(), registro, req.telefone(), email, hash,
                        req.especialidade());
            }
            case CIRURGIAO -> {
                obrigatorio(req.areaCirurgica(), "área cirúrgica");
                yield new Cirurgiao(req.nome(), registro, req.telefone(), email, hash,
                        req.areaCirurgica().trim());
            }
        };

        return profissionalRepository.save(profissional);
    }

    public List<ProfissionalSaude> buscar(CategoriaProfissional categoria, Especialidade especialidade) {
        if (especialidade != null) {
            return new ArrayList<>(profissionalRepository.buscarEspecialistas(especialidade));
        }
        return profissionalRepository.findAll(Sort.by("nome")).stream()
                .filter(p -> categoria == null || p.getCategoria() == categoria)   // polimorfismo
                .toList();
    }

    public ProfissionalSaude buscarPorId(Long id) {
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional " + id + " não encontrado"));
    }

    public Optional<ProfissionalSaude> buscarPorEmail(String email) {
        return profissionalRepository.findByEmail(normalizar(email));
    }

    @Transactional
    public void remover(Long id) {
        ProfissionalSaude profissional = buscarPorId(id);
        try {
            profissionalRepository.delete(profissional);
            profissionalRepository.flush();   // força o DELETE a acontecer aqui dentro do try
        } catch (DataIntegrityViolationException e) {
            // a chave estrangeira de atendimentos/internações recusou: o médico tem histórico (RN6)
            throw new RegraNegocioException("Profissional com atendimentos ou internações não pode ser removido");
        }
    }

    public List<Map<String, String>> listarEspecialidades() {
        return Arrays.stream(Especialidade.values())
                .map(e -> Map.of("valor", e.name(), "nome", e.getNome()))
                .toList();
    }

    private void obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("Preencha o campo " + campo);
        }
    }

    // igual ao do PacienteService: sem espaços e em minúsculo
    private String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}

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
