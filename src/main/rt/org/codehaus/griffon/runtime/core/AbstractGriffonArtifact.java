/*
 * Copyright 2010 the original author or authors.
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
import griffon.core.GriffonArtifact;
import griffon.core.GriffonClass;
import griffon.core.ArtifactManager;
import griffon.util.UIThreadHelper;

import groovy.lang.MetaClass;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of the GriffonArtifact interface.
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public abstract class AbstractGriffonArtifact extends GroovyObjectSupport implements GriffonArtifact {
    private GriffonApplication app;
    private final Logger log;
    
    public AbstractGriffonArtifact() {
        log = LoggerFactory.getLogger("griffon.app."+ getArtifactType() +"."+getClass().getName());
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
        GriffonClass griffonClass = getGriffonClass();
        return (griffonClass != null)? getGriffonClass().getMetaClass() : GriffonApplicationHelper.expandoMetaClassFor(getClass());
    }

    public GriffonClass getGriffonClass() {
        return ArtifactManager.getInstance().findGriffonClass(getClass());
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
    
    public Logger getLog() {
        return log;
    }
}
