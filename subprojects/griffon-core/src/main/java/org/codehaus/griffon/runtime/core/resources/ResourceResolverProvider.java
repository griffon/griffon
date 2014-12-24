/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.core.resources;

import griffon.core.resources.ResourceResolver;
import griffon.util.CompositeResourceBundleBuilder;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ResourceResolverProvider implements Provider<ResourceResolver> {
    private final String basename;

    private CompositeResourceBundleBuilder resourceBundleBuilder;

    public ResourceResolverProvider(@Nonnull String basename) {
        this.basename = requireNonBlank(basename, "Argument 'basename' must not be blank");
    }

    @Inject
    public void setResourceBundleBuilder(@Nonnull CompositeResourceBundleBuilder resourceBundleBuilder) {
        this.resourceBundleBuilder = requireNonNull(resourceBundleBuilder, "Argument 'resourceBundleBuilder' must not be null");
    }

    @Override
    public ResourceResolver get() {
        return new DefaultResourceResolver(resourceBundleBuilder, basename);
    }
}
