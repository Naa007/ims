FROM maven:3-amazoncorretto-17 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn install

FROM openjdk:8-alpine
#COPY --from=build /usr/app/target/subscription-migration-*.jar /runner.jar
#COPY --from=build /usr/app/src/main/resources/CommonValidationRules.json /CommonValidationRules.json
#COPY --from=build /usr/app/src/main/resources/CredentialsConfig.json /CredentialsConfig.json
#COPY --from=build /usr/app/src/main/resources/input  /input
#COPY --from=build /usr/app/src/main/resources/messages.properties /messages.properties
#COPY --from=build /usr/app/src/main/resources/output /output
#COPY --from=build /usr/app/src/main/resources/serviceValidationRules.json /serviceValidationRules.json
#COPY --from=build /usr/app/src/main/resources/config.json /config.json
#COPY --from=build /usr/app/src/main/resources/DB /DB
#COPY --from=build /usr/app/src/main/resources/log4j2.properties /log4j2.properties
#COPY --from=build /usr/app/src/main/resources/Migration.bat /Migration.bat
#COPY --from=build /usr/app/src/main/resources/project.properties /project.properties
#COPY --from=build /usr/app/src/main/resources/synchronize /synchronize
#
#RUN chmod +x /input/*
#RUN chmod 777 /output

ENTRYPOINT ["java","-jar","ims.jar"]