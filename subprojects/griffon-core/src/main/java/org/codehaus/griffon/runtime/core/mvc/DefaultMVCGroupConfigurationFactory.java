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
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupConfigurationFactory;
import griffon.core.mvc.MVCGroupManager;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class DefaultMVCGroupConfigurationFactory implements MVCGroupConfigurationFactory {
    @Inject
    private MVCGroupManager mvcGroupManager;

    @Nonnull
    @Override
    public MVCGroupConfiguration create(@Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config) {
        return new DefaultMVCGroupConfiguration(mvcGroupManager, mvcType, members, config);
    }
}
