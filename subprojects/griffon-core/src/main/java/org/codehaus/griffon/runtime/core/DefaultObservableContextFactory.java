/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.Context;
import griffon.core.ContextFactory;

import javax.application.converter.ConverterRegistry;
import javax.inject.Inject;

/**
 * @author Andres Almiray
 * @since 2.5.0
 */
public class DefaultObservableContextFactory implements ContextFactory {
    @Inject
    private ConverterRegistry converterRegistry;

    @Nonnull
    @Override
    public Context create(@Nullable Context parentContext) {
        return new DefaultObservableContext(converterRegistry, parentContext);
    }
}
