/*
 * Copyright 2008-2014 the original author or authors.
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

import griffon.core.artifact.*;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static griffon.util.GriffonClassUtils.requireState;
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
public abstract class AbstractMVCGroup implements MVCGroup {
    protected final MVCGroupManager mvcGroupManager;
    protected final MVCGroupConfiguration configuration;
    protected final String mvcId;
    protected final Map<String, Object> members = new LinkedHashMap<>();
    private boolean alive;
    private final Object[] lock = new Object[0];

    public AbstractMVCGroup(@Nonnull MVCGroupManager mvcGroupManager, @Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members) {
        this.mvcGroupManager = requireNonNull(mvcGroupManager, "Argument 'mvcGroupManager' cannot be null");
        this.configuration = requireNonNull(configuration, "Argument 'configuration' cannot be null");
        this.mvcId = isBlank(mvcId) ? configuration.getMvcType() + "-" + UUID.randomUUID().toString() : mvcId;
        this.members.putAll(requireNonNull(members, "Argument 'members' cannot be null"));
        this.alive = true;
    }

    @Nonnull
    public MVCGroupManager getMvcGroupManager() {
        return mvcGroupManager;
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
        requireNonBlank(name, "Argument 'name' cannot be blank");
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
            mvcGroupManager.destroyMVCGroup(mvcId);
            members.clear();
            synchronized (lock) {
                alive = false;
            }
        }
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
}
