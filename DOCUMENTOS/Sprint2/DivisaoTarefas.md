# Sprint 2: divisão de tarefas

Sistema Hospitalar MedVita · Programação Modular · PUC Minas

Este documento resume o que muda na Sprint 2, o que o grupo decidiu e o que cada um faz. O detalhe de **cada classe** (atributos, métodos, regras, mensagens de erro) está no comentário `STATUS: A IMPLEMENTAR` dentro do próprio arquivo `.java`, com o nome do responsável na linha `RESPONSÁVEL:`. Antes de começar, leia este documento e os arquivos da sua parte.

O diagrama de classes atualizado (entrega 1) já está em [`DOCUMENTOS/DiagramaUML`](../DiagramaUML/).

---

## 1. O que a Sprint 2 pede

1. **Três categorias de profissional** (clínico geral, especialista e cirurgião), cada uma com atividades diferentes. Isso dá **herança e polimorfismo** em `ProfissionalSaude`.
2. **Quatro modalidades de atendimento** (consulta de rotina, consulta especializada, emergência e procedimento cirúrgico). Os dados em comum ficam numa classe mãe, e cada modalidade valida as próprias regras.
3. **Situações da consulta** como enum (agendada, confirmada, em atendimento, concluída, cancelada, não compareceu), com regras de quais mudanças são permitidas.
4. **Endpoints** `/pacientes`, `/profissionais`, `/consultas`, `/internacoes`, `/quartos`.
5. **Regras de tipos de quarto.** O PDF cita isso, mas não diz quais são os tipos; o grupo definiu (ver abaixo).

---

## 2. Decisões do grupo

| Assunto | Decisão |
|---|---|
| Hierarquia dos profissionais | `ProfissionalSaude` abstrata com 3 subclasses irmãs: `MedicoClinicoGeral`, `MedicoEspecialista`, `Cirurgiao` |
| Modalidades de atendimento | `Atendimento` abstrata → `Consulta` abstrata (→ `ConsultaRotina`, `ConsultaEspecializada`), `AtendimentoEmergencia`, `ProcedimentoCirurgico` |
| Endpoint das modalidades | **Tudo em `/consultas`**: o JSON diz o `"tipo"` |
| Banco | **Uma tabela por hierarquia** (`SINGLE_TABLE`): `profissionais_saude` e `atendimentos`, cada uma com a coluna `tipo` |
| Consulta cancelada | **Estado final.** Reagendar cria uma consulta **nova**; a antiga continua cancelada no histórico |
| Quem atende emergência | Clínico geral e especialista (o cirurgião não). O médico só é definido quando o atendimento começa |
| Tipos de quarto | Enum `TipoQuarto`: `ENFERMARIA` (2 a 6 leitos), `APARTAMENTO` (1 a 2), `UTI` (1 a 10) |
| Situação da internação | Enum `StatusInternacao`: `ATIVA` → `ALTA` |
| Especialidade | Enum `Especialidade` (não é mais texto livre). "Clínica Geral" deixou de ser especialidade e virou categoria |
| Endereços da API | Exatamente como no PDF, **sem `/api`**: `/pacientes`, `/consultas`... (e `/historico`, que fica) |
| Como testar a API | **Só pelas telas** (login normal com sessão; o CSRF continua ligado) |
| Testes automatizados | **Ficam para depois.** Os arquivos `*ServiceTest` estão marcados como adiados |
| Divisão | **Por domínio**: cada um faz model, repository, service, controller e as telas do seu assunto |

---

## 3. O modelo novo

```
ProfissionalSaude (abstrata)            Atendimento (abstrata)
 ├── MedicoClinicoGeral                  ├── Consulta (abstrata)    StatusConsulta
 ├── MedicoEspecialista  + especialidade │    ├── ConsultaRotina        + encaminhamento
 └── Cirurgiao           + areaCirurgica │    └── ConsultaEspecializada + especialidade
                                         ├── AtendimentoEmergencia  + prioridade, StatusEmergencia
                                         └── ProcedimentoCirurgico  + descrição, tipo, obs. pré/pós,
                                                                      início/fim efetivos, StatusProcedimento

Paciente   Quarto (+ TipoQuarto)   Internacao (+ StatusInternacao)   HistoricoMedico (não é tabela)
```

**Enums** (pacote `models/enums`): `Especialidade`, `CategoriaProfissional`, `TipoAtendimento`, `StatusConsulta`, `StatusEmergencia`, `StatusProcedimento`, `Prioridade`, `TipoCirurgia`, `TipoQuarto`, `StatusInternacao`.

