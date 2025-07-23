/*
 * Fichero que almacena la lista de assets excluidos para omitirlos
 * en el análisis.
 * - Paula Morales García
 */

// pmg: Añadido fichero para gestionar la lista de assets excluidos.
package com.ibm.output.util;

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

    // Método para vaciar la lista de assets excluidos
    public static void clearExcludedAssets() {
        excludedAssets = List.of();
        LOGGER.info("Excluded assets list cleared.");
    }

    // Método para verificar si un asset está excluido
    public static boolean isAssetExcluded(String asset) {
        LOGGER.info("Checking if asset is excluded: {} with list {}", asset, excludedAssets);

        // Si está vacía, no hace falta hacer nada
        if (excludedAssets.isEmpty() || excludedAssets == null || excludedAssets.size() == 0) {
            LOGGER.info("No excluded assets configured.");
            return false;
        }

        return excludedAssets.stream()
                .anyMatch(
                        excludedAsset -> asset.toUpperCase().contains(excludedAsset.toUpperCase()));
    }
}
