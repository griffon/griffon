/*
 * Copyright 2010-2012 the original author or authors.
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
import groovy.lang.*;
import org.codehaus.griffon.runtime.builder.UberInterceptorMetaClass;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base implementation of the GriffonArtifact interface for Script based artifacts.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractGriffonArtifactScript extends Script implements GriffonArtifact {
    private GriffonApplication app;
    private final Logger log;
    private MetaClass myMetaClass;
    private final ResourceLocator resourceLocator = new ResourceLocator();

    public AbstractGriffonArtifactScript(String type) {
        log = LoggerFactory.getLogger("griffon.app." + type + "." + getClass().getName());
    }

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
        if (myMetaClass == null) {
            Class clazz = getClass();
            myMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
            if (!(myMetaClass instanceof ExpandoMetaClass) || !(myMetaClass instanceof UberInterceptorMetaClass)) {
                myMetaClass = new ExpandoMetaClass(clazz, true, true);
                log.debug("Upgrading MetaClass to " + myMetaClass);
                myMetaClass.initialize();
                GroovySystem.getMetaClassRegistry().setMetaClass(clazz, myMetaClass);
            }
        }
        return myMetaClass;
    }

    public void setMetaClass(MetaClass metaClass) {
        myMetaClass = metaClass;
        GroovySystem.getMetaClassRegistry().setMetaClass(getClass(), metaClass);
    }

    public GriffonClass getGriffonClass() {
        return app.getArtifactManager().findGriffonClass(getClass());
    }

    public boolean isUIThread() {
        return UIThreadManager.getInstance().isUIThread();
    }

    public void execInsideUIAsync(Runnable runnable) {
        UIThreadManager.getInstance().executeAsync(runnable);
    }

    public void execInsideUISync(Runnable runnable) {
        UIThreadManager.getInstance().executeSync(runnable);
    }

    public void execOutsideUI(Runnable runnable) {
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

    public MVCGroup buildMVCGroup(String mvcType) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, null, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, null, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, Map<String, Object> args) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, null, args);
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return getApp().getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, null, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, null, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args) {
        return getApp().getMvcGroupManager().createMVCGroup(mvcType, null, args);
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
        getApp().getMvcGroupManager().withMVCGroup(mvcType, null, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, Map<String, Object> args, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, Closure handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, null, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        getApp().getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public InputStream getResourceAsStream(String name) {
        return resourceLocator.getResourceAsStream(name);
    }

    public URL getResourceAsURL(String name) {
        return resourceLocator.getResourceAsURL(name);
    }

    public List<URL> getResources(String name) {
        return resourceLocator.getResources(name);
    }
}
