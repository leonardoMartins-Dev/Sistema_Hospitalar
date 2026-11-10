# MedVita — Sistema Hospitalar

Trabalho prático da disciplina de **Programação Modular**.

O MedVita é um sistema web de gestão hospitalar. Pacientes, médicos e administradores entram pela mesma tela de login e cada um é levado para a sua própria área.

![Tela de login](IMAGENS/login.png)

## Telas

### Portal do Paciente
Depois do login, o paciente vê a próxima consulta, as internações ativas e o histórico. Também tem acesso rápido para agendar consultas e ver os próprios dados.

![Portal do Paciente](IMAGENS/paciente.png)

### Área Médica
O médico acompanha as consultas do dia, os pacientes internados e a disponibilidade de quartos.

![Área Médica](IMAGENS/medico.png)

### Administração
O administrador vê um resumo do hospital e gerencia o quadro de médicos.

![Administração](IMAGENS/admin.png)

## Funcionalidades

- **Login com reCAPTCHA** (Google reCAPTCHA v2, o "Não sou um robô").
- **Três perfis de acesso** (paciente, médico e administrador). Cada perfil só abre as próprias páginas e, depois do login, vai direto para a sua área.
- **Cadastro de paciente** com nome, e-mail, CPF, endereço, telefone, data de nascimento e senha.
- **Recuperação de senha por e-mail.** O usuário recebe um link para criar uma nova senha, válido por 15 minutos.
- **Senhas criptografadas** com BCrypt.
- **Pacientes salvos no banco** (PostgreSQL no Supabase). Quem se cadastra continua existindo depois que o servidor reinicia.

