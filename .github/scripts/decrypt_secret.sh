mkdir -m 700 -p ${HOME}/distribution


for keyfile in distribution.key distribution_secret.key
do

    echo "Decrypt ${keyfile}"

    gpg --quiet --batch --yes --decrypt --passphrase="${GPG_KEYRING}" --output ${HOME}/distribution/${keyfile} .github/distribution/${keyfile}.gpg
    sha256sum ${HOME}/distribution/${keyfile}
done


echo 'Import keys'
gpg --import ${HOME}/distribution/distribution.key

gpg --allow-secret-key-import --import ${HOME}/distribution/distribution_secret.key


