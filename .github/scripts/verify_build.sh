#!/usr/bin/env bash

set -e

cd target

for sigfile in *.asc
do
    gpg --verify $sigfile
done
