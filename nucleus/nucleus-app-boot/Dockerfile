FROM openjdk:11.0.11-jre-slim
VOLUME /tmp
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=${ENV}","-jar","/app.jar"]