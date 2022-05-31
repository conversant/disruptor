#!/usr/bin/env bash

VERSION=$(date +%s)
PROJECT=jac18281828/$(basename ${PWD})

docker build . -t ${PROJECT}:${VERSION} && \
    docker run -v ${PWD}:/build --rm -i -t ${PROJECT}:${VERSION}
