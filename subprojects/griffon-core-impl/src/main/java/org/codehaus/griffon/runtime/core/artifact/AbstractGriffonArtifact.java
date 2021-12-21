/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.i18n.NoSuchMessageException;
import griffon.core.mvc.MVCConsumer;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConsumer;
import griffon.core.mvc.TypedMVCGroup;
import griffon.core.mvc.TypedMVCGroupConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
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
    private final Object lock = new Object[0];
    @Inject
    protected GriffonApplication application;
    // @GuardedBy("lock")
    private GriffonClass griffonClass;

    public AbstractGriffonArtifact() {
        log = LoggerFactory.getLogger("griffon.app." + getArtifactType() + "." + getTypeClass().getName());
    }

    @Override
    @Nonnull
    public Class<? extends GriffonArtifact> getTypeClass() {
        return getClass();
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
                griffonClass = application.getArtifactManager().findGriffonClass(getTypeClass());
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
    public <R> Future<R> executeFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeFuture(executorService, callable);
    }

    @Nonnull
    @Override
    public <R> Future<R> executeFuture(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeFuture(callable);
    }

    @Override
    public void executeInsideUISync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeInsideUISync(runnable);
    }

    @Override
    public void executeOutsideUI(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeOutsideUI(runnable);
    }

    @Override
    public void executeOutsideUIAsync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeOutsideUIAsync(runnable);
    }

    @Override
    public void executeInsideUIAsync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeInsideUIAsync(runnable);
    }

    @Nullable
    @Override
    public <R> R executeInsideUISync(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeInsideUISync(callable);
    }

    @Nonnull
    @Override
    public <R> CompletionStage<R> executeOutsideUIAsync(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeOutsideUIAsync(callable);
    }

    @Nonnull
    @Override
    public <R> CompletionStage<R> executeInsideUIAsync(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeInsideUIAsync(callable);
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

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(mvcType);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, args);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType) {
        return application.getMvcGroupManager().createMVCGroup(mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVCGroup(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVCGroup(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVC(mvcType);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return application.getMvcGroupManager().createMVC(args, mvcType);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, args);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType) {
        return application.getMvcGroupManager().createMVC(mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return application.getMvcGroupManager().createMVC(args, mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return application.getMvcGroupManager().createMVC(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return application.getMvcGroupManager().createMVC(mvcType, mvcId, args);
    }

    @Override
    public void destroyMVCGroup(@Nonnull String mvcId) {
        application.getMvcGroupManager().destroyMVCGroup(mvcId);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, mvcId, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(mvcType, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        application.getMvcGroupManager().withMVC(args, mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        application.getMvcGroupManager().withMVCGroup(mvcType, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        application.getMvcGroupManager().withMVCGroup(args, mvcType, handler);
    }

    /**
     * Try to resolve the message.
     *
     * @param key Key to lookup, such as 'log4j.appenders.console'
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     * @since 2.4.0
     */
    @Nonnull
    protected String msg(@Nonnull String key) throws NoSuchMessageException {
        return getApplication().getMessageSource().getMessage(key);
    }

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{0}" within a
     *             message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     * @since 2.4.0
     */
    @Nonnull
    protected String msg(@Nonnull String key, @Nonnull List<?> args) throws NoSuchMessageException {
        return getApplication().getMessageSource().getMessage(key, args);
    }

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{0}" within a
     *             message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     * @since 2.4.0
     */
    @Nonnull
    protected String msg(@Nonnull String key, @Nonnull Object[] args) throws NoSuchMessageException {
        return getApplication().getMessageSource().getMessage(key, args);
    }

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{:key}"
     *             within a message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     * @since 2.4.0
     */
    @Nonnull
    protected String msg(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchMessageException {
        return getApplication().getMessageSource().getMessage(key, args);
    }
}