**DTOs** (pacote novo `dto`): `ProfissionalRequest`, `AtendimentoRequest`, `InternacaoRequest`. Eles existem porque o Jackson não consegue criar classe abstrata a partir do JSON, e porque os pedidos chegam com ids (`pacienteId`), não com objetos.

**Repositório renomeado:** `ConsultaRepository` → `AtendimentoRepository` (um só para as 4 modalidades).

### Onde está o polimorfismo (para a apresentação)

1. **`ProfissionalSaude.podeRealizar(atendimento)`** tem uma implementação em cada subclasse. O `verificarSePodeRealizar()` da classe mãe chama esse método sem nenhum `if` de tipo de médico.
2. **`Atendimento.validar()`** é um método template: as regras comuns ficam na mãe, e cada modalidade implementa `validarRegrasDaModalidade()` (é o "cada modalidade deverá implementar sua própria lógica de validação" do PDF).
3. **`iniciar()`, `concluir()`, `cancelar()`**: o service chama sempre o mesmo método, e o comportamento muda com a modalidade. A emergência não pode ser cancelada; a cirurgia não pode ser concluída sem ter sido iniciada.
4. **`ocupaAgenda()` e `isConcluido()`**: a RN3 (horário do médico) e o histórico funcionam para as 4 modalidades sem `if` por tipo.
5. **`Consulta.reagendar()` + `copiar()`**: cada consulta cria uma cópia do próprio tipo.
6. **Enums com comportamento**: `StatusConsulta.podeMudarPara()` e `TipoQuarto.aceitaCapacidade()`.

**Regra para todos:** fora da criação do objeto (o `switch` do `ConsultaService.criar` e do `ProfissionalSaudeService.cadastrar`), não usem `if`/`switch` por tipo. Se precisar perguntar "que tipo é esse?", é sinal de que falta um método polimórfico.

---

## 4. Endpoints

Todos devolvem JSON. Erro de regra de negócio devolve **400** `{"message": "..."}`, id inexistente devolve **404** e conflito no banco devolve **409**.

| Método | Rota | Quem pode | Responsável |
|---|---|---|---|
| GET | `/pacientes?busca=` | MEDICO, ADMIN | João Pedro |
| GET | `/pacientes/perfil` | paciente logado | João Pedro |
| GET | `/pacientes/{id}` | MEDICO, ADMIN | João Pedro |
| GET | `/profissionais?categoria=&especialidade=&data=&horario=` | logado | João Pedro |
| GET | `/profissionais/especialidades` | logado | João Pedro |
| GET | `/profissionais/{id}` | logado | João Pedro |
| GET | `/profissionais/{id}/procedimentos` | logado | João Pedro (rota) / Gonzaga (consulta) |
| POST | `/profissionais` | ADMIN | João Pedro |
| DELETE | `/profissionais/{id}` | ADMIN | João Pedro |
| POST | `/consultas` | paciente (rotina/especializada para si); MEDICO, ADMIN (emergência/cirurgia) | Leonardo Martins |
| GET | `/consultas?tipo=` | MEDICO, ADMIN | Leonardo Martins |
| GET | `/consultas/{id}` | logado | Leonardo Martins |
| GET | `/consultas/minhas` | paciente logado | Leonardo Martins |
| GET | `/consultas/agenda?data=` | médico logado | Leonardo Martins |
| GET | `/consultas/emergencias?status=` | MEDICO, ADMIN | Leonardo Gonzaga |
| PUT | `/consultas/{id}/confirmar` | paciente dono, ADMIN | Leonardo Martins |
| PUT | `/consultas/{id}/iniciar?profissionalId=` | MEDICO | Leonardo Martins (+ Gonzaga na emergência) |
| PUT | `/consultas/{id}/concluir` | MEDICO | Leonardo Martins |
| PUT | `/consultas/{id}/cancelar` | paciente dono, MEDICO, ADMIN | Leonardo Martins |
| PUT | `/consultas/{id}/nao-compareceu` | MEDICO, ADMIN | Leonardo Martins |
| PUT | `/consultas/{id}/encaminhar?especialidade=` | MEDICO (clínico da consulta) | Leonardo Martins |
| POST | `/consultas/{id}/reagendar` | paciente dono, ADMIN | Leonardo Martins |
| POST | `/internacoes` | MEDICO | Henrique |
| GET | `/internacoes?status=ATIVA` | MEDICO, ADMIN | Henrique |
| GET | `/internacoes/minhas` | paciente logado | Henrique |
| GET | `/internacoes/{id}` | logado | Henrique |
| PUT | `/internacoes/{id}/alta` | MEDICO | Henrique |
| GET | `/quartos?tipo=&disponivel=` | MEDICO, ADMIN | Henrique |
| GET | `/quartos/tipos` | MEDICO, ADMIN | Henrique |
| GET | `/quartos/{id}` | MEDICO, ADMIN | Henrique |
| POST | `/quartos` | MEDICO, ADMIN | Henrique |
| GET | `/historico` | paciente logado | Henrique |
| GET | `/historico/{pacienteId}` | MEDICO, ADMIN | Henrique |

