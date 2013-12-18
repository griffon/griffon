/*
 * Copyright 2012-2013 the original author or authors.
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

import griffon.core.ApplicationClassLoader;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@link griffon.core.resources.ResourceHandler} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultResourceHandler extends AbstractResourceHandler {
    private final ApplicationClassLoader applicationClassLoader;

    @Inject
    public DefaultResourceHandler(@Nonnull ApplicationClassLoader applicationClassLoader) {
        this.applicationClassLoader = requireNonNull(applicationClassLoader, "Argument 'applicationClassLoader' cannot be null");
    }

    @Nonnull
    @Override
    public ClassLoader classloader() {
        return applicationClassLoader.get();
    }
}
