FROM eclipse-temurin:21-jre
VOLUME /tmp
ARG JAR_FILE=catalog-chat-boot/target/*.jar
COPY ${JAR_FILE} app.jar
COPY rag /app/rag
ENTRYPOINT ["java","-jar","/app.jar"]
