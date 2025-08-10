#!/bin/bash

cd $HOME/TFG/sonar-cryptography

mvn clean install

echo "Plugin publicado en Maven local"

cd $HOME/TFG/cbomkit

chmod +x copy-files.sh

./copy-files.sh

echo "Plugin procesado"
