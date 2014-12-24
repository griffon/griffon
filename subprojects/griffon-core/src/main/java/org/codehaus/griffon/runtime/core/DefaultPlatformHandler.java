/*
 * Copyright 2008-2015 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.PlatformHandler;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Handles Linux integration.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultPlatformHandler implements PlatformHandler {
    public void handle(@Nonnull GriffonApplication application) {
        requireNonNull(application, "Argument 'application' must not be null");
    }
}