Exemplos de JSON de cada tipo de atendimento estão em `dto/AtendimentoRequest.java`.

---

## 5. Banco de dados (o que muda no `schema.sql`)

Responsável: **Leonardo Gonzaga**. Como o banco ainda não tem dados de verdade, dá para apagar e criar as tabelas de novo. Continua `spring.jpa.hibernate.ddl-auto=none`, então **o nome de cada coluna tem que bater exatamente** com o `@Column` das classes.

### `profissionais_saude`
| Coluna | Mudança | Regra |
|---|---|---|
| `tipo` | **nova** | `not null`, `CLINICO_GERAL` \| `ESPECIALISTA` \| `CIRURGIAO` |
| `especialidade` | passa a aceitar `null` | preenchida só quando `tipo = 'ESPECIALISTA'`; valores do enum `Especialidade` |
| `area_cirurgica` | **nova** | preenchida só quando `tipo = 'CIRURGIAO'` |

As outras colunas (`nome`, `registro_profissional`, `telefone`, `email`, `password_hash`) não mudam. Opcional: um `check` garantindo que especialista tem especialidade e cirurgião tem área.

### `atendimentos` (nova, substitui `consultas`; apague a antiga com `drop table`)
| Coluna | Tipo | Usada por | Regra |
|---|---|---|---|
| `id` | bigint identity | todas | PK |
| `tipo` | text | todas | `not null`, `ROTINA` \| `ESPECIALIZADA` \| `EMERGENCIA` \| `CIRURGIA` |
| `paciente_id` | bigint | todas | `not null`, FK `pacientes` |
| `profissional_id` | bigint | todas | FK `profissionais_saude`, **aceita null** (emergência aguardando) |
| `data`, `horario` | date, time | todas | `not null` |
| `motivo`, `observacoes` | text | todas | |
| `status` | text | todas | `not null`; aceita os valores dos 3 enums de situação: `AGENDADA, CONFIRMADA, EM_ATENDIMENTO, CONCLUIDA, CANCELADA, NAO_COMPARECEU, AGUARDANDO_AVALIACAO, CONCLUIDO, AGENDADO, EM_ANDAMENTO, CANCELADO` |
| `especialidade` | text | ESPECIALIZADA | |
| `encaminhamento` | text | ROTINA | |
| `prioridade` | text | EMERGENCIA | `BAIXA` \| `MODERADA` \| `ALTA` \| `CRITICA` |
| `descricao_procedimento` | text | CIRURGIA | |
| `tipo_cirurgia` | text | CIRURGIA | `ELETIVA` \| `URGENCIA` \| `EMERGENCIA` |
| `obs_pre_operatorias`, `obs_pos_operatorias` | text | CIRURGIA | |
| `inicio_efetivo`, `fim_efetivo` | timestamp | CIRURGIA | `LocalDateTime` no Java |
| `created_at` | timestamptz | todas | `default now()` |

**Índice da RN3** (substitui o antigo): único em `(profissional_id, data, horario)` **onde** `tipo <> 'EMERGENCIA'` e `status` não é `CANCELADA` nem `CANCELADO`. Lembrem de ligar o RLS na tabela nova, igual às outras.

### `quartos`
| Coluna | Mudança | Regra |
|---|---|---|
| `tipo` | **nova** | `not null`, `ENFERMARIA` \| `APARTAMENTO` \| `UTI` |

### `internacoes`
| Coluna | Mudança | Regra |
|---|---|---|
| `status` | **nova** | `not null default 'ATIVA'`, `ATIVA` \| `ALTA` |

O índice "um paciente internado por vez" passa a ser único em `paciente_id` **onde** `status = 'ATIVA'`.

