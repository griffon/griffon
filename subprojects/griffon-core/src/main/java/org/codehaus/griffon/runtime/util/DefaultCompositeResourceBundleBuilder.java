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
package org.codehaus.griffon.runtime.util;

import griffon.core.injection.Injector;
import griffon.util.ResourceBundleLoader;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.AnnotationUtils.sortByDependencies;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultCompositeResourceBundleBuilder extends AbstractCompositeResourceBundleBuilder {
    protected static final String ERROR_INJECTOR_NULL = "Argument 'injector' must not be null";

    private final Provider<Injector> injector;
    private final Map<String, ResourceBundleLoader> loaders = new ConcurrentHashMap<>();

    @Inject
    public DefaultCompositeResourceBundleBuilder(@Nonnull Provider<Injector> injector) {
        this.injector = requireNonNull(injector, ERROR_INJECTOR_NULL);
    }

    protected void initialize() {
        if (loaders.isEmpty()) {
            Collection<ResourceBundleLoader> instances = injector.get().getInstances(ResourceBundleLoader.class);
            loaders.putAll(sortByDependencies(instances, "", "resource bundle loader"));
        }
    }

    @Nonnull
    protected Collection<ResourceBundle> loadBundlesFor(@Nonnull String basename) {
        List<ResourceBundle> bundles = new ArrayList<>();
        for (Map.Entry<String, ResourceBundleLoader> e : loaders.entrySet()) {
            Collection<ResourceBundle> loaded = e.getValue().load(basename);
            if (!loaded.isEmpty()) {
                bundles.addAll(loaded);
            }
        }

        return bundles;
    }
}
