# Passo a passo: Leonardo Martins (Sprint 2)

**Sua parte:** pessoas e acesso. Isso inclui profissionais da saúde (com a herança e o polimorfismo), pacientes, login do médico, segurança e as exceções do sistema.

Este guia está **na ordem em que você deve fazer**. Cada passo diz o arquivo, o que escrever, como conferir e o que pode dar errado. Os trechos de código são o caminho sugerido; adapte se quiser, mas **mantenha os nomes de classes e métodos**, porque os outros três programam contando com eles.

---

## Resumo (para ir marcando)

**Fase 0: preparação**
- [x] Passo 0: subir a documentação da Sprint 2 para a `main`

**Fase 1: contratos (até o 2º dia; os outros esperam por isso)**
- [x] Passo 1: exceções
- [x] Passo 2: enums
- [x] Passo 3: `ProfissionalSaude`
- [x] Passo 4: as 3 subclasses
- [x] Passo 5: `ProfissionalSaudeRepository`
- [ ] Passo 6: conferir, PR e avisar o grupo

**Fase 2: regras e API**
- [ ] Passo 7: `GlobalExceptionHandler`
- [ ] Passo 8: `SecurityConfig`
- [ ] Passo 9: `ProfissionalRequest`
- [ ] Passo 10: `ProfissionalSaudeService`
- [ ] Passo 11: `ProfissionalSaudeController`
- [ ] Passo 12: login do médico (`DatabaseUserDetailsService`)
- [ ] Passo 13: pacientes (model, repository, service, cadastro, controller)
- [ ] Passo 14: testar as rotas GET no navegador

**Fase 3: telas**
- [ ] Passo 15: preparar as páginas (CSRF + `api.js`)
- [ ] Passo 16: `admin/medicos`
- [ ] Passo 17: `medico/pacientes`
- [ ] Passo 18: `paciente/perfil`
- [ ] Passo 19 (opcional): números do painel `admin/admin`

**Fase 4: fechamento**
- [ ] Passo 20: roteiro de teste da sua parte
- [ ] Passo 21: PR final

---

## Antes de tudo: por que você vai primeiro

Na divisão, **você não depende de ninguém, e todos dependem de você**:
- o João Pedro precisa do `ProfissionalSaude` (o `Atendimento` tem um profissional), do `TipoAtendimento`, do `Especialidade`, do `ProfissionalSaudeRepository` e das exceções;
- o Leonardo Gonzaga e o Henrique precisam das mesmas classes.

Eles não precisam da sua parte *pronta*, só das classes **existindo na `main`** com os nomes combinados. Por isso a fase 1 é pequena e tem que sair rápido: é ela que destrava o grupo.

Comandos úteis (o `./mvnw` do projeto está quebrado; use o `mvn` instalado):
```bash
cd MedVita
mvn -q compile          # só compila (rápido, não precisa de banco)
mvn spring-boot:run     # sobe o site em http://localhost:8080
```

---

# Fase 0: preparação

## Passo 0: subir a documentação da Sprint 2 para a `main`

Os comentários de todas as classes, os diagramas e os documentos da Sprint 2 estão na sua branch `Leonardo`, sem commit. Os outros precisam deles para começar.

```bash
cd "Sistema_Hospitalar"
git checkout Leonardo
git status                      # confira a lista
git add -A
git commit -m "Sprint 2: documentação das classes, diagramas e divisão de tarefas"
git push origin Leonardo
```
Abra o PR `Leonardo → main` no GitHub (ou `gh pr create --base main --head Leonardo`), faça o merge e avise no grupo: *"documentação da Sprint 2 na main, leiam DOCUMENTOS/Sprint2/DivisaoTarefas.md"*.

Depois, sempre que for começar a trabalhar:
```bash
git checkout Leonardo
git pull origin main
```

> **Como escrever o código nos arquivos:** cada classe já tem um comentário grande no topo, explicando o que fazer. **Mantenha o comentário** e escreva o código embaixo dele. Quando terminar a classe, troque a linha `STATUS: A IMPLEMENTAR. Por enquanto este arquivo é só documentação.` por `STATUS: IMPLEMENTADO (Sprint 2).` e apague as partes do comentário que ficaram desatualizadas.

---

# Fase 1: contratos

## Passo 1: exceções

**Arquivos:** `exception/RegraNegocioException.java` e `exception/RecursoNaoEncontradoException.java`

São iguais à `SendEmailException`, que já existe. Embaixo do comentário de cada arquivo:

```java
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String message) {
        super(message);
    }
}
```

```java
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
```

- `RegraNegocioException`: qualquer regra do hospital quebrada → vai virar HTTP 400.
- `RecursoNaoEncontradoException`: busca por id que não achou nada → vai virar HTTP 404.
(Quem transforma em 400/404 é o `GlobalExceptionHandler`, no passo 7. Por enquanto só precisam existir.)

## Passo 2: enums

**Arquivos:** `models/enums/Especialidade.java`, `models/enums/CategoriaProfissional.java` e `models/enums/TipoAtendimento.java`

O pacote é `com.example.MedVita.models.enums` (a linha `package` já está no arquivo).

```java
public enum Especialidade {
    CARDIOLOGIA("Cardiologia"),
    DERMATOLOGIA("Dermatologia"),
    GINECOLOGIA("Ginecologia"),
    NEUROLOGIA("Neurologia"),
    ORTOPEDIA("Ortopedia"),
    PEDIATRIA("Pediatria");

    private final String nome;

    Especialidade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
```

```java
public enum CategoriaProfissional {
    CLINICO_GERAL("Médico clínico geral"),
    ESPECIALISTA("Médico especialista"),
    CIRURGIAO("Cirurgião");

    private final String descricao;

    CategoriaProfissional(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
```

