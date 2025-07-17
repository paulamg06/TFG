/*
 * Fichero que almacena la lista de assets excluidos para omitirlos
 * en el análisis.
 * - Paula Morales García
 */
package com.ibm.rules;

import java.util.List;

public final class ExcludedAssetsConfiguration {
    private static List<String> excludedAssets = List.of(); // Lista de assets a excluir

    private ExcludedAssetsConfiguration() {
        // Evita instanciación
    }

    public static void setExcludedAssets(List<String> excludedAssetsRequest) {
        excludedAssets = excludedAssetsRequest;
    }

    public static List<String> getExcludedAssets() {
        return excludedAssets;
    }
}
