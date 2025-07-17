#!/bin/bash

cd $HOME/TFG/cbomkit

make down

echo "Lanzando la base de datos..."

make dev

mvn clean package

echo "Backend compilado correctamente"

echo "Lanzando backend..."
./mvnw quarkus:dev