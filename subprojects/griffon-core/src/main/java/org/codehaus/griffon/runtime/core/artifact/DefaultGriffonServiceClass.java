/*
 * Copyright 2008-2014 the original author or authors.
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

import griffon.core.artifact.GriffonServiceClass;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultGriffonServiceClass extends DefaultGriffonClass implements GriffonServiceClass {
    protected final Set<String> serviceCache = new TreeSet<>();

    public DefaultGriffonServiceClass(@Nonnull Class<?> clazz) {
        super(clazz, TYPE, TRAILING);
    }

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
                    !GriffonClassUtils.isEventHandler(methodName)) {
                    serviceCache.add(methodName);
                }
            }
        }

        return serviceCache.toArray(new String[serviceCache.size()]);
    }
}
