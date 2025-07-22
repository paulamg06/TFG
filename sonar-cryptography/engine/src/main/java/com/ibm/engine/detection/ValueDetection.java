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
package com.ibm.engine.detection;

import com.ibm.engine.model.IValue;
import com.ibm.engine.model.factory.IValueFactory;
import com.ibm.engine.rule.DetectableParameter;
import com.ibm.engine.rule.ExcludedAssetsList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ValueDetection<O, T>(
        @Nonnull ResolvedValue<O, T> resolvedValue,
        @Nonnull DetectableParameter<T> detectableParameter,
        @Nonnull T expression,
        @Nullable T markerTree)
        implements IValueDetection<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueDetection.class);

    @Nonnull
    @Override
    public Optional<IValue<T>> toValue(
            @Nonnull IValueFactory<T> valueFactory,
            @Nullable List<String> invokedObjectTypeStringsSerializable) {
        // Iteramos por cada tipo y si hay uno que coincide, excluimos el método
        for (String type : invokedObjectTypeStringsSerializable) {
            if (ExcludedAssetsList.isAssetExcluded(type)) {
                LOGGER.info("Excluding method {} from detection", type);
                return Optional.empty();
            }
        }

        return valueFactory.apply(
                new ResolvedValue<>(
                        resolvedValue.value(), Objects.requireNonNullElse(markerTree, expression)));
    }
}
