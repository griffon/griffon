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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCFunction;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupFunction;
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
 * Base implementation of the GriffonArtifact interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonArtifact implements GriffonArtifact {
    private final Logger log;
    @GuardedBy("lock")
    private GriffonClass griffonClass;
    private final Object lock = new Object[0];

    @Inject
    protected GriffonApplication application;

    public AbstractGriffonArtifact() {
        log = LoggerFactory.getLogger("griffon.app." + getArtifactType() + "." + getClass().getName());
    }

    /**
     * Creates a new instance of this class.
     *
     * @param application the GriffonApplication that holds this artifact.
     * @deprecated Griffon prefers field injection over constructor injector for artifacts as of 2.1.0
     */
    @Inject
    @Deprecated
    public AbstractGriffonArtifact(@Nonnull GriffonApplication application) {
        this();
        this.application = application;
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public GriffonClass getGriffonClass() {
        synchronized (lock) {
            if (griffonClass == null) {
                griffonClass = application.getArtifactManager().findGriffonClass(getClass());
            }
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

    @Override
    public boolean isUIThread() {
        return application.getUIThreadManager().isUIThread();
    }

    @Nonnull
    @Override
    public <R> Future<R> runFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        return application.getUIThreadManager().runFuture(executorService, callable);
    }

    @Nonnull
    @Override
    public <R> Future<R> runFuture(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().runFuture(callable);
    }

    @Override
    public void runInsideUISync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().runInsideUISync(runnable);
    }

    @Override
    public void runOutsideUI(@Nonnull Runnable runnable) {
        application.getUIThreadManager().runOutsideUI(runnable);
    }

    @Override
    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().runInsideUIAsync(runnable);
    }

    @Nonnull
    @Override
    public ClassLoader classloader() {
        return application.getResourceHandler().classloader();
    }

    @Nullable
    @Override
    public URL getResourceAsURL(@Nonnull String name) {
        return application.getResourceHandler().getResourceAsURL(name);
    }

    @Nullable
    @Override
    public List<URL> getResources(@Nonnull String name) {
        return application.getResourceHandler().getResources(name);
    }

    @Nullable
    @Override
    public InputStream getResourceAsStream(@Nonnull String name) {
        return application.getResourceHandler().getResourceAsStream(name);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVC(mvcType);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, handler);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(args, mvcType, mvcId);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, mvcId, handler);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType, mvcId);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVC(args, mvcType);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, handler);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(mvcType);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, args);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, args, handler);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId, args);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupFunction handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupFunction handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupFunction handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupFunction handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupFunction handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupFunction handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public void destroyMVCGroup(@Nonnull String mvcId) {
        application.getMvcGroupManager().destroyMVCGroup(mvcId);
    }
}
