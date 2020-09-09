#!/usr/bin/env bash

set -e

gpg --import .github/distribution/cairns_pub.asc

(echo 5; echo y; echo save) | gpg --command-fd 0 --no-tty --no-greeting -q --edit-key DD453D1420D17CA0102FF85C7BEF3762B55F70AD trust

mkdir -m 700 -p ${HOME}/distribution

for keyfile in distribution.asc distribution_secret.asc
do

    echo "Decrypt ${keyfile}"

    gpg --quiet --batch --yes --decrypt --passphrase="${GPG_KEYRING}" --output ${HOME}/distribution/${keyfile} .github/distribution/${keyfile}.gpg
    sha256sum ${HOME}/distribution/${keyfile}
    gpg --allow-secret-key-import --import ${HOME}/distribution/${keyfile}
    
done

echo 'Keys Available'

gpg --list-secret-keys


