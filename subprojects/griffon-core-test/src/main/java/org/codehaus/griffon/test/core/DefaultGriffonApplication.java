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
package org.codehaus.griffon.test.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.CallableWithArgs;
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication;

import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultGriffonApplication extends AbstractGriffonApplication {
    public CallableWithArgs<?> containerGenerator;

    public DefaultGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public DefaultGriffonApplication(@Nonnull String[] args) {
        super(args);
    }

    @Nullable
    public CallableWithArgs<?> getContainerGenerator() {
        return containerGenerator;
    }

    public void setContainerGenerator(@Nullable CallableWithArgs<?> containerGenerator) {
        this.containerGenerator = containerGenerator;
    }

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        if (containerGenerator != null) {
            return containerGenerator.call(attributes);
        }
        return new Object();
    }
}