```java
public enum TipoAtendimento {
    ROTINA("Consulta de rotina"),
    ESPECIALIZADA("Consulta especializada"),
    EMERGENCIA("Atendimento de emergência"),
    CIRURGIA("Procedimento cirúrgico");

    private final String descricao;

    TipoAtendimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
```

**Não mude os nomes dos valores** (`CLINICO_GERAL`, `ROTINA`...). Eles são gravados no banco e são os `@DiscriminatorValue` das classes; o Gonzaga escreve o `schema.sql` com esses textos.

O `TipoAtendimento` é seu, mesmo sendo "de atendimento", porque é ele que o `podeRealizar()` recebe. É isso que faz a sua parte não depender da do João.

## Passo 3: `ProfissionalSaude` (a classe abstrata)

**Arquivo:** `models/ProfissionalSaude.java`

Esta é a classe mais importante para a apresentação: é aqui que está a **herança** e é ela que define o **polimorfismo**.

```java
import com.example.MedVita.exception.RegraNegocioException;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "profissionais_saude")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // as 3 subclasses na mesma tabela
@DiscriminatorColumn(name = "tipo")                      // coluna que diz qual subclasse é
public abstract class ProfissionalSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "registro_profissional", nullable = false, unique = true)
    private String registroProfissional;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore   // a senha (mesmo em hash) nunca sai no JSON
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // JPA precisa de um construtor vazio
    protected ProfissionalSaude() {
    }

    protected ProfissionalSaude(String nome, String registroProfissional, String telefone,
            String email, String passwordHash) {
        this.nome = nome;
        this.registroProfissional = registroProfissional;
        this.telefone = telefone;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // ===== POLIMORFISMO: cada categoria responde do seu jeito =====

    public abstract CategoriaProfissional getCategoria();

    public abstract String getAreaAtuacao();

    public abstract boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade);

    public abstract boolean podeEncaminhar();

    // Escrito uma vez só, vale para as 3 subclasses. Não tem nenhum "if que tipo
    // de médico é": quem decide é o podeRealizar() de cada subclasse.
    public void verificarSePodeRealizar(TipoAtendimento tipo, Especialidade especialidade) {
        if (!podeRealizar(tipo, especialidade)) {
            throw new RegraNegocioException(nome + " (" + getAreaAtuacao()
                    + ") não pode realizar " + tipo.getDescricao());
        }
    }

    // ===== GETTERS E SETTERS =====
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRegistroProfissional() { return registroProfissional; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
```

**Por que assim:**
- `abstract`: não existe "profissional genérico", só as 3 categorias.
- `SINGLE_TABLE` + `@DiscriminatorColumn("tipo")`: uma tabela só. O Hibernate grava `tipo` sozinho e, ao ler, cria a subclasse certa.
- `getCategoria()` e `getAreaAtuacao()` são getters, então **saem no JSON** como `"categoria"` e `"areaAtuacao"`; as telas usam os dois. Já `podeRealizar(...)` e `podeEncaminhar()` não são getters e não saem no JSON.
- `podeRealizar` recebe o **tipo** (enum), não o objeto `Atendimento`. Se recebesse o objeto, a sua classe precisaria das classes do João e do Gonzaga para compilar.

## Passo 4: as 3 subclasses

**Arquivos:** `models/MedicoClinicoGeral.java`, `models/MedicoEspecialista.java` e `models/Cirurgiao.java`

Imports das três (ajuste o que cada uma usar):
```java
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.models.enums.TipoAtendimento;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
```

**`MedicoClinicoGeral`**
```java
@Entity
@DiscriminatorValue("CLINICO_GERAL")
public class MedicoClinicoGeral extends ProfissionalSaude {

    protected MedicoClinicoGeral() {
    }

    public MedicoClinicoGeral(String nome, String registroProfissional, String telefone,
            String email, String passwordHash) {
        super(nome, registroProfissional, telefone, email, passwordHash);
    }

    @Override
    public CategoriaProfissional getCategoria() {
        return CategoriaProfissional.CLINICO_GERAL;
    }

    @Override
    public String getAreaAtuacao() {
        return "Clínica geral";
    }

    // Consultas de rotina e emergências
    @Override
    public boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade) {
        return tipo == TipoAtendimento.ROTINA || tipo == TipoAtendimento.EMERGENCIA;
    }

    // "Pode encaminhar pacientes para atendimento especializado"
    @Override
    public boolean podeEncaminhar() {
        return true;
    }
}
```

**`MedicoEspecialista`**
```java
@Entity
@DiscriminatorValue("ESPECIALISTA")
public class MedicoEspecialista extends ProfissionalSaude {

    @Enumerated(EnumType.STRING)   // grava "CARDIOLOGIA", nunca o número
    @Column(name = "especialidade")
    private Especialidade especialidade;

    protected MedicoEspecialista() {
    }

    public MedicoEspecialista(String nome, String registroProfissional, String telefone,
            String email, String passwordHash, Especialidade especialidade) {
        super(nome, registroProfissional, telefone, email, passwordHash);
        this.especialidade = especialidade;
    }

    @Override
    public CategoriaProfissional getCategoria() {
        return CategoriaProfissional.ESPECIALISTA;
    }

    @Override
    public String getAreaAtuacao() {
        return especialidade.getNome();
    }

    public boolean atendeEspecialidade(Especialidade outra) {
        return especialidade == outra;
    }

    // Emergência, ou consulta especializada DA MESMA especialidade
    @Override
    public boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidadePedida) {
        if (tipo == TipoAtendimento.EMERGENCIA) {
            return true;
        }
        return tipo == TipoAtendimento.ESPECIALIZADA && atendeEspecialidade(especialidadePedida);
    }

    @Override
    public boolean podeEncaminhar() {
        return false;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }
}
```

