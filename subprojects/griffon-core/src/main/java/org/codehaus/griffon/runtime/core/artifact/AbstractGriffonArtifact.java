/*
 * Copyright 2010-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.*;
import griffon.core.mvc.MVCCallable;
import griffon.core.mvc.MVCGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the GriffonArtifact interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonArtifact implements GriffonArtifact {
    private final Logger log;
    private final GriffonApplication application;
    @GuardedBy("lock")
    private GriffonClass griffonClass;
    private final Object lock = new Object[0];

    public AbstractGriffonArtifact(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Arguments 'application' cannot be null");
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
    public <R> Future<R> runFuture(@Nullable ExecutorService executorService, @Nonnull Callable<R> closure) {
        return application.getUIThreadManager().runFuture(executorService, closure);
    }

    @Nonnull
    public <R> Future<R> runFuture(@Nonnull Callable<R> closure) {
        return application.getUIThreadManager().runFuture(closure);
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
    public MVCGroup buildMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().buildMVCGroup(args, mvcType);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(mvcType);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcName, @Nonnull MVCCallable<M, V, C> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcName, handler);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcName) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcName);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcName) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType, mvcName);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcName, @Nonnull MVCCallable<M, V, C> handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, mvcName, handler);
    }

    @Nonnull
    public MVCGroup buildMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcName) {
        return application.getMvcGroupManager().buildMVCGroup(args, mvcType, mvcName);
    }

    @Nonnull
    public MVCGroup buildMVCGroup(@Nonnull String mvcType, @Nonnull String mvcName, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCCallable<M, V, C> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, handler);
    }

    @Nonnull
    public MVCGroup buildMVCGroup(@Nonnull String mvcType) {
        return application.getMvcGroupManager().buildMVCGroup(mvcType);
    }

    @Nonnull
    public MVCGroup buildMVCGroup(@Nonnull String mvcType, @Nonnull String mvcName) {
        return application.getMvcGroupManager().buildMVCGroup(mvcType, mvcName);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, args);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCCallable<M, V, C> handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCCallable<M, V, C> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, args, handler);
    }

    @Nonnull
    public MVCGroup buildMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().buildMVCGroup(mvcType, args);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcName, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcName, args);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcName, @Nonnull Map<String, Object> args, @Nonnull MVCCallable<M, V, C> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public void destroyMVCGroup(@Nonnull String mvcName) {
        application.getMvcGroupManager().destroyMVCGroup(mvcName);
    }
}
