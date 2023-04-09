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
package org.codehaus.griffon.runtime.core.resources;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.resources.ResourceHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Base implementation of the {@link ResourceHandler} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractResourceHandler implements ResourceHandler {
    @Nullable
    public InputStream getResourceAsStream(@Nonnull String name) {
        return classloader().getResourceAsStream(name);
    }

    @Nullable
    public URL getResourceAsURL(@Nonnull String name) {
        return classloader().getResource(name);
    }

    @Nullable
    public List<URL> getResources(@Nonnull String name) {
        Enumeration<URL> resources = null;
        try {
            resources = classloader().getResources(name);
        } catch (IOException e) {
            // ignore
        }

        return resources != null ? toList(resources) : Collections.emptyList();
    }

    private static <T> List<T> toList(Enumeration<T> self) {
        List<T> answer = new ArrayList<>();
        while (self.hasMoreElements()) {
            answer.add(self.nextElement());
        }
        return answer;
    }
}
