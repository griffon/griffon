/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.exceptions;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.lang.reflect.Method;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class InstanceMethodInvocationException extends MethodInvocationException {
    private static final long serialVersionUID = -2325571968606780435L;
    private static final String INSTANCE = "instance";

    public InstanceMethodInvocationException(@Nonnull Object instance, @Nonnull String methodName, @Nullable Object[] args) {
        super(formatArguments(instance, methodName, args));
    }

    public InstanceMethodInvocationException(@Nonnull Object instance, @Nonnull String methodName, @Nullable Object[] args, @Nonnull Throwable cause) {
        super(formatArguments(instance, methodName, args), cause);
    }

    public InstanceMethodInvocationException(@Nonnull Object instance, @Nonnull Method method) {
        super(formatArguments(instance, method));
    }

    public InstanceMethodInvocationException(@Nonnull Object instance, @Nonnull Method method, @Nonnull Throwable cause) {
        super(formatArguments(instance, method), cause);
    }

    @Nonnull
    private static String formatArguments(@Nonnull Object instance, @Nonnull String methodName, @Nullable Object[] args) {
        checkNonNull(instance, INSTANCE);
        checkNonBlank(methodName, "methodName");
        StringBuilder b = new StringBuilder("An error occurred while invoking instance method ")
            .append(instance.getClass().getName())
            .append(".").append(methodName).append("(");

        boolean first = true;
        for (Class<?> type : convertToTypeArray(args)) {
            if (first) {
                first = false;
            } else {
                b.append(",");
            }
            if (type == null) {
                // we don't know the type as the argument is null,
                // let's fallback to plain old Object
                b.append(Object.class.getName());
            } else {
                b.append(type.getName());
            }
        }
        b.append(")");

        return b.toString();
    }

    @Nonnull
    protected static String formatArguments(@Nonnull Object instance, @Nonnull Method method) {
        checkNonNull(instance, INSTANCE);
        checkNonNull(method, "method");
        StringBuilder b = new StringBuilder("An error occurred while invoking instance method ")
            .append(instance.getClass().getName())
            .append(".").append(method.getName()).append("(");

        boolean first = true;
        for (Class<?> type : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                b.append(",");
            }
            b.append(type.getName());
        }
        b.append(")");

        return b.toString();
    }
}
