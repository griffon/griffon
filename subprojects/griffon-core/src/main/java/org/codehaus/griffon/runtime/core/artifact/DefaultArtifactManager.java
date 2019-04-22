/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.Typed;
import griffon.core.ApplicationClassLoader;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.GriffonArtifact;
import org.kordamp.jipsy.util.TypeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@code ArtifactManager}.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultArtifactManager extends AbstractArtifactManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultArtifactManager.class);

    private final ApplicationClassLoader applicationClassLoader;

    @Inject
    public DefaultArtifactManager(@Nonnull ApplicationClassLoader applicationClassLoader) {
        this.applicationClassLoader = requireNonNull(applicationClassLoader, "Argument 'applicationClassLoader' must not be null");
    }

    @Nonnull
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Map<String, List<Class<? extends GriffonArtifact>>> doLoadArtifactMetadata() {
        final Map<String, List<Class<? extends GriffonArtifact>>> artifacts = new LinkedHashMap<>();

        for (Map.Entry<String, ArtifactHandler> e : getArtifactHandlers().entrySet()) {
            final String artifactType = e.getKey();
            ArtifactHandler<?> artifactHandler = e.getValue();
            Class<?> klass = artifactHandler.getClass().getAnnotation(Typed.class).value();
            TypeLoader.load(applicationClassLoader.get(), "META-INF/services/", klass, (classLoader, type, line) -> {
                List<Class<? extends GriffonArtifact>> list = artifacts.computeIfAbsent(artifactType, k -> new ArrayList<>());

                try {
                    String className = line.trim();
                    Class<? extends GriffonArtifact> clazz = (Class<? extends GriffonArtifact>) applicationClassLoader.get().loadClass(className);
                    // if (Modifier.isAbstract(clazz.getModifiers())) return;
                    list.add(clazz);
                } catch (ClassNotFoundException e1) {
                    throw new IllegalArgumentException(e1);
                }
            });
        }

        for (Map.Entry<String, List<Class<? extends GriffonArtifact>>> e : artifacts.entrySet()) {
            LOG.debug("Artifacts of type '{}' = {}", e.getKey(), e.getValue().size());
        }

        return artifacts;
    }
}
