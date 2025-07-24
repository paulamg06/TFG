#!/bin/bash

cd $HOME/TFG/sonar-cryptography

mvn clean install

echo "Plugin publicado en Maven local"

cd $HOME/TFG/cbomkit

chmod +x copy_jars.sh

./copy_jars.sh

echo "Plugin procesado"
