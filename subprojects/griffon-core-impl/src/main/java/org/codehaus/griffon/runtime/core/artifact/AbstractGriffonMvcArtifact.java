/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCConsumer;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConsumer;
import griffon.core.mvc.TypedMVCGroup;
import griffon.core.mvc.TypedMVCGroupConsumer;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the GriffonMvcArtifact interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonMvcArtifact extends AbstractGriffonArtifact implements GriffonMvcArtifact {
    private MVCGroup group;

    public AbstractGriffonMvcArtifact() {
    }

    @Nonnull
    @Override
    public MVCGroup getMvcGroup() {
        return group;
    }

    @MVCMember
    public void setMvcGroup(@Nonnull MVCGroup group) {
        this.group = requireNonNull(group, "Argument 'group' must not be null");
    }

    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        // empty
    }

    public void mvcGroupDestroy() {
        // empty
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return group.createMVCGroup(mvcType);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return group.createMVCGroup(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return group.createMVCGroup(args, mvcType);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return group.createMVCGroup(mvcType, args);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return group.createMVCGroup(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return group.createMVCGroup(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType) {
        return group.createMVCGroup(mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return group.createMVCGroup(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return group.createMVCGroup(args, mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return group.createMVCGroup(mvcType, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return group.createMVCGroup(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return group.createMVCGroup(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return group.createMVC(mvcType);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return group.createMVC(args, mvcType);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return group.createMVC(mvcType, args);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return group.createMVC(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return group.createMVC(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return group.createMVC(mvcType, mvcId, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType) {
        return group.createMVC(mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return group.createMVC(args, mvcType);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return group.createMVC(mvcType, args);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return group.createMVC(mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return group.createMVC(args, mvcType, mvcId);
    }

    @Override
    @Nonnull
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return group.createMVC(mvcType, mvcId, args);
    }

    @Override
    public void destroyMVCGroup(@Nonnull String mvcId) {
        group.destroyMVCGroup(mvcId);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, mvcId, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(args, mvcType, mvcId, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(args, mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(args, mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(mvcType, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        group.withMVC(args, mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        group.withMVCGroup(mvcType, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        group.withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        group.withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        group.withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        group.withMVCGroup(mvcType, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        group.withMVCGroup(args, mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        group.withMVCGroup(mvcType, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        group.withMVCGroup(mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        group.withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        group.withMVCGroup(args, mvcType, mvcId, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        group.withMVCGroup(mvcType, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        group.withMVCGroup(args, mvcType, handler);
    }
}
