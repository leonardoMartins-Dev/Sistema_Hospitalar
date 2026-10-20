# Sprint 2: divisão de tarefas

Sistema Hospitalar MedVita · Programação Modular · PUC Minas

Este documento resume o que muda na Sprint 2, o que o grupo decidiu e o que cada um faz. O detalhe de **cada classe** (atributos, métodos, regras, mensagens de erro) está no comentário `STATUS: A IMPLEMENTAR` dentro do próprio arquivo `.java`, com o nome do responsável na linha `RESPONSÁVEL:`. Antes de começar, leia este documento e os arquivos da sua parte.

O diagrama de classes atualizado (entrega 1) já está em [`DOCUMENTOS/DiagramaUML`](../DiagramaUML/).

A divisão foi pensada para **um depender o mínimo possível do outro**. A seção 5 mostra exatamente o que ainda depende de quem.

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
| Endereço das modalidades | **Tudo em `/consultas`.** As consultas entram por `POST /consultas`; emergência e cirurgia têm rotas próprias dentro dele (`/consultas/emergencias`, `/consultas/cirurgias`), num controller separado, para ninguém mexer no arquivo do outro |
| Banco | **Uma tabela por hierarquia** (`SINGLE_TABLE`): `profissionais_saude` e `atendimentos`, cada uma com a coluna `tipo` |
| Consulta cancelada | **Estado final.** Reagendar cria uma consulta **nova**; a antiga continua cancelada no histórico |
| Quem atende emergência | Clínico geral e especialista (o cirurgião não). O médico só é definido quando assume o atendimento |
| Tipos de quarto | Enum `TipoQuarto`: `ENFERMARIA` (2 a 6 leitos), `APARTAMENTO` (1 a 2), `UTI` (1 a 10) |
| Situação da internação | Enum `StatusInternacao`: `ATIVA` → `ALTA` |
| Especialidade | Enum `Especialidade` (não é mais texto livre). "Clínica Geral" deixou de ser especialidade e virou categoria |
| Remover profissional | Quem impede a remoção de quem tem histórico é a **chave estrangeira do banco** (vira erro 400 com mensagem) |
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

**DTOs** (pacote novo `dto`): `ProfissionalRequest`, `ConsultaRequest`, `EmergenciaRequest`, `CirurgiaRequest`, `InternacaoRequest`. Eles existem porque o Jackson não consegue criar classe abstrata a partir do JSON, e porque os pedidos chegam com ids (`pacienteId`), não com objetos.

**Repositórios:** `ConsultaRepository` virou `AtendimentoRepository`. A emergência e a cirurgia ganharam repositórios próprios (`AtendimentoEmergenciaRepository`, `ProcedimentoCirurgicoRepository`). O Spring Data aceita repositório de subclasse, e eles leem a mesma tabela.

### Onde está o polimorfismo (para a apresentação)

1. **`ProfissionalSaude.podeRealizar(tipo, especialidade)`** tem uma implementação em cada subclasse. O `verificarSePodeRealizar()` da classe mãe chama esse método sem nenhum `if` de tipo de médico.
2. **`ProfissionalSaude.podeEncaminhar()`**: só o clínico geral devolve `true`.
3. **`Atendimento.validar()`** é um método template: as regras comuns ficam na mãe, e cada modalidade implementa `validarRegrasDaModalidade()` (é o "cada modalidade deverá implementar sua própria lógica de validação" do PDF).
4. **`iniciar()`, `concluir()`, `cancelar()`**: as rotas genéricas do `ConsultaController` chamam sempre o mesmo método, e o comportamento muda com a modalidade. A emergência não pode ser cancelada; a cirurgia não pode ser concluída sem ter sido iniciada. O `ConsultaService` nem conhece as classes de emergência e cirurgia.
5. **`ocupaAgenda()` e `isConcluido()`**: a RN3 (horário do médico) e o histórico funcionam para as 4 modalidades sem `if` por tipo.
6. **`Consulta.reagendar()` + `copiar()`**: cada consulta cria uma cópia do próprio tipo.
7. **`getEspecialidadeExigida()`**: devolve `null` em `Atendimento`, e só a `ConsultaEspecializada` sobrescreve.
8. **Enums com comportamento**: `StatusConsulta.podeMudarPara()` e `TipoQuarto.aceitaCapacidade()`.

**Regra para todos:** fora da criação do objeto (os `switch` do `ConsultaService.criar` e do `ProfissionalSaudeService.cadastrar`), não usem `if`/`switch` por tipo. Se precisar perguntar "que tipo é esse?", é sinal de que falta um método polimórfico.

