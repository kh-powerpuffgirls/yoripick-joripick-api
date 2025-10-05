FROM openjdk:17

ARG VERSION

#복사할 jar파일의 [현재위치] -> [복사될 위치]
COPY target/yoripick-joripick-api-0.0.1-SNAPSHOT.jar /app/yoripick-joripick-api.jar

LABEL maintainer="powerpuffgirls" \
      title="Yoripick Joripick Api" \
      version="$VERSION" \
      description="This image is Yoripick Joripick Api service"
      
ENV APP_HOME /app
EXPOSE 8081 8443
VOLUME /app/upload

#컨테이너 실행시 호출할 명령어
WORKDIR $APP_HOME
ENTRYPOINT ["java","-jar","/app/yoripick-joripick-api.jar"]
CMD ["--server.port=8081", \
     "--server.ssl.enabled=true", \
     "--server.ssl.key-store=/app/keystore.p12", \
     "--server.ssl.key-store-password=changeit", \
     "--server.ssl.key-store-type=PKCS12", \
     "--server.ssl.key-alias=springboot"]
