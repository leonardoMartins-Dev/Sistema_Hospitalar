> ## ⚠️ ATENÇÃO
>
> O banco de dados (Supabase) **ainda não está funcionando**. Enquanto isso, dá para entrar no site e ver as telas com estes **3 logins de teste**. Eles ficam fixos no [`application.properties`](MedVita/src/main/resources/application.properties) e não precisam de banco:
>
> | Perfil        | Usuário    | Senha  | Entra em  |
> |---------------|------------|--------|-----------|
> | Paciente      | `paciente` | `1234` | `/home`   |
> | Médico        | `medico`   | `1234` | `/medico` |
> | Administrador | `admin`    | `1234` | `/admin`  |
>
> - O `.env` ainda precisa das chaves do **reCAPTCHA**, porque o captcha continua sendo pedido no login. As linhas `SUPABASE_*` podem ficar de fora.
> - Sem o banco, **cadastro (`/register`) e recuperação de senha não funcionam**, e os números dos painéis ainda não vêm de dados reais.
> - Quando o banco estiver funcionando, apague o bloco `USUÁRIOS DE TESTE` do `application.properties`. O admin continua.

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

> **Em desenvolvimento:** por enquanto só o login usa o banco. Os números dos painéis (consultas, internações, quartos) ainda não vêm de dados reais. As classes do restante do backend já estão criadas, cada uma com a documentação do que vai fazer (veja [Estrutura do projeto](#estrutura-do-projeto)). A justificativa da escolha do banco está em [`DOCUMENTOS/BancoDeDados/`](DOCUMENTOS/BancoDeDados/JustificativaBancoRelacional.txt).

## Tecnologias

- Java 17
- Spring Boot 3.3.5 (Web, Security, Thymeleaf, Mail, Data JPA)
- PostgreSQL no [Supabase](https://supabase.com)
- Google reCAPTCHA v2
- Maven

## Como rodar

### 1. Pré-requisitos

- JDK 17 ou superior
- Maven

### 2. Criar o banco no Supabase

1. Crie um projeto em <https://supabase.com>. Guarde a **Database Password** pedida na criação.
2. Abra o **SQL Editor > New query**, cole todo o conteúdo de [`MedVita/supabase/schema.sql`](MedVita/supabase/schema.sql) e clique em **Run**. Isso cria todas as tabelas do sistema de uma vez.
3. Clique em **Connect**, escolha **Direct** e selecione o **Session pooler**. A string tem este formato:

```text
postgresql://postgres.abcdefghijklmnop:[YOUR-PASSWORD]@aws-0-sa-east-1.pooler.supabase.com:5432/postgres
             └─────── usuário ───────┘ └─── senha ───┘ └────────────── endereço do banco ──────────────┘
```

> Use o **Session pooler**, não a "Direct connection": a conexão direta só funciona em redes IPv6 e costuma dar timeout.

### 3. Criar o arquivo `.env`

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

### 4. Iniciar

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
| Médico        | Cadastrado pelo administrador na página `/admin/medicos` (ainda a implementar).             |

## Estrutura do projeto

```
Sistema_Hospitalar/
├── DOCUMENTOS/                # documentos do trabalho (diagramas, relatórios...)
├── IMAGENS/                   # capturas de tela usadas neste README
└── MedVita/                   # aplicação Spring Boot
    ├── supabase/schema.sql    # script que cria as tabelas no Supabase
    ├── .env.example           # modelo do .env (sem senhas)
    └── src/
        ├── main/
        │   ├── java/com/example/MedVita/
        │   │   ├── application/   # classe principal (TelaLoginApplication)
        │   │   ├── config/        # Spring Security e reCAPTCHA
        │   │   ├── controller/    # páginas (SecureLoginController) e API REST de cada model
        │   │   ├── exception/     # exceções do sistema e tratamento de erros
        │   │   ├── models/        # Paciente, ProfissionalSaude, Consulta, Internacao, Quarto, HistoricoMedico
        │   │   ├── repository/    # acesso ao banco (Spring Data JPA)
        │   │   └── service/       # regras de negócio
        │   └── resources/
        │       ├── static/        # CSS e imagens
        │       └── templates/     # páginas HTML (login, paciente, medico, admin)
        └── test/                  # testes automatizados dos services
```

Os arquivos marcados com `STATUS: A IMPLEMENTAR` ainda não têm código: cada um explica o que aquela parte vai fazer, quais regras de negócio do enunciado ela cobre e quais telas a usam.

Os documentos do trabalho ficam em `DOCUMENTOS/`, cada tipo na sua própria pasta (ex.: `DOCUMENTOS/DiagramaUML/arquivo`).

MASI MUDANCAS EM BREVE