---

## 4. Endpoints

Todos devolvem JSON. Erro de regra de negócio devolve **400** `{"message": "..."}`, id inexistente devolve **404** e conflito no banco devolve **409**.

| Método | Rota | Quem pode | Responsável |
|---|---|---|---|
| GET | `/pacientes?busca=` | MEDICO, ADMIN | Leonardo Martins |
| GET | `/pacientes/perfil` | paciente logado | Leonardo Martins |
| GET | `/pacientes/{id}` | MEDICO, ADMIN | Leonardo Martins |
| GET | `/profissionais?categoria=&especialidade=` | logado | Leonardo Martins |
| GET | `/profissionais/especialidades` | logado | Leonardo Martins |
| GET | `/profissionais/{id}` | logado | Leonardo Martins |
| POST | `/profissionais` | ADMIN | Leonardo Martins |
| DELETE | `/profissionais/{id}` | ADMIN | Leonardo Martins |
| POST | `/consultas` | paciente (para si), ADMIN | João Pedro |
| GET | `/consultas?tipo=` | MEDICO, ADMIN | João Pedro |
| GET | `/consultas/{id}` | logado | João Pedro |
| GET | `/consultas/minhas` | paciente logado | João Pedro |
| GET | `/consultas/agenda?data=` | médico logado | João Pedro |
| GET | `/consultas/profissionais-disponiveis?especialidade=&data=&horario=` | logado | João Pedro |
| PUT | `/consultas/{id}/iniciar` | MEDICO | João Pedro (vale para as 4 modalidades) |
| PUT | `/consultas/{id}/concluir` | MEDICO | João Pedro (vale para as 4 modalidades) |
| PUT | `/consultas/{id}/cancelar` | paciente dono, MEDICO, ADMIN | João Pedro (vale para as 4 modalidades) |
| PUT | `/consultas/{id}/confirmar` | paciente dono, ADMIN | João Pedro |
| PUT | `/consultas/{id}/nao-compareceu` | MEDICO, ADMIN | João Pedro |
| PUT | `/consultas/{id}/encaminhar?especialidade=` | MEDICO (clínico da consulta) | João Pedro |
| POST | `/consultas/{id}/reagendar` | paciente dono, ADMIN | João Pedro |
| POST | `/consultas/emergencias` | MEDICO, ADMIN | Leonardo Gonzaga |
| GET | `/consultas/emergencias?status=` | MEDICO, ADMIN | Leonardo Gonzaga |
| PUT | `/consultas/emergencias/{id}/assumir?profissionalId=` | MEDICO | Leonardo Gonzaga |
| POST | `/consultas/cirurgias` | MEDICO, ADMIN | Leonardo Gonzaga |
| GET | `/consultas/procedimentos?cirurgiaoId=` | logado | Leonardo Gonzaga |
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

Exemplos de JSON estão nos arquivos do pacote `dto`.

---

## 5. Quem depende de quem

### O que foi feito para cortar dependências
- **O `podeRealizar` recebe o tipo, não o objeto.** `ProfissionalSaude.podeRealizar(TipoAtendimento, Especialidade)` não conhece as classes de atendimento, então a parte de profissionais não depende de ninguém.
- **Encaminhamento.** Quem grava o encaminhamento é a `ConsultaRotina`, perguntando `solicitante.podeEncaminhar()`. O médico não precisa conhecer a consulta.
- **Emergência e cirurgia em arquivos próprios.** Elas têm controller, service, repositórios e DTOs próprios. O João Pedro e o Leonardo Gonzaga **não mexem em nenhum arquivo em comum**.
- **Os services usam repositórios, não services de outra parte.** Paciente vem do `PacienteRepository.findById` e do `PacienteService.buscarPorEmail`, que **já existem**. Profissional vem do `ProfissionalSaudeRepository` (`findById`/`findByEmail`), sem passar pelas regras do `ProfissionalSaudeService`.
- **Remoção de médico.** A chave estrangeira do banco impede (RN6), então profissionais não precisa consultar atendimentos e internações.
- **Disponibilidade e procedimentos.** "Médicos livres no horário" ficou em `/consultas/profissionais-disponiveis` e "procedimentos do cirurgião" em `/consultas/procedimentos`, cada um com quem tem os dados.

### O que sobrou (não dá para tirar: é herança ou associação)

