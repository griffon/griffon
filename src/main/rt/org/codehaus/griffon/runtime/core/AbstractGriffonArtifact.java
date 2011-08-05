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
import griffon.core.UIThreadManager;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base implementation of the GriffonArtifact interface.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public abstract class AbstractGriffonArtifact extends GroovyObjectSupport implements GriffonArtifact {
    private GriffonApplication app;
    private final Logger log;

    public static MetaClass metaClassOf(GriffonArtifact artifact) {
        if (artifact == null) return null;
        return GriffonApplicationHelper.expandoMetaClassFor(artifact.getClass());
    }

    public AbstractGriffonArtifact() {
        log = LoggerFactory.getLogger("griffon.app." + getArtifactType() + "." + getClass().getName());
    }

    protected abstract String getArtifactType();

    public GriffonApplication getApp() {
        return app;
    }

    public void setApp(GriffonApplication app) {
        this.app = app;
    }

    public Object newInstance(Class clazz, String type) {
        return GriffonApplicationHelper.newInstance(app, clazz, type);
    }

    public MetaClass getMetaClass() {
        return metaClassOf(this);
    }

    public void setMetaClass(MetaClass metaClass) {
        GroovySystem.getMetaClassRegistry().setMetaClass(getClass(), metaClass);
    }

    public GriffonClass getGriffonClass() {
        if (app == null) app = ApplicationHolder.getApplication();
        return app.getArtifactManager().findGriffonClass(getClass());
    }

    public boolean isUIThread() {
        return UIThreadManager.getInstance().isUIThread();
    }

    public void execAsync(Runnable runnable) {
        UIThreadManager.getInstance().executeAsync(runnable);
    }

    public void execSync(Runnable runnable) {
        UIThreadManager.getInstance().executeSync(runnable);
    }

    public void execOutside(Runnable runnable) {
        UIThreadManager.getInstance().executeOutside(runnable);
    }

    public Future execFuture(ExecutorService executorService, Closure closure) {
        return UIThreadManager.getInstance().executeFuture(executorService, closure);
    }

    public Future execFuture(Closure closure) {
        return UIThreadManager.getInstance().executeFuture(closure);
    }

    public Future execFuture(ExecutorService executorService, Callable callable) {
        return UIThreadManager.getInstance().executeFuture(executorService, callable);
    }

    public Future execFuture(Callable callable) {
        return UIThreadManager.getInstance().executeFuture(callable);
    }

    public Logger getLog() {
        return log;
    }

    public Map<String, Object> buildMVCGroup(String mvcType) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), mvcType, mvcType, Collections.emptyMap());
    }

    public Map<String, Object> buildMVCGroup(String mvcType, String mvcName) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), mvcType, mvcName, Collections.emptyMap());
    }

    public Map<String, Object> buildMVCGroup(Map<String, Object> args, String mvcType) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), mvcType, mvcType, args);
    }

    public Map<String, Object> buildMVCGroup(String mvcType, Map<String, Object> args) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), mvcType, mvcType, args);
    }

    public Map<String, Object> buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), mvcType, mvcName, args);
    }

    public Map<String, Object> buildMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), mvcType, mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcType, Collections.emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcType, Collections.emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcType, Collections.emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcName, Collections.emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcName, Collections.emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcName, Collections.emptyMap());
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

    public void withMVCGroup(Map<String, Object> args, String mvcType, Closure handler) {
        withMVCGroup(mvcType, mvcType, args, handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler) {
        GriffonApplicationHelper.withMVCGroup(getApp(), mvcType, mvcName, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, Closure handler) {
        GriffonApplicationHelper.withMVCGroup(getApp(), mvcType, mvcName, args, handler);
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

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, MVCClosure<M, V, C> handler) {
        withMVCGroup(mvcType, mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        GriffonApplicationHelper.withMVCGroup(getApp(), mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        GriffonApplicationHelper.withMVCGroup(getApp(), mvcType, mvcName, args, handler);
    }
}
