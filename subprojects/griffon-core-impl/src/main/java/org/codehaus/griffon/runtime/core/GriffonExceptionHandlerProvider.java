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
package org.codehaus.griffon.runtime.core;

import griffon.core.ExceptionHandler;
import griffon.core.GriffonApplication;
import griffon.core.GriffonExceptionHandler;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Andres Almiray
 * @since 2.5.0
 */
public class GriffonExceptionHandlerProvider implements Provider<ExceptionHandler> {
    @Inject
    private GriffonApplication application;

    @Override
    public ExceptionHandler get() {
        return new GriffonExceptionHandler(application);
    }
}
