# Crea una imagen de docker con la aplicación
FROM eclipse-temurin:17.0.6_10-jdk
# Crea la carpeta app en la imagen de docker
WORKDIR /gateway
# Copia el jar generado en la carpeta target a la carpeta app de la imagen
COPY target/ApiGateway-0.0.1-SNAPSHOT.jar gateway.jar
# Ejecuta el jar al iniciar el contenedor de docker
ENTRYPOINT ["java", "-jar", "gateway.jar"]