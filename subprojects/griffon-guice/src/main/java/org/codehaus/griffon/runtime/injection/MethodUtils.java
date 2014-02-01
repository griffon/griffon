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
package org.codehaus.griffon.runtime.injection;

import griffon.exceptions.InstanceMethodInvocationException;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public final class MethodUtils {
    public static void invokeAnnotatedMethod(@Nonnull final Object instance, @Nonnull final Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        Class<?> klass = instance.getClass();
        while (klass != null) {
            for (Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation) &&
                    method.getParameterTypes().length == 0 &&
                    Modifier.isPublic(method.getModifiers())) {
                    methods.add(method);
                }
            }

            klass = klass.getSuperclass();
        }

        for (final Method method : methods) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    boolean wasAccessible = method.isAccessible();
                    try {
                        method.setAccessible(true);
                        return method.invoke(instance);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        throw new InstanceMethodInvocationException(instance, method.getName(), null, e);
                    } catch (InvocationTargetException e) {
                        throw new InstanceMethodInvocationException(instance, method.getName(), null, e.getTargetException());
                    } finally {
                        method.setAccessible(wasAccessible);
                    }
                }
            });
        }
    }
}
