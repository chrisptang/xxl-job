FROM openjdk:8-jre-alpine
MAINTAINER chrisptang@gmail.com

ENV PARAMS=""
ENV DEPLOY_ENV="prod"
ENV DINGTALK_BOT_URL=""
ENV LOG_DIR="/data/applogs/xxl-job"
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/xxl-job-admin-*.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS -Dlog.dir=$LOG_DIR -Ddingtalk.bot.url=$DINGTALK_BOT_URL -Dapp.id=xxl-job-admin -Denv=$DEPLOY_ENV /app.jar $PARAMS"]