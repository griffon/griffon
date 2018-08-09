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
package griffon.core.resources.groovy;

import griffon.annotations.core.Nonnull;
import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;

import java.util.List;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public interface GroovyAwareResourceResolver extends ResourceResolver {
    /**
     * Try to resolve the resource.
     *
     * @param key Key to lookup, such as 'sample.SampleModel.icon'
     * @return The resolved resource at the given key for the default locale
     * @throws NoSuchResourceException if no resource is found
     */
    @Nonnull
    Object getAt(@Nonnull String key) throws NoSuchResourceException;

    /**
     * Try to resolve the resource.
     *
     * @param keyAndArgs Key to lookup, such as 'sample.SampleModel.icon' and arguments
     *                   that will be filled in for params within the resource (params look like "{0}" within a
     *                   resource, but this might differ between implementations), or null if none.
     * @return The resolved resource at the given key for the default locale
     * @throws NoSuchResourceException if no resource is found
     */
    @Nonnull
    Object getAt(@Nonnull List<?> keyAndArgs) throws NoSuchResourceException;
}
