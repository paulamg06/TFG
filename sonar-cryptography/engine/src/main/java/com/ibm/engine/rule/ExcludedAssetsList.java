/*
 * SonarQube Cryptography Plugin
 * Copyright (C) 2025 IBM
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.engine.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Fichero que almacena la lista de assets excluidos para omitirlos
// en el análisis.
// - Paula Morales García

public final class ExcludedAssetsList {
    private static List<String> excludedAssets = List.of(); // Lista de assets a excluir
    // Versión 3: incluidos campos de tipo de objeto y nombre del método
    private static String invokedObjectTypeString;
    private static String methodName;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcludedAssetsList.class);

    private ExcludedAssetsList() {
        // Evita instanciación
    }

    // Setters
    public static void setExcludedAssets(List<String> excludedAssetsRequest) {
        excludedAssets = excludedAssetsRequest;
    }

    public static void setInvokedObjectTypeStrings(List<String> invokedObjectTypeStringsRequest) {
        invokedObjectTypeString = invokedObjectTypeStringsRequest.get(0);
    }

    public static void setMethodNames(List<String> methodNamesRequest) {
        methodName = methodNamesRequest.get(0);
    }

    // Getters
    public static List<String> getExcludedAssets() {
        return excludedAssets;
    }

    // Método para vaciar la lista de assets excluidos
    public static void clearExcludedAssets() {
        excludedAssets = List.of();
        LOGGER.info("Excluded assets list cleared.");
    }

    // Método para verificar si un asset está excluido
    // Versión 3: lógica modificada de forma que la lista se encuentra guardada en el propio objeto
    public static boolean isAssetExcluded() {
        // Iteramos por cada activo de la lista
        for (String asset : excludedAssets) {
            // Si es un método, utilizamos methodNames para validar
            if (asset.endsWith("_method")) {
                // Formateamos methodName
                methodName = processMethodName();
                if (asset.contains(methodName)) {
                    LOGGER.info("Excluyendo método {}", methodName);
                    return true;
                }
            } else {
                // Validamos con el tipo de objeto
                if (asset.contains(invokedObjectTypeString)) {
                    LOGGER.info("Excluyendo el activo {}", invokedObjectTypeString);
                    return true;
                }
            }
        }

        // Si no ha habido coincidencias, devuelve false
        return false;
    }

    // Versión 3: método que se encarga de formatear el nombre del método del activo para
    // que tenga el mismo formato que el de los activos guardados (<tipo de objeto>.<nombre del
    // método>)
    private static String processMethodName() {
        List<String> auxList = new ArrayList<>(Arrays.asList(invokedObjectTypeString.split("\\.")));
        // Si es mayor que 4, omitimos el úlitmo elemento
        if (auxList.size() > 4) {
            auxList.remove(auxList.size() - 1);
        }
        // Incorporamos el nombre del método formateado
        String formattedMethodName = String.format("%s_method", methodName);
        auxList.add(formattedMethodName);

        // Mappeamos a tipo string
        return String.join(".", auxList);
    }
}
