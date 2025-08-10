#!/bin/bash

echo "Compilando el frontend..."

cd $HOME/TFG/cbomkit/frontend

npm install

npm run build

echo "Lanzando el frontend en modo dev..."
npm run dev
