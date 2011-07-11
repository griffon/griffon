/*
 * Copyright 2010-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.*;
import griffon.util.ApplicationHolder;
import griffon.util.GriffonExceptionHandler;
import groovy.lang.Closure;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of the MVCHandler interface.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public abstract class AbstractMCVHandler implements MVCHandler {
    protected GriffonApplication getApp() {
        return ApplicationHolder.getApplication();
    }

    public Map<String, Object> buildMVCGroup(String mvcType) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), Collections.emptyMap(), mvcType, mvcType);
    }

    public Map<String, Object> buildMVCGroup(String mvcType, String mvcName) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), Collections.emptyMap(), mvcType, mvcName);
    }

    public Map<String, Object> buildMVCGroup(Map<String, Object> args, String mvcType) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), args, mvcType, mvcType);
    }

    public Map<String, Object> buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), args, mvcType, mvcName);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcName);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType, mvcName);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType, mvcName);
    }

    public void destroyMVCGroup(String mvcName) {
        GriffonApplicationHelper.destroyMVCGroup(getApp(), mvcName);
    }

    public void withMVCGroup(String mvcType, Closure handler) {
        withMVCGroup(mvcType, mvcType, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Closure handler) {
        withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, Map<String, Object> args, Closure handler) {
        withMVCGroup(mvcType, mvcType, args, handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler) {
        try {
            List<?> group = createMVCGroup(mvcType, mvcName, args);
            handler.call(group.toArray(new Object[3]));
        } finally {
            try {
                destroyMVCGroup(mvcName);
            } catch (Exception x) {
                if (getApp().getLog().isWarnEnabled()) {
                    getApp().getLog().warn("Could not destroy group [" + mvcName + "] of type " + mvcType, GriffonExceptionHandler.sanitize(x));
                }
            }
        }
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, MVCClosure<M, V, C> handler) {
        withMVCGroup(mvcType, mvcType, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        withMVCGroup(mvcType, mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        try {
            List<? extends GriffonMvcArtifact> group = createMVCGroup(mvcType, mvcName, args);
            handler.call((M) group.get(0), (V) group.get(1), (C) group.get(2));
        } finally {
            try {
                destroyMVCGroup(mvcName);
            } catch (Exception x) {
                if (getApp().getLog().isWarnEnabled()) {
                    getApp().getLog().warn("Could not destroy group [" + mvcName + "] of type " + mvcType, GriffonExceptionHandler.sanitize(x));
                }
            }
        }
    }
}
