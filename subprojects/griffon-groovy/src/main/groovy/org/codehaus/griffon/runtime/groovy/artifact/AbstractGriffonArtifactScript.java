/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCFunction;
import griffon.core.mvc.MVCGroup;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Andres Almiray
 */
public abstract class AbstractGriffonArtifactScript extends Script implements GriffonArtifact {
    private final Logger log;
    @Inject
    protected GriffonApplication application;
    @GuardedBy("lock")
    private GriffonClass griffonClass;
    private final Object lock = new Object[0];

    public AbstractGriffonArtifactScript() {
        log = LoggerFactory.getLogger("griffon.app." + getArtifactType() + "." + getClass().getName());
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    @Override
    public GriffonClass getGriffonClass() {
        synchronized (lock) {
            if (griffonClass == null) {
                griffonClass = application.getArtifactManager().findGriffonClass(getClass());
            }
            //noinspection ConstantConditions
            return griffonClass;
        }
    }

    @Nonnull
    @Override
    public Logger getLog() {
        return log;
    }

    @Nonnull
    protected abstract String getArtifactType();

    public boolean isUIThread() {
        return application.getUIThreadManager().isUIThread();
    }

    @Nonnull
    public <R> Future<R> runFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        return application.getUIThreadManager().runFuture(executorService, callable);
    }

    @Nonnull
    public <R> Future<R> runFuture(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().runFuture(callable);
    }

    public void runInsideUISync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().runInsideUISync(runnable);
    }

    public void runOutsideUI(@Nonnull Runnable runnable) {
        application.getUIThreadManager().runOutsideUI(runnable);
    }

    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().runInsideUIAsync(runnable);
    }

    @Nonnull
    public ClassLoader classloader() {
        return application.getResourceHandler().classloader();
    }

    @Nullable
    public URL getResourceAsURL(@Nonnull String name) {
        return application.getResourceHandler().getResourceAsURL(name);
    }

    @Nullable
    public List<URL> getResources(@Nonnull String name) {
        return application.getResourceHandler().getResources(name);
    }

    @Nullable
    public InputStream getResourceAsStream(@Nonnull String name) {
        return application.getResourceHandler().getResourceAsStream(name);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVC(mvcType);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, handler);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(args, mvcType, mvcId);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, mvcId, handler);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType, mvcId);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId, args);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVC(args, mvcType);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, handler);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(mvcType);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, args);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, args, handler);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, args);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId, args);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, args, handler);
    }

    public void destroyMVCGroup(@Nonnull String mvcId) {
        application.getMvcGroupManager().destroyMVCGroup(mvcId);
    }
}
