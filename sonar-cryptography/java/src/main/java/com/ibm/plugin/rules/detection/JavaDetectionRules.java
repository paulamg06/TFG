/*
 * SonarQube Cryptography Plugin
 * Copyright (C) 2024 IBM
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
package com.ibm.plugin.rules.detection;

import com.ibm.engine.rule.IDetectionRule;
import com.ibm.plugin.rules.detection.bc.BouncyCastleDetectionRules;
import com.ibm.plugin.rules.detection.jca.JcaDetectionRules;
import com.ibm.plugin.rules.detection.ssl.SSLDetectionRules;
import com.ibm.rules.ExcludedAssetsList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.java.api.tree.Tree;

public final class JavaDetectionRules {
    private JavaDetectionRules() {
        // private
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaDetectionRules.class);

    @Nonnull
    public static List<IDetectionRule<Tree>> rules() {
        List<IDetectionRule<Tree>> streamRules =
                Stream.of(
                                JcaDetectionRules.rules().stream(),
                                BouncyCastleDetectionRules.rules().stream(),
                                SSLDetectionRules.rules().stream())
                        .flatMap(i -> i)
                        .collect(Collectors.toCollection(ArrayList::new));

        List<String> excludedAssets = ExcludedAssetsList.getExcludedAssets();

        if (!excludedAssets.isEmpty()) {
            try {
                streamRules.removeIf(
                        rule -> {
                            try {
                                LOGGER.info("Evaluating rule: {}", rule.getClass().getName());
                                // Control de nulos para bundle
                                if (rule.bundle() == null) {
                                    LOGGER.warn(
                                            "Found rule with null bundle: {}",
                                            rule.getClass().getName());
                                    return false;
                                }

                                String bundleRule = rule.bundle().toString();
                                return ExcludedAssetsList.isAssetExcluded(bundleRule);
                            } catch (Exception e) {
                                LOGGER.error("Error while evaluating rule: {}", e.getMessage(), e);
                                return false;
                            }
                        });
            } catch (Exception e) {
                LOGGER.error("Error while removing excluded assets: {}", e.getMessage(), e);
            }
        }

        return streamRules;
    }
}
