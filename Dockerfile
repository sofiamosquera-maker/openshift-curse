# Usa una imagen de Java para compilar
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copia el código fuente
COPY Main.java .

# Compila el código
RUN javac Main.java

# Imagen final para ejecución
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia el archivo compilado desde la fase de build
COPY --from=build /app/Main*.class .

RUN addgroup --system appgroup && adduser --system --group appuser

RUN chown -R appuser:appgroup /app

USER appuser

# Exponer el puerto
EXPOSE 3000

# Comando para ejecutar el servicio
CMD ["java", "Main"]
