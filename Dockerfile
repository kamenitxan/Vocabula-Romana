# syntax=docker/dockerfile:1

FROM openjdk:17-alpine
RUN apk --no-cache add nginx
RUN apk --no-cache add curl
RUN apk add --no-cache tzdata
ENV TZ Europe/Prague
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY etc/healthcheck.sh healthcheck.sh
COPY etc/prometheus.yaml prometheus.yaml
COPY etc/jmx_prometheus_javaagent-0.17.0.jar jmx_prometheus_javaagent.jar
COPY etc/nginx.conf /etc/nginx/nginx.conf
COPY etc/nginx_server.conf /etc/nginx/http.d/default.conf
COPY modules/backend/target/scala-2.13/vocabularomana-assembly-0.4-SNAPSHOT.jar /app/app.jar
COPY modules/backend/jakon_config_prod.properties /app/jakon_config.properties
COPY modules/backend/templates /app/templates
COPY modules/backend/static /app/static

HEALTHCHECK --interval=10s CMD sh /healthcheck.sh

WORKDIR /app
CMD ["/bin/sh", "-c", "/usr/sbin/nginx && java -javaagent:/jmx_prometheus_javaagent.jar=7654:/prometheus.yaml -jar /app/app.jar"]

EXPOSE 80/tcp