FROM openjdk:8-jre-alpine

COPY ./*.jar /home/app.jar

RUN echo "http://mirrors.aliyun.com/alpine/v3.9/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.9/community" >> /etc/apk/repositories \
    && apk add --no-cache tzdata \
#    && apk upgrade libjpeg-turbo \
#    && apk upgrade libtasn1 \
#    && apk upgrade musl \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

EXPOSE 8077

ENTRYPOINT [ "sh", "-c", "java -jar /home/app.jar" ]
