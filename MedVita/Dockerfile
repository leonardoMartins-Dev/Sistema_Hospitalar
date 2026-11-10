# =========================================================
# IMAGEM DO MEDVITA
# Responsável: Leonardo Martins
# =========================================================
#
# Usada pelo docker-compose.yml (é ele que você roda, não este arquivo).
#
# Duas etapas: a primeira compila o projeto com o Maven e a segunda só roda
# o .jar. Assim ninguém precisa instalar Java nem Maven no computador.
#
# Nenhuma senha entra na imagem (o .dockerignore deixa o .env de fora):
# as variáveis vêm do docker-compose.yml.


# ---------- Etapa 1: compilar ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# As dependências são baixadas antes de copiar o código. Enquanto o pom.xml
# não muda, o Docker reaproveita esta etapa e o build fica bem mais rápido.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Os testes não rodam aqui: o TelaLoginApplicationTests precisa de um banco.
RUN mvn -B -q package -DskipTests


# ---------- Etapa 2: rodar ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# O app não roda como root dentro do container
RUN useradd --system --create-home medvita
USER medvita

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