> **Obs. técnica:** as três subclasses de `Atendimento` que têm situação mapeiam a mesma coluna `status`, cada uma com o próprio enum. O Hibernate aceita isso em `SINGLE_TABLE`. Se reclamar de coluna repetida, a saída é usar colunas separadas (`status_consulta`, `status_emergencia`, `status_procedimento`) e avisar o Gonzaga para mudar o schema.

---

## 6. Quem faz o quê

Troquem as partes entre vocês se quiserem; basta atualizar a linha `RESPONSÁVEL:` dos arquivos.

### Leonardo Martins: Consultas

**Back**
- [ ] **Dia 1: esqueleto comum.** Criar as 10 classes das duas hierarquias só com a declaração (`public abstract class Atendimento`, `public class Cirurgiao extends ProfissionalSaude {}`...) e mandar para a `main`. Isso tira a dependência circular entre `ProfissionalSaude` e `Atendimento` e deixa todo mundo compilar desde o começo.
- [ ] `models/Atendimento.java` (abstrata, `validar()` template, métodos abstratos)
- [ ] `models/Consulta.java` (abstrata, `StatusConsulta`, `mudarStatus`, `reagendar`)
- [ ] `models/ConsultaRotina.java`, `models/ConsultaEspecializada.java`
- [ ] `models/enums/StatusConsulta.java`, `models/enums/TipoAtendimento.java`
- [ ] `dto/AtendimentoRequest.java`
- [ ] `repository/AtendimentoRepository.java` (menos as 2 queries do Gonzaga)
- [ ] `service/ConsultaService.java` (menos as partes `[GONZAGA]`)
- [ ] `controller/ConsultaController.java` (menos a rota `/emergencias`)

**Telas**
- [ ] `paciente/agendar-consulta`: o select de especialidade tem "Clínica geral" (gera `ROTINA`) + `GET /profissionais/especialidades` (gera `ESPECIALIZADA`). Depois de escolher especialidade, data e horário, carrega os profissionais livres em `GET /profissionais?...` e faz `POST /consultas`.
- [ ] `paciente/consultas`: `GET /consultas/minhas`; botões Confirmar, Cancelar e Reagendar conforme a situação; mostrar o encaminhamento, se tiver.
- [ ] `medico/consultas`: `GET /consultas/agenda?data=`; botões Iniciar, Concluir (pede as observações), Não compareceu e Encaminhar (só na rotina).

**No fim:** atualizar o `README.md` (estrutura, Sprint 2, tirar o "MASI MUDANCAS EM BREVE").

### João Pedro: Pessoas e acesso (profissionais, pacientes, login)

**Back**
- [ ] `models/ProfissionalSaude.java` (abstrata, `podeRealizar`, `verificarSePodeRealizar`)
- [ ] `models/MedicoClinicoGeral.java` (com `encaminhar`), `models/MedicoEspecialista.java`, `models/Cirurgiao.java`
- [ ] `models/enums/Especialidade.java`, `models/enums/CategoriaProfissional.java`
- [ ] `dto/ProfissionalRequest.java`
- [ ] `repository/ProfissionalSaudeRepository.java`
- [ ] `service/ProfissionalSaudeService.java`
- [ ] `controller/ProfissionalSaudeController.java`
- [ ] `service/DatabaseUserDetailsService.java`: login do médico pelo banco
- [ ] `config/SecurityConfig.java`: `@EnableMethodSecurity`, liberar `/js/**` (ver o bloco "A CRIAR")
- [ ] Pendências do paciente: `@JsonIgnore` no `Paciente.passwordHash`; `PacienteRepository` (`existsByCpf`, busca); `PacienteService` (`buscarPorId` **primeiro**, depois `listar`, `buscar` e CPF repetido); `controller/PacienteController.java`

**Telas**
- [ ] `admin/medicos`: select **Categoria** (clínico, especialista, cirurgião); campo Especialidade só para especialista; campo Área cirúrgica só para cirurgião; campo **Senha**; tabela com Categoria e Área de atuação; botão remover.
- [ ] `medico/pacientes`: `GET /pacientes?busca=`
- [ ] `paciente/perfil`: `GET /pacientes/perfil`
- [ ] (opcional) números do painel `admin/admin`

### Leonardo Gonzaga: Emergência, cirurgia e base do projeto

