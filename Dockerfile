FROM jac18281828/javadev:latest

WORKDIR /build

ENV JAVA_HOME=/usr/lib/jvm/default-java

USER java
ENV USER=java

CMD mvn -U clean source:jar compile test verify package javadoc:jar
