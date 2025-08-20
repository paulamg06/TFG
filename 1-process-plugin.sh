#!/bin/bash

cd $HOME/TFG/sonar-cryptography

mvn clean install -Dmaven.test.skip=true 

echo "Plugin publicado en Maven local"

cd $HOME/TFG/cbomkit

chmod +x copy-files.sh

./copy-files.sh

echo "Plugin procesado"
