/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.core.Configuration;

import javax.annotation.Nonnull;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class DefaultConfigurationManager extends AbstractConfigurationManager {
    @Nonnull
    @Override
    public Configuration getConfiguration() {
        return application.getInjector().getInstance(Configuration.class);
    }

    @Nonnull
    @Override
    public Configuration getConfiguration(@Nonnull String name) {
        return application.getInjector().getInstance(Configuration.class, named(name));
    }
}
