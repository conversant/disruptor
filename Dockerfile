# Stage 1: Build yamlfmt
FROM golang:1 AS go-builder
# defined from build kit
# DOCKER_BUILDKIT=1 docker build . -t ...
ARG TARGETARCH

# Install yamlfmt
WORKDIR /yamlfmt
RUN go install github.com/google/yamlfmt/cmd/yamlfmt@latest && \
    strip $(which yamlfmt) && \
    yamlfmt --version

# Stage 2: Build Java dev container    
FROM jac18281828/javadev:latest

WORKDIR /build

ENV JAVA_HOME=/usr/lib/jvm/default-java

COPY . .

CMD mvn -U clean compile package &&  java -jar /build/target/benchmarks.jar

FROM jac18281828/javadev:latest

WORKDIR /build

ENV JAVA_HOME=/usr/lib/jvm/default-java

USER java
ENV USER=java

COPY --chown=${USER}:${USER} --from=go-builder /go/bin/yamlfmt /go/bin/yamlfmt
COPY --chown=${USER}:${USER} . .
ENV PATH=${PATH}:/go/bin

# mvn -U clean source:jar compile test verify package javadoc:jar