- **Consultas, emergências, cirurgias, internações e quartos salvos no banco**, com as regras de negócio do enunciado (veja [Sprint 2](#sprint-2)). Os painéis iniciais de cada perfil mostram os números reais.

A justificativa da escolha do banco está em [`DOCUMENTOS/BancoDeDados/`](DOCUMENTOS/BancoDeDados/JustificativaBancoRelacional.txt).

## Tecnologias

- Java 17
- Spring Boot 3.3.5 (Web, Security, Thymeleaf, Mail, Data JPA)
- PostgreSQL no [Supabase](https://supabase.com)
- Google reCAPTCHA v2
- Maven
- Docker e Docker Compose

## Como rodar

O jeito recomendado é com Docker: não precisa instalar Java nem Maven, nem criar conta no Supabase. O banco já vem com contas de teste prontas.

### Com Docker (recomendado)

#### 1. Instalar o Docker

Instale o [Docker Desktop](https://www.docker.com/products/docker-desktop/) e abra o programa. Ele precisa estar aberto sempre que você for rodar o projeto.

#### 2. Subir o sistema

Abra um terminal na pasta do projeto e rode:

```bash
cd MedVita
docker compose up --build
```

Na primeira vez demora alguns minutos, porque o Docker baixa o Java, o Maven, o PostgreSQL e as dependências. Das próximas vezes é bem mais rápido.

O sistema está pronto quando aparecer no terminal uma linha com `Started TelaLoginApplication`. Deixe esse terminal aberto enquanto usa o site.

#### 3. Entrar no site

Acesse <http://localhost:8080> e entre com uma das contas de teste. **A senha de todas é `1234`.**

| Perfil                   | Login                      | Nome                                |
|--------------------------|----------------------------|-------------------------------------|
| Administrador            | `admin`                    | Administrador                       |
| Paciente                 | `paciente@medvita.com`     | Ana Souza                           |
| Paciente                 | `paciente2@medvita.com`    | Bruno Lima                          |
| Médico clínico geral     | `clinico@medvita.com`      | Dr. Carlos Mendes                   |
| Médico especialista      | `especialista@medvita.com` | Dra. Helena Rocha (cardiologia)     |
| Cirurgião                | `cirurgiao@medvita.com`    | Dr. Paulo Teixeira (cirurgia geral) |

O banco também já vem com três quartos: 101 (enfermaria), 201 (apartamento) e 301 (UTI). Consultas, emergências, cirurgias e internações não vêm prontas: crie pelas telas.

No login, marque o "Não sou um robô". O aviso vermelho de "somente para testes" no captcha é normal no Docker.

As contas de teste ficam no [`dados-teste.sql`](MedVita/supabase/dados-teste.sql). Você também pode criar outras: pacientes pela página `/register` e médicos pelo admin, em `/admin/medicos`.

#### 4. Parar

Aperte `Ctrl + C` no terminal onde o sistema está rodando, ou rode `docker compose down` na pasta `MedVita/`. O que você cadastrou continua salvo para a próxima vez.

#### Comandos úteis

Todos na pasta `MedVita/`:

| Comando                       | O que faz |
|-------------------------------|-----------|
| `docker compose up --build`   | sobe tudo; rode de novo depois de mudar o código |
| `docker compose down`         | para tudo (o banco continua salvo) |
| `docker compose down -v`      | para e **apaga** o banco local; no próximo `up` ele é recriado só com as contas de teste |
| `docker compose logs -f app`  | mostra o log do Spring Boot |

#### Como funciona

O [`docker-compose.yml`](MedVita/docker-compose.yml) sobe dois containers:

- **db**: um PostgreSQL só seu. Na primeira vez, ele roda o [`schema.sql`](MedVita/supabase/schema.sql), que cria as tabelas, e depois o [`dados-teste.sql`](MedVita/supabase/dados-teste.sql), que cria as contas de teste. Cada integrante tem o próprio banco, então ninguém apaga os dados de ninguém.
- **app**: o MedVita, compilado pelo [`Dockerfile`](MedVita/Dockerfile).

Não precisa de arquivo `.env`. Sem ele, o reCAPTCHA usa as chaves de teste do Google e a recuperação de senha não envia e-mail. Se quiser usar suas próprias chaves ou o seu Gmail, preencha só essas variáveis no `MedVita/.env` (o modelo é o [`.env.example`](MedVita/.env.example)). Deixe as três `SUPABASE_DB_*` vazias para usar o banco local.

Para abrir o banco local no DBeaver ou no pgAdmin: `localhost:5433`, com banco, usuário e senha `medvita`.

#### Problemas comuns

| Problema | O que fazer |
|----------|-------------|
| `Cannot connect to the Docker daemon` | Abra o Docker Desktop e espere ele terminar de iniciar. |
| `port is already allocated` ou "porta 8080 em uso" | Outro programa ou container está usando a porta 8080. Feche o programa, ou veja o container com `docker ps` e pare com `docker stop <nome>`. |
| As contas de teste não funcionam | O seu banco local foi criado antes delas existirem. Rode `docker compose down -v` e depois `docker compose up` (isso apaga o que você tinha cadastrado). |
| Mudei o `schema.sql` e o banco não mudou | O banco só é criado na primeira vez. Rode `docker compose down -v` e depois `docker compose up`. |
| Erro `tenant/user ... not found` ou de conexão com o banco | O seu `MedVita/.env` está com o Supabase preenchido. Deixe `SUPABASE_DB_URL`, `SUPABASE_DB_USER` e `SUPABASE_DB_PASSWORD` vazias. |
| Mudei o código e nada mudou no site | Pare e suba de novo com `docker compose up --build`. |

### Sem Docker

#### 1. Pré-requisitos

- JDK 17 ou superior
- Maven

#### 2. Criar o banco no Supabase

1. Crie um projeto em <https://supabase.com>. Guarde a **Database Password** pedida na criação.
2. Abra o **SQL Editor > New query**, cole todo o conteúdo de [`MedVita/supabase/schema.sql`](MedVita/supabase/schema.sql) e clique em **Run**. Isso cria todas as tabelas do sistema de uma vez.
3. Clique em **Connect**, escolha **Direct** e selecione o **Session pooler**. A string tem este formato:

```text
postgresql://postgres.abcdefghijklmnop:[YOUR-PASSWORD]@aws-0-sa-east-1.pooler.supabase.com:5432/postgres
             └─────── usuário ───────┘ └─── senha ───┘ └────────────── endereço do banco ──────────────┘
```

> Use o **Session pooler**, não a "Direct connection": a conexão direta só funciona em redes IPv6 e costuma dar timeout.

#### 3. Criar o arquivo `.env`

Copie o [`MedVita/.env.example`](MedVita/.env.example) para `MedVita/.env` e preencha. Ele não vai para o Git.

```properties
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app
RECAPTCHA_SITE_KEY=sua-site-key
RECAPTCHA_SECRET_KEY=sua-secret-key
SUPABASE_DB_URL=jdbc:postgresql://aws-0-<regiao>.pooler.supabase.com:5432/postgres
SUPABASE_DB_USER=postgres.<project-ref>
SUPABASE_DB_PASSWORD=sua-senha-do-banco
```

- **MAIL_PASSWORD** é uma *senha de app* do Gmail, não a senha normal da conta. Para gerar uma, ative a verificação em duas etapas e acesse <https://myaccount.google.com/apppasswords>.
- **Chaves do reCAPTCHA:** crie em <https://www.google.com/recaptcha/admin>, escolhendo o tipo **v2 "Não sou um robô"** e adicionando `localhost` como domínio.
- **Supabase:** `SUPABASE_DB_URL` é `jdbc:postgresql://` + tudo o que vem depois do `@` na string do passo 2; `SUPABASE_DB_USER` é o que vem entre `postgresql://` e o `:`; `SUPABASE_DB_PASSWORD` é a senha do banco.

#### 4. Iniciar

```bash
cd MedVita
mvn spring-boot:run
```

Depois, acesse <http://localhost:8080>.

### Usuários

| Perfil        | Como entra                                                                                  |
|---------------|---------------------------------------------------------------------------------------------|
| Administrador | Fixo no [`application.properties`](MedVita/src/main/resources/application.properties) (`admin` / `1234`). Não fica no banco. |
| Paciente      | Se cadastra pela página `/register`.                                                        |
| Médico        | Cadastrado pelo administrador na página `/admin/medicos`, como clínico geral, especialista ou cirurgião. |

No Docker, o banco já vem com contas de teste de cada perfil: veja a tabela em [Entrar no site](#3-entrar-no-site).

## Estrutura do projeto

```
Sistema_Hospitalar/
├── DOCUMENTOS/                # documentos do trabalho (diagramas, relatórios...)
├── IMAGENS/                   # capturas de tela usadas neste README
└── MedVita/                   # aplicação Spring Boot
    ├── Dockerfile             # imagem do app (usada pelo docker-compose.yml)
    ├── docker-compose.yml     # sobe o app e um PostgreSQL local
    ├── supabase/schema.sql    # script que cria as tabelas (no Supabase e no Postgres do Docker)
    ├── supabase/dados-teste.sql  # contas de teste e quartos do banco local do Docker
    ├── .env.example           # modelo do .env (sem senhas)
    └── src/
        ├── main/
        │   ├── java/com/example/MedVita/
        │   │   ├── application/   # classe principal (TelaLoginApplication)
        │   │   ├── config/        # Spring Security e reCAPTCHA
        │   │   ├── controller/    # páginas (SecureLoginController) e API REST de cada model
        │   │   ├── dto/           # corpos dos pedidos da API (ConsultaRequest, ProfissionalRequest...)
        │   │   ├── exception/     # exceções do sistema e tratamento de erros
        │   │   ├── models/        # Paciente, ProfissionalSaude (+3 categorias), Atendimento (+4 modalidades),
        │   │   │   │              # Internacao, Quarto, HistoricoMedico
        │   │   │   └── enums/     # Especialidade, StatusConsulta, TipoQuarto e os outros enums
        │   │   ├── repository/    # acesso ao banco (Spring Data JPA)
        │   │   └── service/       # regras de negócio
        │   └── resources/
        │       ├── static/        # CSS, imagens e js/api.js (chamadas da API pelas telas)
        │       └── templates/     # páginas HTML (login, paciente, medico, admin)
        └── test/                  # testes dos services (adiados: por enquanto só a documentação)
```

Cada classe começa com um comentário que explica o que ela faz, quais regras de negócio do enunciado cobre, quais telas a usam e quem é o responsável (linhas `STATUS` e `RESPONSÁVEL`).

Os documentos do trabalho ficam em `DOCUMENTOS/`, cada tipo na sua própria pasta (ex.: `DOCUMENTOS/DiagramaUML/arquivo`).

## Sprint 2

- **Categorias de profissional:** `ProfissionalSaude` abstrata com `MedicoClinicoGeral`, `MedicoEspecialista` e `Cirurgiao`. Cada uma diz o que pode realizar (`podeRealizar`) e se pode encaminhar (`podeEncaminhar`).
- **Modalidades de atendimento:** `Atendimento` abstrata, com `ConsultaRotina` e `ConsultaEspecializada` (via `Consulta`), `AtendimentoEmergencia` e `ProcedimentoCirurgico`. As regras comuns ficam em `validar()` e cada modalidade implementa `validarRegrasDaModalidade()`.
- **Situações da consulta:** enum `StatusConsulta` (agendada, confirmada, em atendimento, concluída, cancelada, não compareceu), com as mudanças permitidas em `podeMudarPara()`. Consulta cancelada é final: reagendar cria uma consulta nova.
- **Consultas** (`/consultas`): agendar, confirmar, iniciar, concluir com observações, cancelar, registrar falta, reagendar e encaminhar para especialista. As rotas `iniciar`, `concluir` e `cancelar` valem para as 4 modalidades.
- **Emergência e cirurgia** (`/consultas/emergencias`, `/consultas/cirurgias`): a fila de emergência sai por prioridade (crítica primeiro) e depois por ordem de chegada, e o cirurgião não assume emergência. A cirurgia não pode ser concluída sem ter sido iniciada, e o cirurgião não pode ter dois procedimentos no mesmo horário.
- **Quartos e internações** (`/quartos`, `/internacoes`): enum `TipoQuarto` com o limite de leitos de cada tipo (enfermaria de 2 a 6, apartamento de 1 a 2, UTI de 1 a 10). Internar ocupa um leito, a alta libera, e quarto lotado não recebe paciente.
- **Histórico** (`/historico`): mostra só os atendimentos concluídos, das 4 modalidades, e as internações do paciente.
- **Profissionais e pacientes** (`/profissionais`, `/pacientes`): o admin cadastra e remove médicos (quem já tem atendimento não pode ser removido), e o médico busca pacientes por nome, CPF ou e-mail.
- **Telas ligadas na API:** todas as páginas do paciente, do médico e do admin, inclusive os painéis iniciais. Cada perfil só abre as próprias páginas.

A divisão de tarefas e a lista completa de endpoints estão em [`DOCUMENTOS/Sprint2/DivisaoTarefas.md`](DOCUMENTOS/Sprint2/DivisaoTarefas.md).
