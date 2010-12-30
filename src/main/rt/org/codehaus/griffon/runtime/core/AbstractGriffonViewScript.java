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

import griffon.core.GriffonApplication;
import griffon.core.GriffonClass;
import griffon.core.GriffonView;
import griffon.core.GriffonViewClass;
import griffon.util.UIThreadHelper;

import groovy.lang.MetaClass;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.Closure;
import groovy.lang.Script;
import groovy.lang.GroovySystem;
import groovy.util.FactoryBuilderSupport;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import java.util.Map;
import java.util.List;
import java.util.Collections;

import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.codehaus.griffon.runtime.builder.UberInterceptorMetaClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of the GriffonView interface for Script based views
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public abstract class AbstractGriffonViewScript extends Script implements GriffonView {
    private GriffonApplication app;
    private FactoryBuilderSupport builder;
    private final Logger log;
    private MetaClass _metaClass;
    
    public AbstractGriffonViewScript() {
        log = LoggerFactory.getLogger("griffon.app."+ GriffonViewClass.TYPE +"."+ getClass().getName());
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
        if(_metaClass == null) {
            Class clazz = getClass();
            _metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
            if(!(_metaClass instanceof ExpandoMetaClass) || !(_metaClass instanceof UberInterceptorMetaClass)) {
                _metaClass = new ExpandoMetaClass(clazz, true, true);
                log.debug("Upgrading MetaClass to "+_metaClass);   
                _metaClass.initialize();
                GroovySystem.getMetaClassRegistry().setMetaClass(clazz, _metaClass);
            }
        }
        return _metaClass;
    }
 
    public void setMetaClass(MetaClass metaClass) {
        _metaClass = metaClass;
        GroovySystem.getMetaClassRegistry().setMetaClass(getClass(), metaClass);
    }

    public GriffonClass getGriffonClass() {
        return app.getArtifactManager().findGriffonClass(getClass());
    }

    public boolean isUIThread() {
        return UIThreadHelper.getInstance().isUIThread();
    }

    public void execAsync(Runnable runnable) {
        UIThreadHelper.getInstance().executeAsync(runnable);
    }

    public void execSync(Runnable runnable) {
        UIThreadHelper.getInstance().executeSync(runnable);
    }

    public void execOutside(Runnable runnable) {
        UIThreadHelper.getInstance().executeOutside(runnable);
    }

    public Future execFuture(ExecutorService executorService, Closure closure) {
        return UIThreadHelper.getInstance().executeFuture(executorService, closure);
    }

    public Future execFuture(Closure closure) {
        return UIThreadHelper.getInstance().executeFuture(closure);
    }

    public Future execFuture(ExecutorService executorService, Callable callable) {
        return UIThreadHelper.getInstance().executeFuture(executorService, callable);
    }

    public Future execFuture(Callable callable) {
        return UIThreadHelper.getInstance().executeFuture(callable);
    }

    public void mvcGroupInit(Map<String, ?> args) {
        // empty
    }

    public void mvcGroupDestroy() {
        // empty
    }

    public Map<String, ?> buildMVCGroup(String mvcType) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), Collections.emptyMap(), mvcType, mvcType);
    }

    public Map<String, ?> buildMVCGroup(String mvcType, String mvcName) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), Collections.emptyMap(), mvcType, mvcName);
    }

    public Map<String, ?> buildMVCGroup(Map<String, ?> args, String mvcType) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), args, mvcType, mvcType);
    }

    public Map<String, ?> buildMVCGroup(Map<String, ?> args, String mvcType, String mvcName) {
        return GriffonApplicationHelper.buildMVCGroup(getApp(), args, mvcType, mvcName);
    }

    public List<?> createMVCGroup(String mvcType) {
        return (List<?>) GriffonApplicationHelper.createMVCGroup(getApp(), mvcType);
    }

    public List<?> createMVCGroup(Map<String, ?> args, String mvcType) {
        return (List<?>) GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType);
    }

    public List<?> createMVCGroup(String mvcType, Map<String, ?> args) {
        return (List<?>) GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType);
    }

    public List<?> createMVCGroup(String mvcType, String mvcName) {
        return (List<?>) GriffonApplicationHelper.createMVCGroup(getApp(), mvcType, mvcName);
    }

    public List<?> createMVCGroup(Map<String, ?> args, String mvcType, String mvcName) {
        return (List<?>) GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType, mvcName);
    }

    public List<?> createMVCGroup(String mvcType, String mvcName, Map<String, ?> args) {
        return (List<?>) GriffonApplicationHelper.createMVCGroup(getApp(), args, mvcType, mvcName);
    }

    public void destroyMVCGroup(String mvcName) {
        GriffonApplicationHelper.destroyMVCGroup(getApp(), mvcName);
    }

    public FactoryBuilderSupport getBuilder() {
        return builder;
    }

    public void setBuilder(FactoryBuilderSupport builder) {
        this.builder = builder;
    }
    
    public Logger getLog() {
        return log;
    }
}
