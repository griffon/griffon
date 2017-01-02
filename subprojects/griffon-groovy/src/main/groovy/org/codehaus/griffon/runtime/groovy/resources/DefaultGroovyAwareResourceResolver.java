/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.resources;

import griffon.core.resources.GroovyAwareResourceResolver;
import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverDecorator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonClassUtils.requireNonEmpty;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class DefaultGroovyAwareResourceResolver extends ResourceResolverDecorator implements GroovyAwareResourceResolver {
    public DefaultGroovyAwareResourceResolver(@Nonnull ResourceResolver delegate) {
        super(delegate);
    }

    @Nonnull
    @Override
    public Object getAt(@Nonnull String key) throws NoSuchResourceException {
        return resolveResource(key);
    }

    @Nonnull
    @Override
    public Object getAt(@Nonnull List<?> keyAndArgs) throws NoSuchResourceException {
        requireNonEmpty(keyAndArgs, "Argument 'keyAndArgs' must not be null");
        String key = String.valueOf(keyAndArgs.get(0));
        List<Object> args = new ArrayList<>();
        if (keyAndArgs.size() > 1) {
            args.addAll(keyAndArgs.subList(1, keyAndArgs.size()));
        }
        return resolveResource(key, args);
    }
}
