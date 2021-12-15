FROM hseeberger/scala-sbt:11.0.10_1.5.0_2.13.5

WORKDIR /app
COPY . /app

#TODO: Debugging capabilities in Docker
ENV JAVA_ARGS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

RUN sbt compile
