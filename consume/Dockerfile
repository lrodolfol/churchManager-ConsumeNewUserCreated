# Etapa 1: Construir o projeto Java usando Maven
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Define o diretório de trabalho para o build
WORKDIR /app

# Copia o pom.xml do diretório pai (um nível acima do Dockerfile)
COPY pom.xml ./

# Copia a pasta src do diretório pai para dentro do container
COPY src ./src

# Executa o build do projeto (gera o .jar na pasta target)
RUN mvn clean package -DskipTests

# Etapa 2: Configurar o container para rodar a aplicação
FROM openjdk:21-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o artefato gerado na etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta da aplicação (ajuste caso sua aplicação use outra porta)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
