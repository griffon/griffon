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
package org.codehaus.griffon.runtime.core.injection;

import griffon.core.injection.Injector;

import javax.annotation.Nonnull;
import javax.inject.Provider;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class InjectorProvider implements Provider<Injector> {
    private Injector injector;

    public void setInjector(@Nonnull Injector injector) {
        this.injector = injector;
    }

    @Override
    public Injector get() {
        return injector;
    }
}
