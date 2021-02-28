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
import griffon.annotations.core.Nullable;
import griffon.core.Context;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonModelClass;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.artifact.GriffonViewClass;
import griffon.core.mvc.MVCConsumer;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupConsumer;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.mvc.TypedMVCGroup;
import griffon.core.mvc.TypedMVCGroupConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonClassUtils.setPropertyOrFieldValue;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code MVCGroup} interface
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractMVCGroup extends AbstractMVCHandler implements MVCGroup {
    protected final MVCGroupConfiguration configuration;
    protected final String mvcId;
    protected final Context context;
    protected final Map<String, Object> members = new LinkedHashMap<>();
    protected final Map<String, MVCGroup> children = new LinkedHashMap<>();
    private final Object[] lock = new Object[0];
    protected MVCGroup parentGroup;
    private boolean alive;
    private final List<Object> injectedInstances = new ArrayList<>();

    public AbstractMVCGroup(@Nonnull MVCGroupManager mvcGroupManager, @Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members, @Nullable MVCGroup parentGroup) {
        super(mvcGroupManager);
        this.configuration = requireNonNull(configuration, "Argument 'configuration' must not be null");
        this.mvcId = isBlank(mvcId) ? configuration.getMvcType() + "-" + UUID.randomUUID().toString() : mvcId;
        this.members.putAll(requireNonNull(members, "Argument 'members' must not be null"));
        this.alive = true;
        this.parentGroup = parentGroup;
        this.context = mvcGroupManager.newContext(parentGroup);

        for (Object o : this.members.values()) {
            if (o instanceof GriffonMvcArtifact) {
                setPropertyOrFieldValue(o, "mvcGroup", this);
            }
        }
    }

    @Nonnull
    public List<Object> getInjectedInstances() {
        return injectedInstances;
    }

    @Nonnull
    @Override
    public Context getContext() {
        return context;
    }

    @Nullable
    @Override
    public MVCGroup getParentGroup() {
        return parentGroup;
    }

    @Nonnull
    @Override
    public MVCGroupConfiguration getConfiguration() {
        return configuration;
    }

    @Nonnull
    @Override
    public String getMvcType() {
        return configuration.getMvcType();
    }

    @Nonnull
    @Override
    public String getMvcId() {
        return mvcId;
    }

    @Nullable
    @Override
    public GriffonModel getModel() {
        return (GriffonModel) getMember(GriffonModelClass.TYPE);
    }

    @Nullable
    @Override
    public GriffonView getView() {
        return (GriffonView) getMember(GriffonViewClass.TYPE);
    }

    @Nullable
    @Override
    public GriffonController getController() {
        return (GriffonController) getMember(GriffonControllerClass.TYPE);
    }

    @Nullable
    @Override
    public Object getMember(@Nonnull String name) {
        requireNonBlank(name, "Argument 'name' must not be blank");
        checkIfAlive();
        return members.get(name);
    }

    @Nonnull
    @Override
    public Map<String, Object> getMembers() {
        checkIfAlive();
        return unmodifiableMap(members);
    }

    @Override
    public void destroy() {
        if (isAlive()) {
            List<String> childrenIds = new ArrayList<>(children.keySet());
            Collections.reverse(childrenIds);
            for (String id : childrenIds) {
                getMvcGroupManager().destroyMVCGroup(id);
            }
            getMvcGroupManager().destroyMVCGroup(mvcId);
            members.clear();
            children.clear();
            if (parentGroup != null) {
                parentGroup.notifyMVCGroupDestroyed(mvcId);
            }
            parentGroup = null;
            context.destroy();
            synchronized (lock) {
                alive = false;
            }
        }
    }

    @Override
    public void notifyMVCGroupDestroyed(@Nonnull String mvcId) {
        requireNonBlank(mvcId, "Argument 'mvcId' must not be blank");
        children.remove(mvcId);
    }

    @Override
    public boolean isAlive() {
        synchronized (lock) {
            return alive;
        }
    }

    protected void checkIfAlive() {
        requireState(isAlive(), "Group " + getMvcType() + ":" + mvcId + " has been destroyed already.");
    }


    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return manageChildGroup(super.createMVCGroup(mvcType, mvcId, injectParentGroup()));
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return manageChildGroup(super.createMVCGroup(mvcType, injectParentGroup()));
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return manageChildGroup(super.createMVCGroup(injectParentGroup(args), mvcType));
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return manageChildGroup(super.createMVCGroup(injectParentGroup(args), mvcType, mvcId));
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return manageChildGroup(super.createMVCGroup(mvcType, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return manageChildGroup(super.createMVCGroup(mvcType, mvcId, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return manageTypedChildGroup(super.createMVCGroup(mvcType, mvcId, injectParentGroup()));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType) {
        return manageTypedChildGroup(super.createMVCGroup(mvcType, injectParentGroup()));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return manageTypedChildGroup(super.createMVCGroup(injectParentGroup(args), mvcType));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return manageTypedChildGroup(super.createMVCGroup(injectParentGroup(args), mvcType, mvcId));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return manageTypedChildGroup(super.createMVCGroup(mvcType, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return manageTypedChildGroup(super.createMVCGroup(mvcType, mvcId, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return manageChildGroup(super.createMVC(mvcType, mvcId, injectParentGroup()));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return manageChildGroup(super.createMVC(mvcType, injectParentGroup()));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return manageChildGroup(super.createMVC(injectParentGroup(args), mvcType));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return manageChildGroup(super.createMVC(injectParentGroup(args), mvcType, mvcId));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return manageChildGroup(super.createMVC(mvcType, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return manageChildGroup(super.createMVC(mvcType, mvcId, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return manageChildGroup(super.createMVC(mvcType, mvcId, injectParentGroup()));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType) {
        return manageChildGroup(super.createMVC(mvcType, injectParentGroup()));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return manageChildGroup(super.createMVC(injectParentGroup(args), mvcType));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return manageChildGroup(super.createMVC(injectParentGroup(args), mvcType, mvcId));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return manageChildGroup(super.createMVC(mvcType, injectParentGroup(args)));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return manageChildGroup(super.createMVC(mvcType, mvcId, injectParentGroup(args)));
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(injectParentGroup(args), mvcType, new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(injectParentGroup(args), mvcType, mvcId, new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, injectParentGroup(args), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, mvcId, injectParentGroup(args), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, injectParentGroup(), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, mvcId, injectParentGroup(), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(injectParentGroup(args), mvcType, new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(injectParentGroup(args), mvcType, mvcId, new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, injectParentGroup(args), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, mvcId, injectParentGroup(args), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, injectParentGroup(), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        super.withMVC(mvcType, mvcId, injectParentGroup(), new MVCConsumerDecorator<>(handler));
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        super.withMVCGroup(injectParentGroup(args), mvcType, new MVCGroupConsumerDecorator(handler));
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        super.withMVCGroup(injectParentGroup(args), mvcType, mvcId, new MVCGroupConsumerDecorator(handler));
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        super.withMVCGroup(mvcType, injectParentGroup(args), new MVCGroupConsumerDecorator(handler));
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        super.withMVCGroup(mvcType, mvcId, injectParentGroup(args), new MVCGroupConsumerDecorator(handler));
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        super.withMVCGroup(mvcType, injectParentGroup(), new MVCGroupConsumerDecorator(handler));
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull final MVCGroupConsumer handler) {
        super.withMVCGroup(mvcType, mvcId, injectParentGroup(), new MVCGroupConsumerDecorator(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        super.withMVCGroup(injectParentGroup(args), mvcType, new TypedMVCGroupConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        super.withMVCGroup(injectParentGroup(args), mvcType, mvcId, new TypedMVCGroupConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        super.withMVCGroup(mvcType, injectParentGroup(args), new TypedMVCGroupConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        super.withMVCGroup(mvcType, mvcId, injectParentGroup(args), new TypedMVCGroupConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        super.withMVCGroup(mvcType, injectParentGroup(), new TypedMVCGroupConsumerDecorator<>(handler));
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull final TypedMVCGroupConsumer<MVC> handler) {
        super.withMVCGroup(mvcType, mvcId, injectParentGroup(), new TypedMVCGroupConsumerDecorator<>(handler));
    }

    @Nonnull
    private MVCGroup manageChildGroup(@Nonnull MVCGroup group) {
        children.put(group.getMvcId(), group);
        return group;
    }

    @Nonnull
    private <MVC extends TypedMVCGroup> MVC manageTypedChildGroup(@Nonnull MVC group) {
        children.put(group.getMvcId(), group);
        return group;
    }

    @Nonnull
    private List<? extends GriffonMvcArtifact> manageChildGroup(@Nonnull List<? extends GriffonMvcArtifact> artifacts) {
        MVCGroup group = null;
        for (GriffonMvcArtifact artifact : artifacts) {
            if (artifact != null) {
                group = artifact.getMvcGroup();
                break;
            }
        }
        if (group != null) {
            children.put(group.getMvcId(), group);
        }
        return artifacts;
    }

    @Nonnull
    @Override
    public Map<String, MVCGroup> getChildrenGroups() {
        return unmodifiableMap(children);
    }

    @Nonnull
    private Map<String, Object> injectParentGroup() {
        return injectParentGroup(new LinkedHashMap<String, Object>());
    }

    @Nonnull
    private Map<String, Object> injectParentGroup(@Nonnull Map<String, Object> args) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("parentGroup", this);
        map.putAll(args);
        return map;
    }

    private final class MVCConsumerDecorator<M extends GriffonModel, V extends GriffonView, C extends GriffonController> implements MVCConsumer<M, V, C> {
        private final MVCConsumer<M, V, C> delegate;

        private MVCConsumerDecorator(MVCConsumer<M, V, C> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(@Nullable M model, @Nullable V view, @Nullable C controller) {
            MVCGroup group = null;
            if (model != null) { group = model.getMvcGroup(); }
            if (view != null) { group = view.getMvcGroup(); }
            if (controller != null) { group = controller.getMvcGroup(); }

            if (group != null) { children.put(group.getMvcId(), group); }
            delegate.accept(model, view, controller);
            if (group != null) { children.remove(group.getMvcId()); }
        }
    }

    private final class MVCGroupConsumerDecorator implements MVCGroupConsumer {
        private final MVCGroupConsumer delegate;

        private MVCGroupConsumerDecorator(MVCGroupConsumer delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(@Nonnull MVCGroup group) {
            children.put(group.getMvcId(), group);
            delegate.accept(group);
            children.remove(group.getMvcId());
        }
    }

    private final class TypedMVCGroupConsumerDecorator<MVC extends TypedMVCGroup> implements TypedMVCGroupConsumer<MVC> {
        private final TypedMVCGroupConsumer<MVC> delegate;

        private TypedMVCGroupConsumerDecorator(TypedMVCGroupConsumer<MVC> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(@Nonnull MVC group) {
            children.put(group.getMvcId(), group);
            delegate.accept(group);
            children.remove(group.getMvcId());
        }
    }
}
