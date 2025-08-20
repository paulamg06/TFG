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

import java.util.List;

// Fichero que almacena la lista de assets excluidos para omitirlos
// en el análisis.
// - Paula Morales García

public final class ExcludedAssetsList {
    private static List<String> excludedAssets = List.of(); // Lista de assets a excluir

    private ExcludedAssetsList() {
        // Evita instanciación
    }

    public static void setExcludedAssets(List<String> excludedAssetsRequest) {
        excludedAssets = excludedAssetsRequest;
    }

    public static List<String> getExcludedAssets() {
        return excludedAssets;
    }

    // Método para vaciar la lista de assets excluidos
    public static void clearExcludedAssets() {
        excludedAssets = List.of();
    }

    // Método para verificar si un asset está excluido
    public static boolean isAssetExcluded(String asset) {

        // Si está vacía, no hace falta hacer nada
        if (excludedAssets.isEmpty() || excludedAssets == null || excludedAssets.size() == 0) {
            return false;
        }

        return excludedAssets.stream()
                .anyMatch(
                        excludedAsset -> asset.toUpperCase().contains(excludedAsset.toUpperCase()));
    }
}
