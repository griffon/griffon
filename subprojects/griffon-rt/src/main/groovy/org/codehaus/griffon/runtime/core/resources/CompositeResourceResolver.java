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

import java.util.List;
import java.util.Locale;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class CompositeResourceResolver extends AbstractResourceResolver {
    private final ResourceResolver[] resourceResolvers;

    public CompositeResourceResolver(List<ResourceResolver> resourceResolvers) {
        this(toResourceResolverArray(resourceResolvers));
    }

    public CompositeResourceResolver(ResourceResolver[] resourceResolvers) {
        this.resourceResolvers = resourceResolvers;
    }

    private static ResourceResolver[] toResourceResolverArray(List<ResourceResolver> resourceResolvers) {
        if (null == resourceResolvers || resourceResolvers.isEmpty()) {
            return new ResourceResolver[0];
        }
        return resourceResolvers.toArray(new ResourceResolver[resourceResolvers.size()]);
    }

    @Override
    protected Object doResolveResourceValue(String key, Locale locale) throws NoSuchResourceException {
        if (null == locale) locale = Locale.getDefault();
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
