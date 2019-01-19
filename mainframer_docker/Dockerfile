FROM ubuntu:16.04

# MAINTAINER

# Install java8
RUN apt update && \
  apt install -y software-properties-common && \
  add-apt-repository -y ppa:webupd8team/java && \
  (echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections) && \
  apt update && \
  apt install -y oracle-java8-installer && \
  rm -fr /var/lib/apt/lists/* /tmp/* /var/tmp/*
ENV JAVA8_HOME /usr/lib/jvm/java-8-oracle

# Install Deps
RUN dpkg --add-architecture i386 && \
  apt update && apt install -y --force-yes expect git wget libc6-i386 \
  lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 python curl libqt5widgets5 && \
  apt clean && rm -fr /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Copy install tools
COPY tools /opt/tools
COPY gradle.properties /root/.gradle/gradle.properties

# Install Android SDK
RUN cd /opt && wget --output-document=android-sdk.tgz --quiet https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz && \
  tar xzf android-sdk.tgz && \
  rm -f android-sdk.tgz && \
  chown -R root.root android-sdk-linux && \
  /opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter platform-tools,tools" && \
  /opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter build-tools-28.0.0,build-tools-28.0.2,build-tools-28.0.3" && \
  /opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter android-21,android-27,android-28,extra-android-support" && \
  /opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter addon-google_apis_x86-google-28"

# /opt/tools/android-accept-licenses.sh "android-sdk-linux/tools/android update sdk --all --no-ui --filter extra-android-m2repository,extra-google-m2repository,extra-google-google_play_services" && \


# Setup environment
ENV JAVA8_HOME /usr/lib/jvm/java-8-oracle
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${PATH}:/opt/tools:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
RUN echo "export JAVA_HOME=$JAVA8_HOME" | cat - ~/.bashrc >> temp && mv temp ~/.bashrc && \
  echo "export JAVA8_HOME=$JAVA8_HOME" | cat - ~/.bashrc >> temp && mv temp ~/.bashrc && \
  echo "export ANDROID_HOME=$ANDROID_HOME" | cat - ~/.bashrc >> temp && mv temp ~/.bashrc

RUN which adb
RUN which android

# Create emulator
# RUN echo "no" | android create avd \
#                 --force \
#                 --device "Nexus 5" \
#                 --name test \
#                 --target android-24 \
#                 --abi armeabi-v7a \
#                 --skin WVGA800 \
#                 --sdcard 512M

# Setup ssh server
RUN apt-get update && \
  apt-get install -y openssh-server && \
  mkdir /var/run/sshd && \
  echo 'root:root' |chpasswd && \
  sed -ri 's/^PermitRootLogin\s+.*/PermitRootLogin yes/' /etc/ssh/sshd_config && \
  sed -ri 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config
EXPOSE 22
CMD    ["/usr/sbin/sshd", "-D"]

# Cleaning
RUN apt clean

# GO to workspace
RUN mkdir -p /opt/workspace
WORKDIR /opt/workspace
