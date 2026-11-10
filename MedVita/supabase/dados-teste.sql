-- =========================================================
-- DADOS DE TESTE DO MEDVITA (banco local do Docker)
-- Responsável: Leonardo Martins
-- =========================================================

-- O docker-compose.yml roda este arquivo logo depois do schema.sql, quando o
-- banco local é criado: na primeira vez ou depois de "docker compose down -v".

-- Todas as contas usam a senha 1234. O password_hash abaixo é o BCrypt de
-- "1234", no mesmo formato que o passwordEncoder do SecurityConfig gera.
-- O admin não está aqui: ele vem do application.properties (admin / 1234).

-- Consultas, emergências, cirurgias e internações não vêm prontas:
-- crie pelas telas para testar.


-- =========================================================
-- PACIENTES (login pela tela inicial, perfil paciente)
-- =========================================================

insert into public.pacientes
    (email, password_hash, nome, cpf, telefone, endereco, data_nascimento)
values
    ('paciente@medvita.com',
     '$2a$10$OfGhHWVujuiXxjFkXGGhHO4YpDPDpORsnXOW1l8zEGpSsW7ZyjoBK',
     'Ana Souza', '12345678909', '31999990001',
     'Rua das Flores, 100 - Belo Horizonte', '1990-05-12'),

    ('paciente2@medvita.com',
     '$2a$10$OfGhHWVujuiXxjFkXGGhHO4YpDPDpORsnXOW1l8zEGpSsW7ZyjoBK',
     'Bruno Lima', '98765432100', '31999990002',
     'Av. Afonso Pena, 2000 - Belo Horizonte', '1985-09-30');


-- =========================================================
-- MÉDICOS (um de cada categoria)
-- =========================================================

insert into public.profissionais_saude
    (tipo, nome, registro_profissional, telefone, email, password_hash,
     especialidade, area_cirurgica)
values
    ('CLINICO_GERAL', 'Dr. Carlos Mendes', 'CRM 100001-MG', '31988880001',
     'clinico@medvita.com',
     '$2a$10$OfGhHWVujuiXxjFkXGGhHO4YpDPDpORsnXOW1l8zEGpSsW7ZyjoBK',
     null, null),

    ('ESPECIALISTA', 'Dra. Helena Rocha', 'CRM 100002-MG', '31988880002',
     'especialista@medvita.com',
     '$2a$10$OfGhHWVujuiXxjFkXGGhHO4YpDPDpORsnXOW1l8zEGpSsW7ZyjoBK',
     'CARDIOLOGIA', null),

    ('CIRURGIAO', 'Dr. Paulo Teixeira', 'CRM 100003-MG', '31988880003',
     'cirurgiao@medvita.com',
     '$2a$10$OfGhHWVujuiXxjFkXGGhHO4YpDPDpORsnXOW1l8zEGpSsW7ZyjoBK',
     null, 'Cirurgia geral');


-- =========================================================
-- QUARTOS (um de cada tipo, dentro dos limites do TipoQuarto)
-- =========================================================

insert into public.quartos (numero, andar, tipo, capacidade_maxima)
values
    ('101', 1, 'ENFERMARIA',  4),
    ('201', 2, 'APARTAMENTO', 2),
    ('301', 3, 'UTI',         5);
