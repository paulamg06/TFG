#!/bin/bash

# Directorios de destino y de origen
DIR_NAME="$HOME/TFG/cbomkit/cbomkit-override/lib/main"
LOCAL_M2="$HOME/.m2/repository/com/ibm"

# Nombres de los jars a copiar
MODULES=("common" "engine" "enricher" "java" "mapper" "output" "python" "rules" "sonar-cryptography-plugin")

echo "Copiando JARs versión 1.4.5 en la carpeta $DIR_NAME"

for module in "${MODULES[@]}"; do
	JAR_NAME="${module}-1.4.5.jar"
	LOCAL_JAR_PATH="${LOCAL_M2}/${module}/1.4.5/${JAR_NAME}"
	echo "Copiando $LOCAL_JAR_PATH a la carpeta $DIR_NAME..."
	cp "$LOCAL_JAR_PATH" "$DIR_NAME/com.ibm.$JAR_NAME"
	echo "Reemplazado: $JAR_NAME"
done

echo "Todos los JARs fueron copiados."