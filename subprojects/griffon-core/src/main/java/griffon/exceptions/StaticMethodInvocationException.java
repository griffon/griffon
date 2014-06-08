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
package griffon.exceptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class StaticMethodInvocationException extends MethodInvocationException {
    private static final long serialVersionUID = 7806081622952446099L;

    public StaticMethodInvocationException(@Nonnull Class<?> klass, @Nonnull String methodName, @Nullable Object[] args) {
        super(formatArguments(klass, methodName, args));
    }

    public StaticMethodInvocationException(@Nonnull Class<?> klass, @Nonnull String methodName, @Nullable Object[] args, @Nonnull Throwable cause) {
        super(formatArguments(klass, methodName, args), cause);
    }

    @Nonnull
    private static String formatArguments(@Nonnull Class<?> klass, @Nonnull String methodName, @Nullable Object[] args) {
        checkNonNull(klass, "class");
        checkNonBlank(methodName, "methodName");
        StringBuilder b = new StringBuilder("An error occurred while invoking static method ")
            .append(klass.getName())
            .append(".").append(methodName).append("(");

        boolean first = true;
        for (Class<?> type : convertToTypeArray(args)) {
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
