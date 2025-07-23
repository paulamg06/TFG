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

import com.ibm.common.IObserver;
import com.ibm.engine.detection.Finding;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.java.JavaScanContext;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.reorganizer.IReorganizerRule;
import com.ibm.output.util.ExcludedAssetsConfiguration;
import com.ibm.plugin.JavaAggregator;
import com.ibm.plugin.translation.JavaTranslationProcess;
import com.ibm.plugin.translation.reorganizer.JavaReorganizerRules;
import com.ibm.rules.IReportableDetectionRule;
import com.ibm.rules.issue.Issue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Tree;

public abstract class JavaBaseDetectionRule extends IssuableSubscriptionVisitor
        implements IObserver<Finding<JavaCheck, Tree, Symbol, JavaFileScannerContext>>,
                IReportableDetectionRule<Tree> {

    private final boolean isInventory;
    @Nonnull protected final JavaTranslationProcess javaTranslationProcess;
    @Nonnull protected final List<IDetectionRule<Tree>> detectionRules;

    // pmg: añadido logger para registrar las exclusiones
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaBaseDetectionRule.class);

    protected JavaBaseDetectionRule() {
        this.isInventory = false;
        this.detectionRules = JavaDetectionRules.rules();
        this.javaTranslationProcess = new JavaTranslationProcess(JavaReorganizerRules.rules());
    }

    protected JavaBaseDetectionRule(
            final boolean isInventory,
            @Nonnull List<IDetectionRule<Tree>> detectionRules,
            @Nonnull List<IReorganizerRule> reorganizerRules) {
        this.isInventory = isInventory;
        this.detectionRules = detectionRules;
        this.javaTranslationProcess = new JavaTranslationProcess(reorganizerRules);
    }

    /**
     * Returns a list of tree nodes to visit during detection.
     *
     * @return A list of tree node kinds to visit.
     */
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return List.of(Tree.Kind.METHOD_INVOCATION, Tree.Kind.NEW_CLASS, Tree.Kind.ENUM);
    }

    /**
     * Visits a tree node and applies detection rules to it.
     *
     * @param tree The tree node to visit.
     */
    @Override
    public void visitNode(@Nonnull Tree tree) {
        detectionRules.forEach(
                rule -> {
                    DetectionExecutive<JavaCheck, Tree, Symbol, JavaFileScannerContext>
                            detectionExecutive =
                                    JavaAggregator.getLanguageSupport()
                                            .createDetectionExecutive(
                                                    tree, rule, new JavaScanContext(this.context));
                    detectionExecutive.subscribe(this);
                    detectionExecutive.start();
                });
    }

    /**
     * On new finding.
     *
     * @param finding A finding containing detection store information.
     */
    @Override
    public void update(@Nonnull Finding<JavaCheck, Tree, Symbol, JavaFileScannerContext> finding) {
        final List<INode> nodes = javaTranslationProcess.initiate(finding.detectionStore());

        // pmg: añadida lógica para el filtrado de nodos
        List<INode> nodesAux = new ArrayList<>(nodes); // Lista auxiliar modificable
        final List<String> excludedAssets =
                ExcludedAssetsConfiguration.getExcludedAssets(); // Assets excluidos

        // Filtrado de nodos
        if (excludedAssets != null && !excludedAssets.isEmpty()) {
            Iterator<INode> iterator = nodesAux.iterator();

            // Iteramos por cada nodo
            while (iterator.hasNext()) {
                INode node = iterator.next();
                String nodeString = node.asString().toUpperCase();

                if (nodeString != null) {
                    boolean excludeNode =
                            excludedAssets.stream()
                                    .anyMatch(
                                            excludedAsset ->
                                                    nodeString.contains(
                                                            excludedAsset.toUpperCase()));

                    // Si el nodo pertenece a la lista de activos excluidos, lo eliminamos
                    if (excludeNode) {
                        LOGGER.info(
                                "Excluyendo nodo: {} por pertenecer a la lista de activos excluidos",
                                nodeString);
                        iterator.remove();
                    }
                }
            }
        }

        if (isInventory) {
            // pmg: guardado de nodos auxiliares que han sido filtrados
            JavaAggregator.addNodes(nodesAux);
        }
        // report
        // pmg: reporte de los nodos auxiliares que han sido filtrados
        this.report(finding.getMarkerTree(), nodesAux)
                .forEach(
                        issue ->
                                finding.detectionStore()
                                        .getScanContext()
                                        .reportIssue(this, issue.tree(), issue.message()));
    }

    @Override
    @Nonnull
    public List<Issue<Tree>> report(
            @Nonnull Tree markerTree, @Nonnull List<INode> translatedNodes) {
        // override by higher level rule, to report an issue
        return Collections.emptyList();
    }
}
