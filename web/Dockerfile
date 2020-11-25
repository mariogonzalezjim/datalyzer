# Dockerfile
FROM openjdk:8


ARG MAVEN_VERSION=3.5.4
ARG USER_HOME_DIR="/root"
ARG SHA=ce50b1c91364cb77efe3776f756a6d92b76d9038b0a0782f7d53acf1e997a14d
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha256sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY settings-docker.xml /usr/share/maven/ref/
  
# Copia los archivos.
COPY . /docker

ENV DEBIAN_FRONTEND noninteractive
ENV SCALA_VERSION 2.11
ENV KAFKA_VERSION 2.0.0
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"


# Install Kafka, Zookeeper and other needed things
RUN apt-get update && \
    apt-get install -y zookeeper wget supervisor dnsutils && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean && \
    wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz && \
    tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt && \
    rm /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz

ADD scripts/start-kafka.sh /usr/bin/start-kafka.sh

#Supervisor config
ADD supervisor/zookeeper.conf  /etc/supervisor/conf.d/
ADD supervisor/kafka.conf  /etc/supervisor/conf.d/
ADD supervisor/nodejs.conf  /etc/supervisor/conf.d/

RUN echo "\n advertised.host.name=localhost" >> $KAFKA_HOME/config/server.properties
RUN echo "\n auto.create.topics.enable=true" >> $KAFKA_HOME/config/server.properties
RUN echo "\n group.initial.rebalance.delay.ms=3000" >> $KAFKA_HOME/config/server.properties



# install node
RUN apt-get install -y curl \
  && curl -sL https://deb.nodesource.com/setup_9.x | bash - \
  && apt-get install -y nodejs \
  && curl -L https://www.npmjs.com/install.sh | sh


# Instala dependencias.
WORKDIR /docker
RUN npm install


# 8080 is node 2181 is zookeeper, 9092 is kafka, 3333 is for TCP socket
EXPOSE 8080 2181 9092 3333

# Inicia la aplicación.
#CMD ["node", "app.js"]
CMD ["supervisord", "-n", "-c", "/etc/supervisor/supervisord.conf"]