**`Cirurgiao`**
```java
@Entity
@DiscriminatorValue("CIRURGIAO")
public class Cirurgiao extends ProfissionalSaude {

    @Column(name = "area_cirurgica")
    private String areaCirurgica;

    protected Cirurgiao() {
    }

    public Cirurgiao(String nome, String registroProfissional, String telefone,
            String email, String passwordHash, String areaCirurgica) {
        super(nome, registroProfissional, telefone, email, passwordHash);
        this.areaCirurgica = areaCirurgica;
    }

    @Override
    public CategoriaProfissional getCategoria() {
        return CategoriaProfissional.CIRURGIAO;
    }

    @Override
    public String getAreaAtuacao() {
        return areaCirurgica;
    }

    // Só procedimentos cirúrgicos (decisão do grupo)
    @Override
    public boolean podeRealizar(TipoAtendimento tipo, Especialidade especialidade) {
        return tipo == TipoAtendimento.CIRURGIA;
    }

    @Override
    public boolean podeEncaminhar() {
        return false;
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }
}
```

**Não crie** uma `List<ProcedimentoCirurgico>` no `Cirurgiao`: isso faria a sua parte depender da do Gonzaga e o JSON entraria em loop. A lista de procedimentos sai em `GET /consultas/procedimentos?cirurgiaoId=` (parte dele).

## Passo 5: `ProfissionalSaudeRepository`

**Arquivo:** `repository/ProfissionalSaudeRepository.java`

```java
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MedVita.models.MedicoEspecialista;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.Especialidade;

public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

    Optional<ProfissionalSaude> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRegistroProfissional(String registroProfissional);

    // "especialidade" só existe na subclasse, por isso precisa de @Query
    @Query("select m from MedicoEspecialista m where m.especialidade = :especialidade order by m.nome")
    List<MedicoEspecialista> buscarEspecialistas(@Param("especialidade") Especialidade especialidade);
}
```

O `findAll()` e o `findById()` (que vêm do `JpaRepository`) já devolvem cada profissional como a subclasse certa. Os outros três usam `findById` e `findByEmail` daqui direto.

## Passo 6: conferir, PR e avisar o grupo

1. **Compilar:** `mvn -q compile` (de dentro de `MedVita/`). Sem erro = ok.
2. **Subir o site:** `mvn spring-boot:run`. Erro de mapeamento JPA aparece aqui (ex.: anotação errada). Se subir e o login de teste (`admin`/`1234`) funcionar, está ok.
3. **Conferir os nomes** que os outros vão usar:
   - `podeRealizar(TipoAtendimento, Especialidade)`, `podeEncaminhar()`, `verificarSePodeRealizar(TipoAtendimento, Especialidade)`, `getCategoria()`, `getAreaAtuacao()`
   - `findById`, `findByEmail`, `buscarEspecialistas`
   - `RegraNegocioException(String)`, `RecursoNaoEncontradoException(String)`
4. **PR pequeno** só com isso:
   ```bash
   git add MedVita/src/main/java/com/example/MedVita/exception MedVita/src/main/java/com/example/MedVita/models MedVita/src/main/java/com/example/MedVita/repository/ProfissionalSaudeRepository.java
   git commit -m "Contratos: ProfissionalSaude, subclasses, enums, repositório e exceções"
   git push origin Leonardo
   ```
   Abra o PR para a `main` e peça merge rápido.
5. **Avise no grupo:** *"contratos dos profissionais e exceções na main, podem dar pull"*.

> Para testar com banco, a tabela `profissionais_saude` precisa das colunas novas (`tipo`, `area_cirurgica`, `especialidade` aceitando null), e isso vem do `schema.sql` do Gonzaga. Até lá dá para programar e compilar normalmente.

---

# Fase 2: regras e API

## Passo 7: `GlobalExceptionHandler`

**Arquivo:** `exception/GlobalExceptionHandler.java` (já existe, com 2 handlers)

Acrescente um método auxiliar e os novos handlers. Imports novos:
```java
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
```

```java
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> handleRegraNegocio(RegraNegocioException ex) {
        log.warn("Regra de negócio: {}", ex.getMessage());
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConflito(DataIntegrityViolationException ex) {
        log.warn("Conflito no banco", ex);
        return resposta(HttpStatus.CONFLICT,
                "A operação conflita com dados já cadastrados (registro repetido ou em uso).");
    }

    // JSON com valor inválido (ex.: "tipo": "XPTO") ou parâmetro inválido (?categoria=XPTO)
    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleDadosInvalidos(Exception ex) {
        log.warn("Dados inválidos: {}", ex.getMessage());
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.");
    }

    // IMPORTANTE: sem este, o @PreAuthorize negado cairia no handleGenericException e viraria 500
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAcessoNegado(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN, "Você não tem permissão para esta ação.");
    }

    private ResponseEntity<Object> resposta(HttpStatus status, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", mensagem);
        return new ResponseEntity<>(body, status);
    }
```
Se quiser, use o `resposta(...)` também nos dois handlers que já existem, para não repetir o `Map`.

**Por que o `AccessDeniedException`:** este `@ControllerAdvice` tem um handler para `Exception` (qualquer erro). Quando o `@PreAuthorize` nega acesso, ele lança `AccessDeniedException` *dentro* do controller, e o handler genérico pegaria e devolveria **500**. Com este handler a resposta vira o **403** correto.

## Passo 8: `SecurityConfig`

**Arquivo:** `config/SecurityConfig.java` (já existe)

