/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.resources;

import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class DefaultResourcesInjector extends AbstractResourcesInjector {
    private ResourceResolver resourceResolver;

    @Inject
    public void setResourceResolver(@Nonnull ResourceResolver resourceResolver) {
        this.resourceResolver = requireNonNull(resourceResolver, "Argument 'resourceResolver' cannot be null");
    }

    @Nullable
    protected Object resolveResource(@Nonnull String key, @Nonnull String[] args) {
        try {
            return resourceResolver.resolveResource(key, args, Locale.getDefault());
        } catch (NoSuchResourceException nsre) {
            return null;
        }
    }

    @Nullable
    protected Object resolveResource(@Nonnull String key, @Nonnull String[] args, @Nonnull String defaultValue) {
        return resourceResolver.resolveResource(key, args, Locale.getDefault(), defaultValue);
    }
}
