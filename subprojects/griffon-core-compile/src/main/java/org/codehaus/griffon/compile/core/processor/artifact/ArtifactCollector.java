/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.artifact;

import org.kordamp.jipsy.processor.Initializer;
import org.kordamp.jipsy.processor.LogLocation;
import org.kordamp.jipsy.processor.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public final class ArtifactCollector {
    private final Map<String, Artifact> artifacts = new LinkedHashMap<>();
    private final Map<String, Artifact> cached = new LinkedHashMap<>();

    private final List<String> removed = new ArrayList<>();
    private final Initializer initializer;
    private final Logger logger;

    public ArtifactCollector(Initializer initializer, Logger logger) {
        this.initializer = initializer;
        this.logger = logger;
    }

    public void cache() {
        this.cached.putAll(artifacts);
    }

    public boolean isModified() {
        if (cached.size() != artifacts.size()) {
            return true;
        }

        for (Map.Entry<String, Artifact> e : cached.entrySet()) {
            if (!artifacts.containsKey(e.getKey())) {
                return true;
            }
            if (!e.getValue().equals(artifacts.get(e.getKey()))) {
                return true;
            }
        }

        return false;
    }

    public Artifact getArtifact(String artifact) {
        if (artifact == null) {
            throw new NullPointerException("artifact");
        }
        if (!artifacts.containsKey(artifact)) {
            Artifact newArtifact = new Artifact(logger, artifact);
            CharSequence initialData = initializer.initialData(artifact);
            if (initialData != null) {
                newArtifact.fromProviderNamesList(initialData.toString());
                for (String provider : removed) {
                    newArtifact.removeProvider(provider);
                }
            }
            artifacts.put(artifact, newArtifact);
        }
        return artifacts.get(artifact);
    }

    public Collection<Artifact> artifacts() {
        return Collections.unmodifiableMap(artifacts).values();
    }

    public void removeProvider(String provider) {
        if (provider == null) {
            throw new NullPointerException("provider");
        }
        logger.note(LogLocation.LOG_FILE, "Removing " + provider);
        removed.add(provider);
        for (Artifact artifact : artifacts.values()) {
            artifact.removeProvider(provider);
        }
    }

    @Override
    public String toString() {
        return artifacts.values().toString();
    }
}
