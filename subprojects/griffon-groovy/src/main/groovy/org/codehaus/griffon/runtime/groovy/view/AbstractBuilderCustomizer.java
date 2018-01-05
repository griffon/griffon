/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.groovy.view;

import griffon.util.BuilderCustomizer;
import groovy.lang.Closure;
import groovy.util.Factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public class AbstractBuilderCustomizer implements BuilderCustomizer {
    private final Map<String, Object> variables = new LinkedHashMap<>();
    private final Map<String, Factory> factories = new LinkedHashMap<>();
    private final Map<String, Closure> methods = new LinkedHashMap<>();
    private final Map<String, Closure[]> props = new LinkedHashMap<>();
    private final List<Closure> attributeDelegates = new ArrayList<>();
    private final List<Closure> preInstantiateDelegates = new ArrayList<>();
    private final List<Closure> postInstantiateDelegates = new ArrayList<>();
    private final List<Closure> postNodeCompletionDelegates = new ArrayList<>();
    private final List<Closure> disposalClosures = new ArrayList<>();
    private Closure methodMissingDelegate;
    private Closure propertyMissingDelegate;

    @Nonnull
    public Map<String, Object> getVariables() {
        return unmodifiableMap(variables);
    }

    public void setVariables(@Nonnull Map<String, Object> variables) {
        requireNonNull(variables, "Argument 'variables' must not be null");
        this.variables.clear();
        this.variables.putAll(variables);
    }

    @Nonnull
    public Map<String, Factory> getFactories() {
        return unmodifiableMap(factories);
    }

    public void setFactories(@Nonnull Map<String, Factory> factories) {
        requireNonNull(factories, "Argument 'factories' must not be null");
        this.factories.clear();
        this.factories.putAll(factories);
    }

    @Nonnull
    public Map<String, Closure> getMethods() {
        return unmodifiableMap(methods);
    }

    public void setMethods(@Nonnull Map<String, Closure> methods) {
        requireNonNull(methods, "Argument 'methods' must not be null");
        this.methods.clear();
        this.methods.putAll(methods);
    }

    @Nonnull
    public Map<String, Closure[]> getProps() {
        return unmodifiableMap(props);
    }

    public void setProps(@Nonnull Map<String, Closure[]> props) {
        requireNonNull(props, "Argument 'props' must not be null");
        this.props.clear();
        this.props.putAll(props);
    }

    @Nonnull
    public List<Closure> getAttributeDelegates() {
        return unmodifiableList(attributeDelegates);
    }

    public void setAttributeDelegates(@Nonnull List<Closure> attributeDelegates) {
        requireNonNull(attributeDelegates, "Argument 'attributeDelegates' must not be null");
        this.attributeDelegates.clear();
        this.attributeDelegates.addAll(attributeDelegates);
    }

    @Nonnull
    public List<Closure> getPostInstantiateDelegates() {
        return unmodifiableList(postInstantiateDelegates);
    }

    public void setPostInstantiateDelegates(@Nonnull List<Closure> postInstantiateDelegates) {
        requireNonNull(postInstantiateDelegates, "Argument 'postInstantiateDelegates' must not be null");
        this.postInstantiateDelegates.clear();
        this.postInstantiateDelegates.addAll(postInstantiateDelegates);
    }

    @Nonnull
    public List<Closure> getPostNodeCompletionDelegates() {
        return unmodifiableList(postNodeCompletionDelegates);
    }

    public void setPostNodeCompletionDelegates(@Nonnull List<Closure> postNodeCompletionDelegates) {
        requireNonNull(postNodeCompletionDelegates, "Argument 'postNodeCompletionDelegates' must not be null");
        this.postNodeCompletionDelegates.clear();
        this.postNodeCompletionDelegates.addAll(postNodeCompletionDelegates);
    }

    @Nonnull
    public List<Closure> getPreInstantiateDelegates() {
        return unmodifiableList(preInstantiateDelegates);
    }

    public void setPreInstantiateDelegates(@Nonnull List<Closure> preInstantiateDelegates) {
        requireNonNull(preInstantiateDelegates, "Argument 'preInstantiateDelegates' must not be null");
        this.preInstantiateDelegates.clear();
        this.preInstantiateDelegates.addAll(preInstantiateDelegates);
    }

    @Nonnull
    public List<Closure> getDisposalClosures() {
        return unmodifiableList(disposalClosures);
    }

    public void setDisposalClosures(@Nonnull List<Closure> disposalClosures) {
        requireNonNull(disposalClosures, "Argument 'disposalClosures' must not be null");
        this.disposalClosures.clear();
        this.disposalClosures.addAll(disposalClosures);
    }

    @Nullable
    public Closure getMethodMissingDelegate() {
        return methodMissingDelegate;
    }

    public void setMethodMissingDelegate(@Nullable Closure methodMissingDelegate) {
        this.methodMissingDelegate = methodMissingDelegate;
    }

    @Nullable
    public Closure getPropertyMissingDelegate() {
        return propertyMissingDelegate;
    }

    public void setPropertyMissingDelegate(@Nullable Closure propertyMissingDelegate) {
        this.propertyMissingDelegate = propertyMissingDelegate;
    }
}