| Quem | Depende de | O quê | Quando fica pronto |
|---|---|---|---|
| **Leonardo Martins** | ninguém | | |
| **João Pedro** | Leonardo Martins | `ProfissionalSaude` + 3 subclasses, `TipoAtendimento`, `Especialidade`, `ProfissionalSaudeRepository`, exceções | fase 1 |
| **Leonardo Gonzaga** | João Pedro e Leonardo Martins | classe `Atendimento` (as classes dele herdam dela); as mesmas classes do Leonardo Martins | fase 1 |
| **Henrique** | Leonardo Martins; João Pedro (só no histórico) | `ProfissionalSaude`, `ProfissionalSaudeRepository`, exceções; `Atendimento` + `AtendimentoRepository` para o histórico, que fica por último | fase 1 |

Todas são dependências **de "a classe existir na main"**, não de "estar pronta". Por isso:

> **Contratos da fase 1** (até o 2º dia, PR pequeno direto para a `main`):
> - **Leonardo Martins:** `ProfissionalSaude`, `MedicoClinicoGeral`, `MedicoEspecialista`, `Cirurgiao`, os enums `Especialidade`, `CategoriaProfissional`, `TipoAtendimento`, o `ProfissionalSaudeRepository` e as exceções `RegraNegocioException` e `RecursoNaoEncontradoException`.
> - **João Pedro:** a classe `Atendimento` e o `AtendimentoRepository`.
>
> Enquanto o contrato não chega, dá para escrever o código normalmente: a assinatura de cada método já está no comentário da classe. Só não compila até o merge.

Fora do código Java:
- **Banco:** todo mundo precisa do Supabase no ar para **testar** (não para escrever código). Isso é do Leonardo Gonzaga, na fase 1.
- **Telas:** as telas do Gonzaga usam as rotas genéricas `/consultas/{id}/iniciar|concluir|cancelar` do João. Como as telas são da fase 3, essas rotas já vão existir. Todas as telas usam o `api.js` do Gonzaga (fase 1).

---

## 6. Banco de dados (o que muda no `schema.sql`)

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

As FKs de `atendimentos` e `internacoes` para `profissionais_saude` **não** podem ter `on delete cascade`: são elas que impedem remover um médico com histórico.

> **Obs. técnica:** as três subclasses de `Atendimento` que têm situação mapeiam a mesma coluna `status`, cada uma com o próprio enum. O Hibernate aceita isso em `SINGLE_TABLE`. Se reclamar de coluna repetida, a saída é usar colunas separadas (`status_consulta`, `status_emergencia`, `status_procedimento`) e avisar o Gonzaga para mudar o schema.

---

## 7. Quem faz o quê

Troquem as partes entre vocês se quiserem; basta atualizar a linha `RESPONSÁVEL:` dos arquivos.

### Leonardo Martins: Pessoas e acesso (profissionais, pacientes, login, exceções)
Não depende de ninguém. Os outros dependem dos seus contratos, então eles saem primeiro.

**Back (contratos, fase 1)**
- [ ] `models/ProfissionalSaude.java` (abstrata, `podeRealizar`, `podeEncaminhar`, `verificarSePodeRealizar`)
- [ ] `models/MedicoClinicoGeral.java`, `models/MedicoEspecialista.java`, `models/Cirurgiao.java`
- [ ] `models/enums/Especialidade.java`, `CategoriaProfissional.java`, `TipoAtendimento.java`
- [ ] `repository/ProfissionalSaudeRepository.java`
- [ ] `exception/RegraNegocioException.java`, `exception/RecursoNaoEncontradoException.java`

**Back (depois)**
- [ ] `exception/GlobalExceptionHandler.java`: handlers de 404, 400, 409 e JSON inválido
- [ ] `dto/ProfissionalRequest.java`, `service/ProfissionalSaudeService.java`, `controller/ProfissionalSaudeController.java`
- [ ] `service/DatabaseUserDetailsService.java`: login do médico pelo banco
- [ ] `config/SecurityConfig.java`: `@EnableMethodSecurity`, liberar `/js/**` (ver o bloco "A CRIAR")
- [ ] Pendências do paciente: `@JsonIgnore` no `Paciente.passwordHash`; `PacienteRepository` (`existsByCpf`, busca); `PacienteService` (`listar`, `buscar`, `buscarPorId`, CPF repetido); `controller/PacienteController.java`

**Telas**
- [ ] `admin/medicos`: select **Categoria** (clínico, especialista, cirurgião); campo Especialidade só para especialista; campo Área cirúrgica só para cirurgião; campo **Senha**; tabela com Categoria e Área de atuação; botão remover.
- [ ] `medico/pacientes`: `GET /pacientes?busca=`
- [ ] `paciente/perfil`: `GET /pacientes/perfil`
- [ ] (opcional) números do painel `admin/admin`

