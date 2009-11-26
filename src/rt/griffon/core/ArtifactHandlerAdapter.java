/*
 * Copyright 2009 the original author or authors.
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

package griffon.core;

import griffon.util.IGriffonApplication;

/**
 * Base implementation of the ArtifactHandler interface.
 *
 * @author Andres Almiray (aalmiray)
 */
public abstract class ArtifactHandlerAdapter implements ArtifactHandler {
    private final String type;
    private ArtifactInfo[] artifactInfos = new ArtifactInfo[0];
    private IGriffonApplication app;

    public ArtifactHandlerAdapter(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Returns true if the target Class is a class artifact
     * handled by this object.<p>
     * This implementation performs an equality check on class.name
     */
    public boolean isArtifact(Class klass) {
        for(ArtifactInfo artifactInfo: artifactInfos) {
            if(artifactInfo.getKlass().getName().equals(klass.getName())) return true;
        }
        return false;
    }

    public void initialize(ArtifactInfo[] artifacts) {
        artifactInfos = new ArtifactInfo[artifacts.length];
        System.arraycopy(artifacts, 0, artifactInfos, 0, artifacts.length);
    }

    public ArtifactInfo[] getArtifacts() {
        return artifactInfos;
    }

    public ArtifactInfo findArtifact(String name) {
        String simpleName = null;
        if(name.length() == 1) {
            simpleName = name.toLowerCase();
        } else {
            simpleName = name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        simpleName += type.substring(0, 1).toUpperCase() + type.substring(1);
        for(ArtifactInfo artifactInfo : artifactInfos) {
            if(artifactInfo.getSimpleName().equals(simpleName)) return artifactInfo;
        }
        return null;
    }

    public void setApp(IGriffonApplication app) {
        this.app = app;
    }

    public IGriffonApplication getApp() {
        return app;
    }
}
