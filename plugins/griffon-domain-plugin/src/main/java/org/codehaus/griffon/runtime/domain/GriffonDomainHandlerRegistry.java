/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.domain;

import griffon.core.artifact.ArtifactManager;
import griffon.core.injection.Injector;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class GriffonDomainHandlerRegistry {
    private static Injector<?> injector;
    private static ArtifactManager artifactManager;
    private static final Map<Class<?>, GriffonDomainHandler> HANDLERS = new ConcurrentHashMap<>();

    public static void init(@Nonnull ArtifactManager artifactManager) {
        requireNonNull(artifactManager, "Argument 'artifactManager' cannot be null");
        GriffonDomainHandlerRegistry.artifactManager = artifactManager;
    }

    @Nonnull
    public static <T extends GriffonDomain> GriffonDomainHandler resolveFor(@Nonnull Class<T> clazz) {
        GriffonDomainHandler handler = HANDLERS.get(clazz);
        if (handler == null) {
            GriffonDomainClass domainClass = (GriffonDomainClass) artifactManager.findGriffonClass(clazz);
            handler = domainClass.getDomainHandler();
            HANDLERS.put(clazz, handler);
        }
        return handler;
    }

    public static void cleanup() {
        HANDLERS.clear();
        artifactManager = null;
    }
}
