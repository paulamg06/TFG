#!/bin/bash

echo "Eliminando los volúmenes de contenedores existentes..."
make down

echo "Copiando los archivos JAR de la versión 1.4.5 a la carpeta de destino..."
cd $HOME/TFG/cbomkit
./copy_jars.sh
mvn clean package

echo "Lanzando la base de datos..."
make dev

echo "Lanzando el servidor..."
./mvnw quarkus:dev