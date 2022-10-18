#!/usr/bin/env bash

VERSION=$(date +%s)
PROJECT=jac18281828/$(basename ${PWD})

docker build --no-cache=true . -t ${PROJECT}:${VERSION} && \
    docker run -v ${PWD}:/build --rm -i -t ${PROJECT}:${VERSION}
