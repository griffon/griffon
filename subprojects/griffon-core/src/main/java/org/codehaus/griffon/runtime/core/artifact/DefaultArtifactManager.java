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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.ApplicationClassLoader;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.GriffonArtifact;
import griffon.inject.Typed;
import griffon.util.ServiceLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
            ServiceLoaderUtils.load(applicationClassLoader.get(), "META-INF/griffon/", klass, new ServiceLoaderUtils.LineProcessor() {
                @Override
                public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                    List<Class<? extends GriffonArtifact>> list = artifacts.get(artifactType);
                    if (list == null) {
                        list = new ArrayList<>();
                        artifacts.put(artifactType, list);
                    }

                    try {
                        String className = line.trim();
                        Class<? extends GriffonArtifact> clazz = (Class<? extends GriffonArtifact>) applicationClassLoader.get().loadClass(className);
                        // if (Modifier.isAbstract(clazz.getModifiers())) return;
                        list.add(clazz);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }

        for (Map.Entry<String, List<Class<? extends GriffonArtifact>>> e : artifacts.entrySet()) {
            LOG.debug("Artifacts of type '{}' = {}", e.getKey(), e.getValue().size());
        }

        return artifacts;
    }
}
