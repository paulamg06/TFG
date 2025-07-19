/*
 * Fichero que almacena la lista de assets excluidos para omitirlos
 * en el análisis.
 * - Paula Morales García
 */
package com.ibm.rules;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcludedAssetsList {
    private static List<String> excludedAssets = List.of(); // Lista de assets a excluir

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcludedAssetsList.class);

    private ExcludedAssetsList() {
        // Evita instanciación
    }

    public static void setExcludedAssets(List<String> excludedAssetsRequest) {
        excludedAssets = excludedAssetsRequest;
    }

    public static List<String> getExcludedAssets() {
        return excludedAssets;
    }

    // Método para verificar si un asset está excluido
    public static boolean isAssetExcluded(String asset) {
        LOGGER.info("Checking if asset is excluded: {} with list {}", asset, excludedAssets);
        if (excludedAssets.isEmpty()) {
            LOGGER.info("No excluded assets configured.");
            return false; // No hay assets excluidos configurados
        }

        return excludedAssets.stream()
                .anyMatch(
                        excludedAsset -> asset.toUpperCase().contains(excludedAsset.toUpperCase()));
    }
}
