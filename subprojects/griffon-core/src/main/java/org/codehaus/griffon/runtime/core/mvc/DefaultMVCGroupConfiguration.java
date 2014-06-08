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

import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of the {@code MVCGroupConfiguration} interface
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultMVCGroupConfiguration extends AbstractMVCGroupConfiguration {
    private final MVCGroupManager mvcGroupManager;

    @Inject
    public DefaultMVCGroupConfiguration(@Nonnull MVCGroupManager mvcGroupManager, @Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config) {
        super(mvcType, members, config);
        this.mvcGroupManager = requireNonNull(mvcGroupManager, "Argument 'mvcGroupManager' must not be null");
    }

    public MVCGroupManager getMvcGroupManager() {
        return mvcGroupManager;
    }

    @Nonnull
    @Override
    protected MVCGroup instantiateMVCGroup(@Nullable String mvcId, @Nonnull Map<String, Object> args) {
        return getMvcGroupManager().buildMVCGroup(getMvcType(), mvcId, args);
    }
}
