/*
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
package org.codehaus.griffon.runtime.injection;

import griffon.exceptions.InstanceMethodInvocationException;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Almiray
 */
public final class MethodUtils {
    /**
     * @since 2.6.0
     */
    public static boolean hasMethodAnnotatedwith(@Nonnull final Object instance, @Nonnull final Class<? extends Annotation> annotation) {
        Class<?> klass = instance.getClass();
        while (klass != null) {
            boolean found = false;
            for (Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation) &&
                    method.getParameterTypes().length == 0) {
                    return true;
                }
            }

            klass = klass.getSuperclass();
        }

        return false;
    }

    public static void invokeAnnotatedMethod(@Nonnull final Object instance, @Nonnull final Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        Class<?> klass = instance.getClass();
        while (klass != null) {
            boolean found = false;
            for (Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation) &&
                    method.getParameterTypes().length == 0) {
                    if (found) {
                        throw new InstanceMethodInvocationException(instance, method, buildCause(instance.getClass(), method, methods));
                    }
                    methods.add(method);
                    found = true;
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

    @Nonnull
    private static Throwable buildCause(@Nonnull Class<?> clazz, @Nonnull Method method, @Nonnull List<Method> methods) {
        StringBuilder b = new StringBuilder("The following methods were found annotated with @PostConstruct on ")
            .append(clazz);
        for (Method m : methods) {
            b.append("\n  ").append(m.toGenericString());
        }
        b.append("\n  ").append(method.toGenericString());
        return new IllegalStateException(b.toString());
    }
}