**Base (primeiros dias, porque todo mundo depende disso)**
- [ ] `exception/RecursoNaoEncontradoException.java`, `exception/RegraNegocioException.java`
- [ ] `exception/GlobalExceptionHandler.java`: handlers de 404, 400, 409 e JSON inválido
- [ ] `supabase/schema.sql` novo (seção 5), **rodado no Supabase**, e o banco funcionando para o grupo. Mandem o `.env` por mensagem privada, **nunca no git**.
- [ ] `static/js/api.js` (seção 8)

**Back**
- [ ] `models/AtendimentoEmergencia.java`, `models/ProcedimentoCirurgico.java`
- [ ] `models/enums/StatusEmergencia.java`, `StatusProcedimento.java`, `Prioridade.java`, `TipoCirurgia.java`
- [ ] Partes `[GONZAGA]` do `ConsultaService` (criar EMERGENCIA/CIRURGIA, atribuir médico ao iniciar, `filaEmergencia`, `listarProcedimentosDoCirurgiao`)
- [ ] As 2 queries do `AtendimentoRepository` e a rota `GET /consultas/emergencias`

**Telas (novas, obrigatórias: como a API só é testada pelas telas, sem elas não dá para demonstrar emergência e cirurgia)**
- [ ] `medico/emergencias`: registrar emergência (paciente, queixa, prioridade); fila por prioridade; assumir e iniciar; concluir.
- [ ] `medico/cirurgias`: agendar procedimento (paciente, cirurgião, data/hora prevista, descrição, tipo, obs. pré); iniciar; concluir (obs. pós); cancelar.
- [ ] Rotas `/medico/emergencias` e `/medico/cirurgias` no `SecureLoginController` e links no menu lateral das páginas do médico (façam isso **por último**, depois que os outros terminarem as telas do médico, para não dar conflito).

### Henrique: Quartos, internações e histórico

**Back**
- [ ] `models/Quarto.java` (+ `tipo`, `ocupar`, `liberar`, `validar`), `models/enums/TipoQuarto.java`
- [ ] `repository/QuartoRepository.java` (inclui `buscarParaAtualizar` com trava)
- [ ] `service/QuartoService.java`, `controller/QuartoController.java`
- [ ] `models/Internacao.java` (+ `status`, `darAlta`), `models/enums/StatusInternacao.java`
- [ ] `dto/InternacaoRequest.java`, `repository/InternacaoRepository.java`
- [ ] `service/InternacaoService.java`, `controller/InternacaoController.java`
- [ ] `models/HistoricoMedico.java` (agora com `List<Atendimento>`)
- [ ] `service/HistoricoMedicoService.java`, `controller/HistoricoMedicoController.java`

**Telas**
- [ ] `medico/quartos`: lista com Tipo e Situação, filtro por tipo, cadastrar quarto (select de tipo vindo de `GET /quartos/tipos`)
- [ ] `medico/internacoes`: internados agora; registrar internação (selects de paciente, médico e quartos disponíveis); dar alta
- [ ] `paciente/internacoes`: `GET /internacoes/minhas`
- [ ] `paciente/historico`: `GET /historico`, mostrando o tipo de cada atendimento

---

## 7. Ordem de trabalho

| Fase | O quê | Pronto quando |
|---|---|---|
| **1. Base** (primeiros 2 a 3 dias) | Esqueleto comum (Leonardo M.); exceções, schema e banco no ar (Gonzaga); models de cada um; `PacienteService.buscarPorId` (João) | `main` compila com todas as classes e o Supabase responde |
| **2. Regras e API** | Repositories, services e controllers | as rotas da seção 4 respondem |
| **3. Telas** | `api.js` + cada um liga as suas telas | dá para fazer o roteiro abaixo só clicando |
| **4. Fechamento** | Roteiro de teste completo, conferir o diagrama com o código, README, ensaio da apresentação | |

**Quem depende de quem:** todos dependem das exceções (Gonzaga) e do esqueleto (Leonardo M.). `ConsultaService` e `InternacaoService` dependem de `PacienteService.buscarPorId` e `ProfissionalSaudeService.buscarPorId` (João). A parte do Gonzaga no `ConsultaService` depende do `Atendimento` pronto (Leonardo M.). Por isso, na fase 1, mandem os models para a `main` cedo, mesmo sem tudo pronto: as assinaturas dos métodos já estão definidas nos comentários.

