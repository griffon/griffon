/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.runtime.core.injection;

import griffon.annotations.core.Nonnull;
import griffon.core.injection.Binding;
import griffon.core.injection.Module;

import java.util.ArrayList;
import java.util.List;

import static griffon.util.ObjectUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractModule implements Module {
    protected final List<Binding<?>> bindings = new ArrayList<>();
    protected BindingBuilder<?> currentBinding;
    protected boolean configured;

    public final void configure() {
        requireState(!configured, "Module " + this + " has already been configured");
        doConfigure();
        configured = true;
    }

    protected abstract void doConfigure();

    @Nonnull
    @Override
    public final List<Binding<?>> getBindings() {
        if (!configured) {
            configure();
        }

        if (currentBinding != null) {
            bindings.add(currentBinding.getBinding());
            currentBinding = null;
        }
        return bindings;
    }

    protected <T> AnnotatedBindingBuilder<T> bind(@Nonnull Class<T> clazz) {
        requireNonNull(clazz, "Argument 'class' must not be null");
        if (currentBinding != null) {
            bindings.add(currentBinding.getBinding());
        }

        AnnotatedBindingBuilder<T> builder = Bindings.bind(clazz);
        currentBinding = builder;
        return builder;
    }
}
