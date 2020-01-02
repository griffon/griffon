/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupFactory;
import griffon.core.mvc.MVCGroupManager;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class DefaultMVCGroupFactory implements MVCGroupFactory {
    @Inject
    protected MVCGroupManager mvcGroupManager;

    @Nonnull
    @Override
    public MVCGroup create(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members, @Nullable MVCGroup parentGroup) {
        return new DefaultMVCGroup(mvcGroupManager, configuration, mvcId, members, parentGroup);
    }
}