### Roteiro de teste (serve também para a apresentação)
1. Admin cadastra um clínico, um cardiologista e um cirurgião.
2. Paciente agenda uma consulta de rotina com o clínico. Outro paciente tenta o mesmo médico no mesmo horário → erro (RN3).
3. Consulta: confirmar → iniciar → concluir com observações. Tentar cancelar a concluída → erro.
4. Cancelar uma consulta e reagendar → aparece uma consulta nova, e a antiga continua cancelada.
5. Clínico encaminha para cardiologia → paciente agenda a especializada com o cardiologista.
6. Emergências CRÍTICA e BAIXA → a fila mostra a crítica primeiro. Tentar assumir com o cirurgião → erro. Assumir com o clínico → iniciar → concluir.
7. Cirurgia: agendar; tentar concluir sem iniciar → erro; iniciar → concluir.
8. Cadastrar apartamento com 3 leitos → erro. Internar até lotar um quarto → erro no próximo. Dar alta libera o leito.
9. Histórico do paciente mostra só os atendimentos concluídos, de todas as modalidades, e as internações.

> Os usuários de teste do `application.properties` (`paciente` e `medico`) **não existem no banco**, então rotas como `/consultas/minhas` não funcionam com eles. Com o banco no ar, cadastrem pacientes pelo `/register` e médicos pelo admin.

---

## 8. Telas: como chamar a API

As telas hoje são HTML estático, sem JavaScript. Para ligar cada uma na API:

1. **Token CSRF.** O CSRF continua ligado, então cada página coloca no `<head>`:
   ```html
   <meta name="_csrf" th:content="${_csrf.token}"/>
   <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
   ```
2. **`static/js/api.js`** (Gonzaga faz no começo). Funções `api.get(url)`, `api.post(url, corpo)`, `api.put(url, corpo)` e `api.delete(url)`, que:
   - mandam `Content-Type: application/json` e o cabeçalho do CSRF (lido das duas `<meta>`);
   - se a resposta não for ok, leem o `{"message"}` do `GlobalExceptionHandler` e lançam um `Error` com essa mensagem;
   - devolvem o JSON (ou `null` no 204).
3. Cada página inclui `<script th:src="@{/js/api.js}"></script>` e um `<script>` próprio no fim, que preenche as tabelas e mostra as mensagens de erro numa caixa da página. Os dados escritos à mão no HTML (ex.: "Dr. Ricardo Almeida") saem.

---

## 9. Git: como não dar conflito

- Cada um na sua branch (`Leonardo` e `Joao-Pedro` já existem; criem `Henrique` e `Leonardo-Gonzaga`), sempre a partir da `main` atualizada.
- PRs **pequenos e frequentes** para a `main`, e puxem a `main` todo dia.
- **Arquivos com mais de um dono:**
  - `ConsultaService`, `ConsultaController`, `AtendimentoRepository`: Leonardo M. é o dono, e o Gonzaga só acrescenta os métodos marcados `[GONZAGA]`, no fim do arquivo.
  - `SecurityConfig`: só o João mexe; quem precisar de algo pede a ele.
  - `schema.sql`: só o Gonzaga mexe; quem precisar de coluna pede a ele.
  - Menu lateral das páginas do médico: o Gonzaga mexe só no fim (fase 4).
- `.env` nunca vai para o git.

---

## 10. Checklist das entregas do PDF

| Entrega | Onde está | Status |
|---|---|---|
| 1. Diagrama de classes atualizado | `DOCUMENTOS/DiagramaUML/` (domínio e camadas) | feito; conferir com o código na fase 4 |
| 2. Classes de domínio com atributos, métodos e regras | `models/` e `models/enums/` | a fazer (seção 6) |
| 3. Endpoints `/pacientes`, `/profissionais`, `/consultas`, `/internacoes`, `/quartos` | `controller/` | a fazer (seção 4) |
| 4. Regras de categorias de profissionais | `podeRealizar()` nas 3 subclasses, `MedicoClinicoGeral.encaminhar()`, filtro por especialidade | a fazer |
| 4. Regras das modalidades de atendimento | `validarRegrasDaModalidade()` nas 4 modalidades | a fazer |
| 4. Situações das consultas | `StatusConsulta.podeMudarPara()` + `Consulta.mudarStatus()` | a fazer |
| 4. Procedimentos cirúrgicos | `ProcedimentoCirurgico` + `StatusProcedimento` (não conclui sem iniciar) | a fazer |
| 4. Tipos de quartos | `TipoQuarto.aceitaCapacidade()` + `Quarto.validar()` | a fazer |
