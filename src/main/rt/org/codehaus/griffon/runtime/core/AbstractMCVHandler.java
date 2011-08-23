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
import groovy.lang.Closure;

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

    public MVCGroup buildMVCGroup(String mvcType) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcType, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcType, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, Map<String, Object> args) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcType, args);
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, mvcType, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, mvcType, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, mvcType, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, mvcName, args);
    }

    public void destroyMVCGroup(String mvcName) {
        getApp().getMvcGroupManager().destroyMVCGroup(mvcName);
    }

    public void withMVCGroup(String mvcType, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcType, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, Map<String, Object> args, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcType, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcType, args, handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcType, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }
}
