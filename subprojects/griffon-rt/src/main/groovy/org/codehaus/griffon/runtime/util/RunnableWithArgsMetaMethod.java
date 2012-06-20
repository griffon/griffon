/*
 * Copyright 2009-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.util;

import griffon.util.RunnableWithArgs;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;

import java.lang.reflect.Modifier;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class RunnableWithArgsMetaMethod extends MetaMethod {
    private final RunnableWithArgs runnable;
    private final String name;
    private final Class declaringClass;

    public RunnableWithArgsMetaMethod(String name, Class declaringClass, RunnableWithArgs runnable, Class[] parameterTypes) {
        super(parameterTypes);
        this.runnable = runnable;
        this.name = name;
        this.declaringClass = declaringClass;
    }

    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    public String getName() {
        return name;
    }

    public Class getReturnType() {
        return Void.TYPE;
    }

    public CachedClass getDeclaringClass() {
        return ReflectionCache.getCachedClass(declaringClass);
    }

    public Object invoke(Object object, Object[] arguments) {
        arguments = coerceArgumentsToClasses(arguments);
        runnable.setArgs(arguments);
        runnable.run();
        return null;
    }
}