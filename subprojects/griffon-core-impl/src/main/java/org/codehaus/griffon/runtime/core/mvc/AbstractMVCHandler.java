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
package org.codehaus.griffon.runtime.core.mvc;

import griffon.annotations.core.Nonnull;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCConsumer;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConsumer;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.mvc.MVCHandler;
import griffon.core.mvc.TypedMVCGroup;
import griffon.core.mvc.TypedMVCGroupConsumer;

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

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return mvcGroupManager.createMVCGroup(mvcType);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVCGroup(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return mvcGroupManager.createMVCGroup(args, mvcType);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVCGroup(mvcType, args);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVCGroup(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVCGroup(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType) {
        return mvcGroupManager.createMVCGroup(mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVCGroup(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return mvcGroupManager.createMVCGroup(args, mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVCGroup(mvcType, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVCGroup(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVCGroup(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return mvcGroupManager.createMVC(mvcType);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return mvcGroupManager.createMVC(args, mvcType);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVC(mvcType, args);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVC(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVC(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVC(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType) {
        return mvcGroupManager.createMVC(mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return mvcGroupManager.createMVC(args, mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVC(mvcType, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVC(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return mvcGroupManager.createMVC(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return mvcGroupManager.createMVC(mvcType, mvcId, args);
    }

    @Override
    public void destroyMVCGroup(@Nonnull String mvcId) {
        mvcGroupManager.destroyMVCGroup(mvcId);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, mvcId, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(args, mvcType, mvcId, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(args, mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(args, mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(mvcType, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        mvcGroupManager.withMVC(args, mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        mvcGroupManager.withMVCGroup(mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        mvcGroupManager.withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        mvcGroupManager.withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        mvcGroupManager.withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        mvcGroupManager.withMVCGroup(mvcType, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        mvcGroupManager.withMVCGroup(args, mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        mvcGroupManager.withMVCGroup(mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        mvcGroupManager.withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        mvcGroupManager.withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        mvcGroupManager.withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        mvcGroupManager.withMVCGroup(mvcType, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        mvcGroupManager.withMVCGroup(args, mvcType, handler);
    }
}
