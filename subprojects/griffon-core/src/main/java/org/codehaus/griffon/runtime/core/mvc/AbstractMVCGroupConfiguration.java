/*
 * SPDX-License-Identifier: Apache-2.0
 *
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

import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code MVCGroupConfiguration} interface
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractMVCGroupConfiguration implements MVCGroupConfiguration {
    private static final String ERROR_MEMBERS_NULL = "Argument 'members' must not be null";
    private static final String ERROR_ARGS_NULL = "Argument 'args' must not be null";
    protected final Map<String, String> members = new LinkedHashMap<>();
    protected final Map<String, Object> config = new LinkedHashMap<>();
    protected final String mvcType;

    public AbstractMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config) {
        this.mvcType = requireNonBlank(mvcType, "Argument 'mvcType' must not be blank");
        this.members.putAll(requireNonNull(members, ERROR_MEMBERS_NULL));
        this.config.putAll(requireNonNull(config, "Argument 'config' must not be null"));
    }

    @Override
    public String toString() {
        return "MVCGroupConfiguration{" +
            "mvcType='" + mvcType + '\'' +
            ", members=" + members +
            ", config=" + config +
            '}';
    }

    @Nonnull
    @Override
    public String getMvcType() {
        return mvcType;
    }

    @Nonnull
    @Override
    public Map<String, String> getMembers() {
        return unmodifiableMap(members);
    }

    @Nonnull
    @Override
    public Map<String, Object> getConfig() {
        return unmodifiableMap(config);
    }

    @Nonnull
    @Override
    public MVCGroup create() {
        return create(null, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public MVCGroup create(@Nullable String mvcId) {
        return create(mvcId, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public MVCGroup create(@Nonnull Map<String, Object> args) {
        return create(null, args);
    }

    @Nonnull
    @Override
    public MVCGroup create(@Nullable String mvcId, @Nonnull Map<String, Object> args) {
        requireNonNull(args, ERROR_ARGS_NULL);
        return instantiateMVCGroup(mvcId, args);
    }

    @Nonnull
    protected abstract MVCGroup instantiateMVCGroup(@Nullable String mvcId, @Nonnull Map<String, Object> args);
}
