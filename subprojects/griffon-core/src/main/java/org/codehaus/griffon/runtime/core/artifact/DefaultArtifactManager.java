/*
 * Copyright 2009-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.ApplicationClassLoader;
import griffon.core.artifact.ArtifactInfo;
import griffon.core.artifact.GriffonArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.runtime.core.GriffonApplicationSupport.loadClass;

/**
 * Default implementation of {@code ArtifactManager}.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public class DefaultArtifactManager extends AbstractArtifactManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultArtifactManager.class);

    private final ApplicationClassLoader applicationClassLoader;

    @Inject
    public DefaultArtifactManager(@Nonnull ApplicationClassLoader applicationClassLoader) {
        this.applicationClassLoader = requireNonNull(applicationClassLoader, "Argument 'applicationClassLoader' cannot be null");
    }

    @Nonnull
    protected Map<String, List<ArtifactInfo>> doLoadArtifactMetadata() {
        Map<String, List<ArtifactInfo>> artifacts = new LinkedHashMap<>();
        try {
            Enumeration<URL> urls = getArtifactResourceLocations();
            while (urls.hasMoreElements()) {
                processURL(urls.nextElement(), artifacts);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return artifacts;
    }

    protected Enumeration<URL> getArtifactResourceLocations() throws IOException {
        return applicationClassLoader.get().getResources("META-INF/griffon/artifacts.properties");
    }

    @SuppressWarnings("unchecked")
    private void processURL(URL url, Map<String, List<ArtifactInfo>> artifacts) {
        Properties p = new Properties();
        try {
            p.load(url.openStream());
        } catch (IOException e) {
            return;
        }

        LOG.debug("Loading artifact definitions from {}", url);

        for (Object key : p.keySet()) {
            String type = key.toString();
            String classes = (String) p.get(type);
            if (classes.startsWith("'") && classes.endsWith("'")) {
                classes = classes.substring(1, classes.length() - 1);
            }
            String[] classNames = classes.split(",");
            LOG.debug("Artifacts of type '{}' = {}", type, classNames.length);


            List<ArtifactInfo> list = artifacts.get(type);
            if (list == null) {
                list = new ArrayList<>();
                artifacts.put(type, list);
            }

            for (String className : classNames) {
                try {
                    Class<? extends GriffonArtifact> clazz = (Class<? extends GriffonArtifact>) loadClass(className, applicationClassLoader.get());
                    if (Modifier.isAbstract(clazz.getModifiers())) continue;
                    ArtifactInfo<? extends GriffonArtifact> info = new ArtifactInfo<>(clazz, type);
                    if (!list.contains(info)) list.add(info);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }
}
