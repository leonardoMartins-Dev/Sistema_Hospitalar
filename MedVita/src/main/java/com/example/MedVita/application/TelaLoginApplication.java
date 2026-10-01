package com.example.MedVita.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * CLASSE PRINCIPAL DA APLICAÇÃO (ponto de entrada do Spring Boot).
 *
 * O main() sobe o servidor embutido (Tomcat, porta 8080) e cria o "contexto"
 * do Spring: ele procura as classes anotadas (@Controller, @Service,
 * @Configuration, @Component, repositórios e entidades), cria um objeto de
 * cada e injeta um no outro pelos construtores. Também lê o
 * application.properties / .env e abre o pool de conexões com o Supabase.
 *
 * ATENÇÃO À LOCALIZAÇÃO DO PACOTE:
 * Esta classe está em "com.example.MedVita.application", mas o resto do
 * código está em pacotes "irmãos" (config, service, repository, models...).
 * Por padrão o Spring só procura a partir do pacote da classe principal, então
 * são necessárias três configurações explícitas:
 *   - scanBasePackages       -> encontra @Controller, @Service, @Configuration...
 *   - @EntityScan            -> encontra as classes @Entity (models.Paciente)
 *   - @EnableJpaRepositories -> encontra as interfaces JpaRepository
 *                               (repository.PacienteRepository)
 * O scanBasePackages NÃO vale para entidades e repositórios. Sem as duas
 * últimas o app nem sobe ("No qualifying bean of type PacienteRepository" /
 * "Not a managed type: class Paciente").
 *
 * Alternativa mais simples (padrão do Spring): mover esta classe para o pacote
 * raiz "com.example.MedVita" e remover as três configurações acima.
 */
@SpringBootApplication(scanBasePackages = {"com.example"})
@EntityScan(basePackages = "com.example.MedVita")
@EnableJpaRepositories(basePackages = "com.example.MedVita")
public class TelaLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelaLoginApplication.class, args);
	}

}
