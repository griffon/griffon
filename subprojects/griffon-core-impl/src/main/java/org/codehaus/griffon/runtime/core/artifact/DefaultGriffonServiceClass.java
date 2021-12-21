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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonServiceClass;
import griffon.core.util.GriffonClassUtils;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultGriffonServiceClass extends DefaultGriffonClass implements GriffonServiceClass {
    protected final Set<String> serviceCache = new TreeSet<>();

    public DefaultGriffonServiceClass(@Nonnull GriffonApplication application, @Nonnull Class<?> clazz) {
        super(application, clazz, TYPE, TRAILING);
    }

    @Override
    public void resetCaches() {
        super.resetCaches();
        serviceCache.clear();
    }

    @Nonnull
    public String[] getServiceNames() {
        if (serviceCache.isEmpty()) {
            for (Method method : getClazz().getMethods()) {
                String methodName = method.getName();
                if (!serviceCache.contains(methodName) &&
                    GriffonClassUtils.isPlainMethod(method) &&
                    !GriffonClassUtils.isEventHandler(method)) {
                    serviceCache.add(methodName);
                }
            }
        }

        return serviceCache.toArray(new String[serviceCache.size()]);
    }
}
