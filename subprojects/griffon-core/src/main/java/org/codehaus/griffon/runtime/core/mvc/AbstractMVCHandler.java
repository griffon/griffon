/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.core.mvc;

import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCFunction;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupFunction;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.mvc.MVCHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the MVCHandler interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractMVCHandler implements MVCHandler {
    private final MVCGroupManager mvcGroupManager;

    @Inject
    public AbstractMVCHandler(@Nonnull MVCGroupManager mvcGroupManager) {
        this.mvcGroupManager = requireNonNull(mvcGroupManager, "Argument 'mvcGroupManager' must not be null");
    }

    protected MVCGroupManager getMvcGroupManager() {
        return mvcGroupManager;
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return mvcGroupManager.createMVCGroup(args, mvcType);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVCGroup(args, mvcType, mvcId);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return mvcGroupManager.createMVCGroup(mvcType);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVCGroup(mvcType, args);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVCGroup(mvcType, mvcId);
    }

    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVCGroup(mvcType, mvcId, args);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return mvcGroupManager.createMVC(args, mvcType);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVC(args, mvcType, mvcId);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return mvcGroupManager.createMVC(mvcType);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVC(mvcType, args);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVC(mvcType, mvcId);
    }

    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVC(mvcType, mvcId, args);
    }

    public void destroyMVCGroup(@Nonnull String mvcId) {
        mvcGroupManager.destroyMVCGroup(mvcId);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, mvcId, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler) {
        mvcGroupManager.withMVC(args, mvcType, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler) {
        mvcGroupManager.withMVC(args, mvcType, mvcId, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, mvcId, args, handler);
    }

    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupFunction handler) {
        mvcGroupManager.withMVCGroup(mvcType, mvcId, handler);
    }

    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupFunction handler) {
        mvcGroupManager.withMVCGroup(args, mvcType, handler);
    }

    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupFunction handler) {
        mvcGroupManager.withMVCGroup(args, mvcType, mvcId, handler);
    }

    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupFunction handler) {
        mvcGroupManager.withMVCGroup(mvcType, args, handler);
    }

    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupFunction handler) {
        mvcGroupManager.withMVCGroup(mvcType, handler);
    }

    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupFunction handler) {
        mvcGroupManager.withMVCGroup(mvcType, mvcId, args, handler);
    }
}
