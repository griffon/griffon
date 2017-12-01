/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.controller.ControllerAction;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import static griffon.util.AnnotationUtils.isAnnotatedWith;
import static griffon.util.GriffonClassUtils.isEventHandler;
import static griffon.util.GriffonClassUtils.isPlainMethod;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultGriffonControllerClass extends DefaultGriffonClass implements GriffonControllerClass {
    protected final Set<String> actionsCache = new TreeSet<>();

    public DefaultGriffonControllerClass(@Nonnull GriffonApplication application, @Nonnull Class<?> clazz) {
        super(application, clazz, TYPE, TRAILING);
    }

    @Override
    public void resetCaches() {
        super.resetCaches();
        actionsCache.clear();
    }

    @Nonnull
    public String[] getActionNames() {
        if (actionsCache.isEmpty()) {
            for (Method method : getClazz().getMethods()) {
                String methodName = method.getName();
                if (actionsCache.contains(methodName)) {
                    continue;
                }

                if (isPlainMethod(method) &&
                    !isEventHandler(methodName) &&
                    (isAnnotatedWith(method, ControllerAction.class, true) || method.getReturnType() == Void.TYPE)) {
                    actionsCache.add(methodName);
                }
            }
        }

        return actionsCache.toArray(new String[actionsCache.size()]);
    }
}
