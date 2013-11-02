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

package griffon.exceptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class InstanceMethodInvocationException extends GriffonException {
    public InstanceMethodInvocationException(@Nonnull Object instance, @Nonnull String methodName, @Nullable Object[] args) {
        super(formatArguments(instance, methodName, args));
    }

    public InstanceMethodInvocationException(@Nonnull Object instance, @Nonnull String methodName, @Nullable Object[] args, @Nonnull Throwable cause) {
        super(formatArguments(instance, methodName, args), cause);
    }

    @Nonnull
    private static String formatArguments(@Nonnull Object instance, @Nonnull String methodName, @Nullable Object[] args) {
        checkNonNull(instance, "instance");
        checkNonBlank(methodName, "methodName");
        StringBuilder b = new StringBuilder("An error occurred while invoking instance method ")
            .append(instance.getClass().getName())
            .append(".").append(methodName).append("(");

        boolean first = true;
        for (Class type : convertToTypeArray(args)) {
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

    @Nonnull
    private static Class[] convertToTypeArray(@Nullable Object[] args) {
        if (args == null) {
            return new Class[0];
        }
        int s = args.length;
        Class[] ans = new Class[s];
        for (int i = 0; i < s; i++) {
            Object o = args[i];
            ans[i] = o != null ? o.getClass() : null;
        }
        return ans;
    }
}
