/*
 * Copyright 2010-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.artifact.GriffonControllerClass;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class DefaultGriffonControllerClass extends DefaultGriffonClass implements GriffonControllerClass {
    protected final Set<String> actionsCache = new LinkedHashSet<>();

    public DefaultGriffonControllerClass(@Nonnull Class<?> clazz) {
        super(clazz, TYPE, TRAILING);
    }

    public void resetCaches() {
        super.resetCaches();
        actionsCache.clear();
    }

    @Nonnull
    public String[] getActionNames() {
        if (actionsCache.isEmpty()) {
            for (Method method : getClazz().getMethods()) {
                String methodName = method.getName();
                if (!actionsCache.contains(methodName) &&
                    GriffonClassUtils.isPlainMethod(method) &&
                    !GriffonClassUtils.isEventHandler(methodName) &&
                    method.getReturnType() == Void.TYPE) {
                    actionsCache.add(methodName);
                }
            }
        }

        return actionsCache.toArray(new String[actionsCache.size()]);
    }
}
