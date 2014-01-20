/*
 * Copyright 2010-2014 the original author or authors.
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
import java.util.List;
import java.util.Locale;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class CompositeResourceResolver extends AbstractResourceResolver {
    private final ResourceResolver[] resourceResolvers;

    public CompositeResourceResolver(@Nonnull List<ResourceResolver> resourceResolvers) {
        this(toResourceResolverArray(resourceResolvers));
    }

    public CompositeResourceResolver(@Nonnull ResourceResolver[] resourceResolvers) {
        this.resourceResolvers = requireNonNull(resourceResolvers, "Argument 'resourceResolvers' cannot be null");
    }

    private static ResourceResolver[] toResourceResolverArray(@Nonnull List<ResourceResolver> resourceResolvers) {
        requireNonNull(resourceResolvers, "Argument 'resourceResolvers' cannot be null");
        if (resourceResolvers.isEmpty()) {
            return new ResourceResolver[0];
        }
        return resourceResolvers.toArray(new ResourceResolver[resourceResolvers.size()]);
    }

    @Nonnull
    @Override
    protected Object doResolveResourceValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        for (ResourceResolver resourceResolver : resourceResolvers) {
            try {
                return resourceResolver.resolveResourceValue(key, locale);
            } catch (NoSuchResourceException nsre) {
                // ignore
            }
        }
        throw new NoSuchResourceException(key, locale);
    }
}