### João Pedro: Consultas
Depende só dos contratos do Leonardo Martins.

**Back**
- [ ] **Contrato (fase 1):** `models/Atendimento.java` (abstrata, `validar()` template, métodos abstratos) e `repository/AtendimentoRepository.java`
- [ ] `models/Consulta.java` (abstrata, `StatusConsulta`, `mudarStatus`, `reagendar`)
- [ ] `models/ConsultaRotina.java` (com `registrarEncaminhamento`), `models/ConsultaEspecializada.java`
- [ ] `models/enums/StatusConsulta.java`
- [ ] `dto/ConsultaRequest.java`
- [ ] `service/ConsultaService.java` (inclui `profissionaisDisponiveis`)
- [ ] `controller/ConsultaController.java`

**Telas**
- [ ] `paciente/agendar-consulta`: o select de especialidade tem "Clínica geral" (gera `ROTINA`) + `GET /profissionais/especialidades` (gera `ESPECIALIZADA`). Depois de escolher especialidade, data e horário, carrega os médicos em `GET /consultas/profissionais-disponiveis` e faz `POST /consultas`.
- [ ] `paciente/consultas`: `GET /consultas/minhas`; botões Confirmar, Cancelar e Reagendar conforme a situação; mostrar o encaminhamento, se tiver.
- [ ] `medico/consultas`: `GET /consultas/agenda?data=`; botões Iniciar, Concluir (pede as observações), Não compareceu e Encaminhar (só na rotina).

**No fim:** atualizar o `README.md` (estrutura, Sprint 2, tirar o "MASI MUDANCAS EM BREVE").

### Leonardo Gonzaga: Emergência, cirurgia e infraestrutura
Depende da classe `Atendimento` (João Pedro) e dos contratos do Leonardo Martins. Não mexe em nenhum arquivo deles.

**Infraestrutura (fase 1)**
- [ ] `supabase/schema.sql` novo (seção 6), **rodado no Supabase**, e o banco funcionando para o grupo. Mandem o `.env` por mensagem privada, **nunca no git**.
- [ ] `static/js/api.js` (seção 9)

**Back**
- [ ] `models/AtendimentoEmergencia.java`, `models/ProcedimentoCirurgico.java`
- [ ] `models/enums/StatusEmergencia.java`, `StatusProcedimento.java`, `Prioridade.java`, `TipoCirurgia.java`
- [ ] `dto/EmergenciaRequest.java`, `dto/CirurgiaRequest.java`
- [ ] `repository/AtendimentoEmergenciaRepository.java`, `repository/ProcedimentoCirurgicoRepository.java`
- [ ] `service/EmergenciaCirurgiaService.java`, `controller/EmergenciaCirurgiaController.java`

**Telas (novas, obrigatórias: como a API só é testada pelas telas, sem elas não dá para demonstrar emergência e cirurgia)**
- [ ] `medico/emergencias`: registrar emergência (paciente, queixa, prioridade); fila por prioridade; assumir; concluir (rota genérica `/consultas/{id}/concluir`).
- [ ] `medico/cirurgias`: agendar procedimento (paciente, cirurgião, data/hora prevista, descrição, tipo, obs. pré); iniciar, concluir (obs. pós) e cancelar pelas rotas genéricas.
- [ ] Rotas `/medico/emergencias` e `/medico/cirurgias` no `SecureLoginController` e links no menu lateral das páginas do médico (façam isso **por último**, depois que os outros terminarem as telas do médico, para não dar conflito).

### Henrique: Quartos, internações e histórico
Depende só dos contratos do Leonardo Martins. O histórico, feito por último, usa também a classe `Atendimento` do João Pedro.

**Back**
- [ ] `models/Quarto.java` (+ `tipo`, `ocupar`, `liberar`, `validar`), `models/enums/TipoQuarto.java`
- [ ] `repository/QuartoRepository.java` (inclui `buscarParaAtualizar` com trava)
- [ ] `service/QuartoService.java`, `controller/QuartoController.java`
- [ ] `models/Internacao.java` (+ `status`, `darAlta`), `models/enums/StatusInternacao.java`
- [ ] `dto/InternacaoRequest.java`, `repository/InternacaoRepository.java`
- [ ] `service/InternacaoService.java`, `controller/InternacaoController.java`
- [ ] **Por último:** `models/HistoricoMedico.java`, `service/HistoricoMedicoService.java`, `controller/HistoricoMedicoController.java`