1. Acrescente `@EnableMethodSecurity` na classe (import `org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity`):
   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity      // liga o @PreAuthorize nos controllers
   public class SecurityConfig {
   ```
2. Libere o JavaScript estático, junto com `/css/**` e `/images/**`:
   ```java
   .requestMatchers(HttpMethod.GET, "/js/**").permitAll()
   ```
3. **Não** desligue o CSRF: as telas vão mandar o token (passo 15).
4. As rotas da API (`/pacientes`, `/profissionais`...) já exigem login pelo `anyRequest().authenticated()`. O controle de quem pode o quê fica no `@PreAuthorize` de cada método.

Este arquivo é só seu: se alguém do grupo precisar de uma regra aqui, vai pedir a você.

## Passo 9: `ProfissionalRequest`

**Arquivo:** `dto/ProfissionalRequest.java`

```java
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;

public record ProfissionalRequest(
        CategoriaProfissional tipo,
        String nome,
        String registroProfissional,
        String telefone,
        String email,
        String senha,
        Especialidade especialidade,   // só ESPECIALISTA
        String areaCirurgica) {        // só CIRURGIAO
}
```
**Por que existe:** o Jackson não consegue criar `ProfissionalSaude` (é abstrata), e o formulário manda `senha`, enquanto a entidade tem `passwordHash`.

## Passo 10: `ProfissionalSaudeService`

**Arquivo:** `service/ProfissionalSaudeService.java`

```java
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
```

**Pontos de atenção:**
- **E-mail normalizado no cadastro e na busca.** Senão o médico cadastrado como `Ana@Medvita.com` não consegue logar com `ana@medvita.com`.
- **`remover` não consulta atendimentos nem internações.** Quem barra é o banco. Por isso a sua parte não depende das partes de consultas e internações.
- **Não dá dependência circular:** este service usa o `PacienteService`, que é seu e não usa este.

## Passo 11: `ProfissionalSaudeController`

**Arquivo:** `controller/ProfissionalSaudeController.java`

```java
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.dto.ProfissionalRequest;
import com.example.MedVita.models.ProfissionalSaude;
import com.example.MedVita.models.enums.CategoriaProfissional;
import com.example.MedVita.models.enums.Especialidade;
import com.example.MedVita.service.ProfissionalSaudeService;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSaudeController {

    private final ProfissionalSaudeService profissionalSaudeService;

    public ProfissionalSaudeController(ProfissionalSaudeService profissionalSaudeService) {
        this.profissionalSaudeService = profissionalSaudeService;
    }

    // GET /profissionais, /profissionais?categoria=CIRURGIAO, /profissionais?especialidade=CARDIOLOGIA
    @GetMapping
    public List<ProfissionalSaude> listar(
            @RequestParam(name = "categoria", required = false) CategoriaProfissional categoria,
            @RequestParam(name = "especialidade", required = false) Especialidade especialidade) {
        return profissionalSaudeService.buscar(categoria, especialidade);
    }

    @GetMapping("/especialidades")
    public List<Map<String, String>> especialidades() {
        return profissionalSaudeService.listarEspecialidades();
    }

    @GetMapping("/{id}")
    public ProfissionalSaude buscarPorId(@PathVariable Long id) {
        return profissionalSaudeService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfissionalSaude> cadastrar(@RequestBody ProfissionalRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profissionalSaudeService.cadastrar(req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        profissionalSaudeService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
```

Exemplo do JSON que sai num `GET /profissionais`:
```json
[
  { "id": 1, "nome": "Ana Souza", "registroProfissional": "CRM 1-MG", "telefone": "31999990000",
    "email": "ana@medvita.com", "especialidade": "CARDIOLOGIA",
    "categoria": "ESPECIALISTA", "areaAtuacao": "Cardiologia" },
  { "id": 2, "nome": "Bruno Lima", "registroProfissional": "CRM 2-MG", "telefone": "31999990001",
    "email": "bruno@medvita.com", "categoria": "CLINICO_GERAL", "areaAtuacao": "Clínica geral" }
]
```
Cada item sai com os campos da **própria subclasse** (o especialista tem `especialidade`, o clínico não). O `passwordHash` não aparece.

## Passo 12: login do médico (`DatabaseUserDetailsService`)

**Arquivo:** `service/DatabaseUserDetailsService.java` (já existe)

1. Novo atributo e parâmetro no construtor:
   ```java
   private final ProfissionalSaudeService profissionalSaudeService;

   public DatabaseUserDetailsService(PacienteService pacienteService,
           ProfissionalSaudeService profissionalSaudeService,
           UserConfig userConfig, PasswordEncoder passwordEncoder) {
       this.pacienteService = pacienteService;
       this.profissionalSaudeService = profissionalSaudeService;
       // ... o resto continua igual
   ```
2. Troque o passo "2. PACIENTE" do `loadUserByUsername` (o `orElseThrow` atual) por:
   ```java
        // 2. PACIENTE (tabela pacientes)
        Optional<Paciente> paciente = pacienteService.buscarPorEmail(username);
        if (paciente.isPresent()) {
            return User.builder()
                    .username(paciente.get().getEmail())
                    .password(paciente.get().getPasswordHash())
                    .roles("USER")
                    .build();
        }

        // 3. MÉDICO (tabela profissionais_saude), qualquer uma das 3 categorias
        ProfissionalSaude profissional = profissionalSaudeService.buscarPorEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(profissional.getEmail())
                .password(profissional.getPasswordHash())
                .roles("MEDICO")
                .build();
   ```
   Imports novos: `java.util.Optional` e `com.example.MedVita.models.ProfissionalSaude`.

O `successHandler` do `SecurityConfig` já manda `ROLE_MEDICO` para `/medico`, então não precisa mexer em mais nada. Atualize o comentário do topo do arquivo: o passo 3 deixou de ser "a criar".

## Passo 13: pacientes

### 13.1 `Paciente.java`
Coloque `@JsonIgnore` em cima do `passwordHash` (import `com.fasterxml.jackson.annotation.JsonIgnore`):
```java
    @JsonIgnore
    @Column (name = "password_hash", nullable = false)
    private String passwordHash;
```

### 13.2 `PacienteRepository.java`
```java
    boolean existsByCpf(String cpf);

    List<Paciente> findByNomeContainingIgnoreCaseOrCpfContainingOrEmailContainingIgnoreCase(
            String nome, String cpf, String email);
```
(import `java.util.List`)

### 13.3 `PacienteService.java`
Imports novos: `java.util.List`, `org.springframework.data.domain.Sort`, `RecursoNaoEncontradoException` e `RegraNegocioException`.

```java
        // no começo do cadastrar(...), antes de criar o Paciente:
        if (existeCpf(cpf)) {
            throw new RegraNegocioException("Já existe um paciente com este CPF");
        }
```

```java
        //VERIFICA SE O CPF JÁ ESTÁ CADASTRADO
        public boolean existeCpf(String cpf){
                return pacienteRepository.existsByCpf(somenteDigitos(cpf));
        }

        //TODOS OS PACIENTES, EM ORDEM DE NOME
        public List<Paciente> listar(){
                return pacienteRepository.findAll(Sort.by("nome"));
        }

        //BUSCA POR NOME, CPF OU E-MAIL (TEXTO VAZIO = TODOS)
        public List<Paciente> buscar(String termo){
                if (termo == null || termo.isBlank()) {
                        return listar();
                }
                String texto = termo.trim();
                String digitos = somenteDigitos(texto);
                // se o texto não tem número (ex.: "Ana"), o "" casaria com todo CPF
                String cpf = digitos.isEmpty() ? texto : digitos;
                return pacienteRepository
                        .findByNomeContainingIgnoreCaseOrCpfContainingOrEmailContainingIgnoreCase(texto, cpf, texto);
        }

        //BUSCA PELO ID (404 SE NÃO EXISTIR)
        public Paciente buscarPorId(Long id){
                return pacienteRepository.findById(id)
                                .orElseThrow(() -> new RecursoNaoEncontradoException(
                                                "Paciente " + id + " não encontrado"));
        }
```
**Cuidado com a busca por CPF:** `Containing("")` casa com **todos** os registros. Sem o ajuste acima, buscar "Ana" traria todo mundo (o CPF de "Ana" vira `""`).

### 13.4 Cadastro com CPF repetido (página `/register`)
O cadastro é uma página (formulário), não a API. Se a `RegraNegocioException` subisse até o navegador, o usuário veria um JSON. Por isso, confira antes, igual já é feito com o e-mail.

Em `controller/SecureLoginController.java`, no `handleRegister`, logo depois do `if` do e-mail:
```java
                if (pacienteService.existeCpf(cpf)) {
                        return "redirect:/register?erro=cpf";
                }
```

Em `templates/login/register.html`, a mensagem de erro hoje aparece para qualquer `erro`. Separe as duas:
```html
        <!-- E-mail já cadastrado -->
        <div class="register-message error-message"
             th:if="${param.erro != null and param.erro[0] == 'email'}">
          <strong>Usuário já cadastrado!</strong>
          <span> Este e-mail já possui uma conta. </span>
        </div>

        <!-- CPF já cadastrado -->
        <div class="register-message error-message"
             th:if="${param.erro != null and param.erro[0] == 'cpf'}">
          <strong>CPF já cadastrado!</strong>
          <span> Já existe uma conta com este CPF. </span>
        </div>
```

### 13.5 `PacienteController.java`
```java
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MedVita.exception.RecursoNaoEncontradoException;
import com.example.MedVita.models.Paciente;
import com.example.MedVita.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public List<Paciente> listar(@RequestParam(name = "busca", required = false) String busca) {
        return pacienteService.buscar(busca);
    }

    // "/perfil" é um caminho fixo, então o Spring escolhe esta rota antes da "/{id}"
    @GetMapping("/perfil")
    @PreAuthorize("hasRole('USER')")
    public Paciente perfil(Authentication authentication) {
        return pacienteService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public Paciente buscarPorId(@PathVariable Long id) {
        return pacienteService.buscarPorId(id);
    }
}
```

## Passo 14: testar as rotas GET no navegador

Como a API é testada pelas telas (o login tem reCAPTCHA, então o Postman não serve), o jeito mais simples é: **logar no site e abrir a URL da rota GET direto no navegador**. O navegador mostra o JSON.

Com `admin`/`1234`:
- `http://localhost:8080/profissionais` → `[]` (ou a lista)
- `http://localhost:8080/profissionais/especialidades` → as 6 especialidades com `valor` e `nome`
- `http://localhost:8080/profissionais?categoria=XPTO` → 400 "Dados inválidos na requisição."
- `http://localhost:8080/profissionais/999` → 404
- `http://localhost:8080/pacientes` → lista de pacientes, **sem** `passwordHash`

Com um paciente cadastrado pelo `/register`:
- `http://localhost:8080/pacientes/perfil` → os dados dele
- `http://localhost:8080/pacientes` → **403** (não pode ser 500; se der 500, falta o handler do `AccessDeniedException`)

POST e DELETE ficam para o passo 16, quando a tela `admin/medicos` estiver mandando o token CSRF.

> Os usuários de teste do `application.properties` (`paciente`, `medico`) **não existem no banco**: `/pacientes/perfil` com eles dá 404. Teste com contas reais.

---

# Fase 3: telas

## Passo 15: preparar as páginas (CSRF + `api.js`)

Nas **três** páginas da sua parte (`admin/medicos.html`, `medico/pacientes.html`, `paciente/perfil.html`), dentro do `<head>`:
```html
    <meta name="_csrf" th:content="${_csrf.token}" />
    <meta name="_csrf_header" th:content="${_csrf.headerName}" />
```
E antes do `</body>`:
```html
    <script th:src="@{/js/api.js}"></script>
    <script>
      // o script da página (passos 16 a 18)
    </script>
```

O `static/js/api.js` é do Gonzaga (fase 1 dele) e oferece `api.get(url)`, `api.post(url, corpo)`, `api.put(url, corpo)` e `api.delete(url)`. Essas funções mandam o token CSRF, devolvem o JSON e, se der erro, lançam um `Error` com a mensagem do servidor.

**Se o `api.js` ainda não estiver na `main`,** para não ficar esperando, coloque esta versão provisória no início do seu `<script>` e apague quando o dele chegar (a interface é a mesma):
```js
      const api = (() => {
        async function chamar(metodo, url, corpo) {
          const token = document.querySelector('meta[name="_csrf"]').content;
          const cabecalho = document.querySelector('meta[name="_csrf_header"]').content;
          const resposta = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json', [cabecalho]: token },
            body: corpo ? JSON.stringify(corpo) : undefined,
          });
          if (!resposta.ok) {
            let mensagem = 'Erro inesperado';
            try { mensagem = (await resposta.json()).message || mensagem; } catch (e) {}
            throw new Error(mensagem);
          }
          return resposta.status === 204 ? null : resposta.json();
        }
        return {
          get: (url) => chamar('GET', url),
          post: (url, corpo) => chamar('POST', url, corpo),
          put: (url, corpo) => chamar('PUT', url, corpo),
          delete: (url) => chamar('DELETE', url),
        };
      })();
```

**Duas regras para as três telas:**
- Monte as linhas da tabela com `document.createElement` + `textContent`, **nunca** com `innerHTML` e dados do usuário. Assim um nome como `<script>` não executa nada.
- Para esconder campos, use `elemento.style.display = 'none'`. O atributo `hidden` **não funciona** nos `.form-group`, porque o CSS deles força `display: flex`.

## Passo 16: `admin/medicos`

**Arquivo:** `templates/admin/medicos.html`

### 16.1 Formulário
Dentro do `<div class="form-grid">`:
- **Troque** o bloco do select de especialidade (o que tem "Clínica Geral") por:
  ```html
                <div class="form-group">
                  <label for="categoria">Categoria</label>
                  <select id="categoria" required>
                    <option value="" selected disabled>Selecione a categoria</option>
                    <option value="CLINICO_GERAL">Médico clínico geral</option>
                    <option value="ESPECIALISTA">Médico especialista</option>
                    <option value="CIRURGIAO">Cirurgião</option>
                  </select>
                </div>

                <div class="form-group" id="grupo-especialidade" style="display: none">
                  <label for="especialidade">Especialidade</label>
                  <select id="especialidade">
                    <option value="" selected disabled>Selecione a especialidade</option>
                  </select>
                </div>

                <div class="form-group" id="grupo-area" style="display: none">
                  <label for="areaCirurgica">Área cirúrgica</label>
                  <input type="text" id="areaCirurgica" placeholder="Ex.: Cirurgia cardíaca" />
                </div>
  ```
- **Acrescente**, depois do e-mail:
  ```html
                <div class="form-group">
                  <label for="senha">Senha inicial</label>
                  <input type="password" id="senha" placeholder="Senha do primeiro acesso" required />
                </div>
  ```
- No botão "Adicionar médico", acrescente `id="btn-adicionar"` (ele continua `type="button"`).
- Logo abaixo do `</form>`, uma linha para as mensagens:
  ```html
            <p id="mensagem" style="display: none; margin-top: 1rem"></p>
  ```

### 16.2 Tabela
- No `<thead>`, as colunas passam a ser: Nome, Registro, **Categoria**, **Área de atuação**, Telefone, E-mail, Ações.
- No `<tbody>`, coloque `id="lista-medicos"` e **apague as 3 linhas de exemplo** (Dr. Ricardo etc.).

### 16.3 Script
```js
      const NOMES_CATEGORIA = {
        CLINICO_GERAL: 'Clínico geral',
        ESPECIALISTA: 'Especialista',
        CIRURGIAO: 'Cirurgião',
      };

      const form = document.querySelector('form');
      const categoria = document.getElementById('categoria');
      const especialidade = document.getElementById('especialidade');
      const grupoEspecialidade = document.getElementById('grupo-especialidade');
      const grupoArea = document.getElementById('grupo-area');
      const tabela = document.getElementById('lista-medicos');
      const mensagem = document.getElementById('mensagem');

      function mostrarMensagem(texto, erro) {
        mensagem.textContent = texto;
        mensagem.className = erro ? 'badge badge-danger' : 'badge badge-success';
        mensagem.style.display = '';
      }

      // mostra o campo certo para cada categoria
      function atualizarCampos() {
        grupoEspecialidade.style.display = categoria.value === 'ESPECIALISTA' ? '' : 'none';
        grupoArea.style.display = categoria.value === 'CIRURGIAO' ? '' : 'none';
      }

      async function carregarEspecialidades() {
        const lista = await api.get('/profissionais/especialidades');
        lista.forEach((e) => especialidade.add(new Option(e.nome, e.valor)));
      }

      function celula(texto) {
        const td = document.createElement('td');
        td.textContent = texto ?? '';
        return td;
      }

      async function carregarMedicos() {
        const medicos = await api.get('/profissionais');
        tabela.replaceChildren();
        medicos.forEach((m) => {
          const linha = document.createElement('tr');
          linha.append(
            celula(m.nome),
            celula(m.registroProfissional),
            celula(NOMES_CATEGORIA[m.categoria]),
            celula(m.areaAtuacao),
            celula(m.telefone),
            celula(m.email),
          );

          const acoes = document.createElement('td');
          acoes.className = 'actions';
          const botao = document.createElement('button');
          botao.type = 'button';
          botao.className = 'btn btn-danger btn-sm';
          botao.textContent = 'Remover';
          botao.addEventListener('click', () => remover(m));
          acoes.append(botao);
          linha.append(acoes);

          tabela.append(linha);
        });
      }

      async function adicionar() {
        if (!form.reportValidity()) return;   // usa o "required" dos campos
        const corpo = {
          tipo: categoria.value,
          nome: document.getElementById('nome').value,
          registroProfissional: document.getElementById('registro').value,
          telefone: document.getElementById('telefone').value,
          email: document.getElementById('email').value,
          senha: document.getElementById('senha').value,
          especialidade: categoria.value === 'ESPECIALISTA' ? (especialidade.value || null) : null,
          areaCirurgica: categoria.value === 'CIRURGIAO' ? document.getElementById('areaCirurgica').value : null,
        };
        try {
          await api.post('/profissionais', corpo);
          form.reset();
          atualizarCampos();
          mostrarMensagem('Médico cadastrado com sucesso.', false);
          await carregarMedicos();
        } catch (erro) {
          mostrarMensagem(erro.message, true);
        }
      }

      async function remover(medico) {
        if (!confirm(`Remover ${medico.nome}?`)) return;
        try {
          await api.delete(`/profissionais/${medico.id}`);
          mostrarMensagem('Médico removido.', false);
          await carregarMedicos();
        } catch (erro) {
          mostrarMensagem(erro.message, true);   // ex.: "Profissional com atendimentos ... não pode ser removido"
        }
      }

      categoria.addEventListener('change', atualizarCampos);
      document.getElementById('btn-adicionar').addEventListener('click', adicionar);
      carregarEspecialidades();
      carregarMedicos();
```

**Detalhes que evitam erro:**
- `especialidade.value || null`: se ninguém escolheu, o select vale `""`, e o Jackson recusa `""` como enum (daria 400 "Dados inválidos"). Mandando `null`, quem responde é a sua regra ("Informe a especialidade").
- O `value` das opções de especialidade é o `valor` (`CARDIOLOGIA`), e o texto é o `nome` (`Cardiologia`). Se mandar o texto, dá 400.

## Passo 17: `medico/pacientes`

**Arquivo:** `templates/medico/pacientes.html`

### 17.1 HTML
- No `<tbody>` da tabela, coloque `id="lista-pacientes"` e apague as 3 linhas de exemplo.
- Depois do card da tabela (ainda dentro de `<div class="content">`), acrescente um card para a ficha, escondido:
  ```html
          <div class="card" id="ficha" style="display: none">
            <div class="card-header">
              <div>
                <h2 id="ficha-nome">Ficha do paciente</h2>
                <p>Dados cadastrais do paciente.</p>
              </div>
            </div>
            <div class="form-grid">
              <div class="form-group"><label>CPF</label><input id="ficha-cpf" disabled /></div>
              <div class="form-group"><label>Nascimento</label><input id="ficha-nascimento" disabled /></div>
              <div class="form-group"><label>Telefone</label><input id="ficha-telefone" disabled /></div>
              <div class="form-group"><label>E-mail</label><input id="ficha-email" disabled /></div>
              <div class="form-group full-width"><label>Endereço</label><input id="ficha-endereco" disabled /></div>
            </div>
          </div>
  ```

### 17.2 Script
```js
      const busca = document.getElementById('busca');
      const tabela = document.getElementById('lista-pacientes');
      let espera;

      function formatarCpf(cpf) {
        return cpf ? cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : '';
      }

      function formatarData(data) {          // "1998-05-10" -> "10/05/1998"
        if (!data) return '';
        const [ano, mes, dia] = data.split('-');
        return `${dia}/${mes}/${ano}`;
      }

      function celula(texto) {
        const td = document.createElement('td');
        td.textContent = texto ?? '';
        return td;
      }

      async function carregar() {
        const texto = busca.value.trim();
        const url = texto ? `/pacientes?busca=${encodeURIComponent(texto)}` : '/pacientes';
        const pacientes = await api.get(url);
        tabela.replaceChildren();
        pacientes.forEach((p) => {
          const linha = document.createElement('tr');
          linha.append(celula(p.nome), celula(formatarCpf(p.cpf)), celula(formatarData(p.dataNascimento)),
                       celula(p.telefone), celula(p.email));
          const acoes = document.createElement('td');
          acoes.className = 'actions';
          const botao = document.createElement('button');
          botao.type = 'button';
          botao.className = 'btn btn-outline btn-sm';
          botao.textContent = 'Ver ficha';
          botao.addEventListener('click', () => verFicha(p.id));
          acoes.append(botao);
          linha.append(acoes);
          tabela.append(linha);
        });
      }

      async function verFicha(id) {
        const p = await api.get(`/pacientes/${id}`);
        document.getElementById('ficha-nome').textContent = p.nome;
        document.getElementById('ficha-cpf').value = formatarCpf(p.cpf);
        document.getElementById('ficha-nascimento').value = formatarData(p.dataNascimento);
        document.getElementById('ficha-telefone').value = p.telefone;
        document.getElementById('ficha-email').value = p.email;
        document.getElementById('ficha-endereco').value = p.endereco;
        const ficha = document.getElementById('ficha');
        ficha.style.display = '';
        ficha.scrollIntoView({ behavior: 'smooth' });
      }

      // busca enquanto digita, esperando 300 ms depois da última tecla
      busca.addEventListener('input', () => {
        clearTimeout(espera);
        espera = setTimeout(carregar, 300);
      });

      carregar();
```
Quando o histórico do Henrique estiver pronto, dá para pôr na ficha um link para `GET /historico/{id}`. Isso é opcional e fica para o fim.

## Passo 18: `paciente/perfil`

**Arquivo:** `templates/paciente/perfil.html`

Os inputs já têm ids (`nome`, `cpf`, `nascimento`, `telefone`, `endereco`, `email`). Tire os `value="..."` de exemplo e preencha pelo script:
```js
      function formatarCpf(cpf) {
        return cpf ? cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : '';
      }

      api.get('/pacientes/perfil')
        .then((p) => {
          document.getElementById('nome').value = p.nome;
          document.getElementById('cpf').value = formatarCpf(p.cpf);
          document.getElementById('nascimento').value = p.dataNascimento;   // <input type="date"> já aceita "1998-05-10"
          document.getElementById('telefone').value = p.telefone;
          document.getElementById('endereco').value = p.endereco;
          document.getElementById('email').value = p.email;
        })
        .catch((erro) => alert(erro.message));
```

## Passo 19 (opcional): números do painel `admin/admin`

Se sobrar tempo: no `admin/admin.html`, buscar `GET /profissionais` e contar por `categoria` para preencher os cards do painel (total de médicos, quantos de cada categoria). Use o mesmo padrão dos passos anteriores.

---

# Fase 4: fechamento

## Passo 20: roteiro de teste da sua parte

Com o banco no ar e o site rodando:

**Profissionais (logado como `admin`)**
- [ ] Cadastrar um clínico geral, um especialista em Cardiologia e um cirurgião (área "Cirurgia geral").
- [ ] A tabela mostra Categoria e Área de atuação certas ("Clínica geral", "Cardiologia", "Cirurgia geral").
- [ ] Cadastrar com e-mail repetido → "Já existe um usuário com este e-mail".
- [ ] Cadastrar com o e-mail de um **paciente** → mesma mensagem.
- [ ] Cadastrar com registro repetido → "Já existe um profissional com este registro".
- [ ] Especialista sem especialidade → "Informe a especialidade". Cirurgião sem área → "Preencha o campo área cirúrgica".
- [ ] `/profissionais?especialidade=CARDIOLOGIA` mostra só o cardiologista; `?categoria=CIRURGIAO` só o cirurgião.
- [ ] Remover um médico recém-cadastrado → some da lista.
- [ ] (depois que o João tiver consultas) Remover um médico que já atendeu → mensagem de erro, e ele continua na lista.

**Login**
- [ ] Sair, entrar com o e-mail e a senha do clínico → cai em `/medico`.
- [ ] Entrar com o e-mail digitado em maiúsculas → também funciona.

**Pacientes**
- [ ] Cadastrar paciente pelo `/register` com CPF já usado → aparece "CPF já cadastrado!".
- [ ] Como médico: `medico/pacientes` lista todos; buscar por parte do nome, por CPF com pontos (`123.456`) e por e-mail; "Ver ficha" abre os dados.
- [ ] Como paciente: `paciente/perfil` mostra os próprios dados.
- [ ] Como paciente, abrir `http://localhost:8080/pacientes` → 403.
- [ ] Nenhum JSON mostra `passwordHash`.

## Passo 21: PR final

```bash
git checkout Leonardo
git pull origin main          # traz o que os outros já mandaram e resolve conflito, se houver
mvn -q compile                # (dentro de MedVita/)
git add -A
git commit -m "Profissionais, pacientes, login do médico, segurança e telas"
git push origin Leonardo
```
Abra o PR para a `main`. Depois do merge, confira se o diagrama `DOCUMENTOS/DiagramaUML/01-diagrama-classes-dominio.png` bate com o que você implementou (nomes de métodos e atributos). Se mudou algo, avise para atualizar o diagrama.

---

## Para a apresentação: o que mostrar da sua parte

1. **Herança:** `ProfissionalSaude` abstrata, com as 3 subclasses numa tabela só (`SINGLE_TABLE`) e a coluna `tipo` como discriminador.
2. **Polimorfismo:** `podeRealizar(tipo, especialidade)` tem 3 implementações diferentes. Mostre o `verificarSePodeRealizar()`, que chama esse método sem saber qual é o médico. Mesma coisa com `podeEncaminhar()` e `getAreaAtuacao()`.
3. **Polimorfismo no service:** o filtro `p.getCategoria() == categoria` funciona sem `instanceof`.
4. **O único `switch` por categoria** é no `cadastrar`, porque para criar o objeto é preciso saber a classe. Em todo o resto, quem decide é o objeto.
5. **Regras de negócio:** e-mail único entre médicos e pacientes, registro único, especialidade obrigatória para especialista, área obrigatória para cirurgião, e o médico com histórico que não pode ser removido (garantido pela chave estrangeira, RN6).

---

## Erros comuns e como resolver

| Sintoma | Causa provável | Solução |
|---|---|---|
| POST/DELETE da tela dá **403** | faltou o token CSRF | as duas `<meta>` do passo 15 no `<head>` e o `api.js` mandando o cabeçalho |
| Acesso negado vira **500** em vez de 403 | o handler genérico pegou o `AccessDeniedException` | handler do passo 7 |
| `@PreAuthorize` não faz nada | faltou `@EnableMethodSecurity` | passo 8 |
| `column "tipo" does not exist` / `null value in column "especialidade"` | o banco ainda está com o schema antigo | esperar ou pedir o `schema.sql` novo do Gonzaga |
| Médico não consegue logar | e-mail não normalizado, ou senha salva sem BCrypt | `normalizar()` no cadastro e no `buscarPorEmail`; `passwordEncoder.encode` |
| 400 "Dados inválidos" ao cadastrar | o select mandou o texto ("Cardiologia") ou `""` em vez do valor do enum | `new Option(e.nome, e.valor)` e `especialidade.value \|\| null` |
| `passwordHash` aparece no JSON | faltou `@JsonIgnore` | passos 3 e 13.1 |
| Campo escondido continua aparecendo | `hidden` não funciona no `.form-group` | `style.display = 'none'` |
| Busca de paciente traz todo mundo | `CpfContaining("")` | ajuste do `cpf` no `buscar` (passo 13.3) |
| `/pacientes/perfil` dá 404 com o usuário `paciente` | os usuários de teste não existem no banco | testar com conta criada pelo `/register` |
| `./mvnw` não funciona | falta a pasta `.mvn` no projeto | usar `mvn` |
