/*
 * Copyright 2011 the original author or authors.
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

package org.codehaus.griffon.artifacts;

import org.codehaus.griffon.artifacts.model.Artifact;
import org.codehaus.griffon.artifacts.model.Release;

import java.io.File;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public interface ArtifactRepository {
    String DEFAULT = "griffon-central";
    String REMOTE = "remote";
    String LOCAL = "local";

    String getName();

    String getType();

    List<Artifact> listArtifacts(String type);

    Artifact findArtifact(String type, String name);

    File downloadFile(String type, String name, String version, String username);

    boolean uploadRelease(Release release, String username, String password);
}