**Telas**
- [ ] `medico/quartos`: lista com Tipo e Situação, filtro por tipo, cadastrar quarto (select de tipo vindo de `GET /quartos/tipos`)
- [ ] `medico/internacoes`: internados agora; registrar internação (selects de paciente, médico e quartos disponíveis); dar alta
- [ ] `paciente/internacoes`: `GET /internacoes/minhas`
- [ ] `paciente/historico`: `GET /historico`, mostrando o tipo de cada atendimento

---

## 8. Ordem de trabalho

| Fase | O quê | Pronto quando |
|---|---|---|
| **1. Contratos e base** (primeiros 2 dias) | Contratos do Leonardo Martins e do João Pedro (seção 5); schema, banco no ar e `api.js` (Gonzaga). Henrique já começa Quarto e Internação. | `main` compila com os contratos e o Supabase responde |
| **2. Regras e API** | Cada um termina models, repositories, services e controllers da sua parte | as rotas da seção 4 respondem |
| **3. Telas** | Cada um liga as suas telas | dá para fazer o roteiro abaixo só clicando |
| **4. Fechamento** | Roteiro de teste completo, conferir o diagrama com o código, README, ensaio da apresentação | |

### Roteiro de teste (serve também para a apresentação)
1. Admin cadastra um clínico, um cardiologista e um cirurgião.
2. Paciente agenda uma consulta de rotina com o clínico. Outro paciente tenta o mesmo médico no mesmo horário → erro (RN3).
3. Consulta: confirmar → iniciar → concluir com observações. Tentar cancelar a concluída → erro.
4. Cancelar uma consulta e reagendar → aparece uma consulta nova, e a antiga continua cancelada.
5. Clínico encaminha para cardiologia → paciente agenda a especializada com o cardiologista.
6. Emergências CRÍTICA e BAIXA → a fila mostra a crítica primeiro. Tentar assumir com o cirurgião → erro. Assumir com o clínico → concluir.
7. Cirurgia: agendar; tentar concluir sem iniciar → erro; iniciar → concluir.
8. Cadastrar apartamento com 3 leitos → erro. Internar até lotar um quarto → erro no próximo. Dar alta libera o leito.
9. Tentar remover um médico que já atendeu → erro. Remover um recém-cadastrado → ok.
10. Histórico do paciente mostra só os atendimentos concluídos, de todas as modalidades, e as internações.

> Os usuários de teste do `application.properties` (`paciente` e `medico`) **não existem no banco**, então rotas como `/consultas/minhas` não funcionam com eles. Com o banco no ar, cadastrem pacientes pelo `/register` e médicos pelo admin.

---

## 9. Telas: como chamar a API

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

## 10. Git

- Cada um na sua branch (`Leonardo` e `Joao-Pedro` já existem; criem `Henrique` e `Leonardo-Gonzaga`), sempre a partir da `main` atualizada.
- **Os contratos da fase 1 vão em PRs pequenos e rápidos.** O resto pode ir em PRs maiores.
- **Nenhum arquivo Java tem dois donos.** Os únicos arquivos mexidos por mais de uma pessoa:
  - `SecurityConfig`: só o Leonardo Martins mexe; quem precisar de algo pede a ele.
  - `schema.sql`: só o Gonzaga mexe; quem precisar de coluna pede a ele.
  - `SecureLoginController` e o menu lateral das páginas do médico: o Gonzaga mexe só no fim (fase 4).
- `.env` nunca vai para o git.

---

## 11. Checklist das entregas do PDF

| Entrega | Onde está | Status |
|---|---|---|
| 1. Diagrama de classes atualizado | `DOCUMENTOS/DiagramaUML/` (domínio e camadas) | feito; conferir com o código na fase 4 |
| 2. Classes de domínio com atributos, métodos e regras | `models/` e `models/enums/` | a fazer (seção 7) |
| 3. Endpoints `/pacientes`, `/profissionais`, `/consultas`, `/internacoes`, `/quartos` | `controller/` | a fazer (seção 4) |
| 4. Regras de categorias de profissionais | `podeRealizar()` e `podeEncaminhar()` nas 3 subclasses, filtro por especialidade | a fazer |
| 4. Regras das modalidades de atendimento | `validarRegrasDaModalidade()` nas 4 modalidades | a fazer |
| 4. Situações das consultas | `StatusConsulta.podeMudarPara()` + `Consulta.mudarStatus()` | a fazer |
| 4. Procedimentos cirúrgicos | `ProcedimentoCirurgico` + `StatusProcedimento` (não conclui sem iniciar) | a fazer |
| 4. Tipos de quartos | `TipoQuarto.aceitaCapacidade()` + `Quarto.validar()` | a fazer |
