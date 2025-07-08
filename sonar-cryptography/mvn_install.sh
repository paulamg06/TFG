#!/bin/bash

echo "Publicando el plugin en Maven local..."

cd $HOME/TFG/sonar-cryptography

mvn clean install -Dmaven.test.skip=true