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
package griffon.plugins.domain.methods;

import org.apache.commons.lang.builder.CompareToBuilder;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author Andres Almiray
 */
public class MethodSignature implements Comparable<MethodSignature> {
    private final Class<?> returnType;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final String[] parameterClassnames;
    private final boolean isStatic;

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    public MethodSignature(@Nonnull String methodName) {
        this(false, Void.TYPE, methodName, EMPTY_CLASS_ARRAY);
    }

    public MethodSignature(@Nonnull Class<?> returnType, @Nonnull String methodName) {
        this(false, returnType, methodName, EMPTY_CLASS_ARRAY);
    }

    public MethodSignature(@Nonnull String methodName, Class<?>... parameterTypes) {
        this(false, Void.TYPE, methodName, parameterTypes);
    }

    public MethodSignature(@Nonnull Class<?> returnType, @Nonnull String methodName, Class<?>... parameterTypes) {
        this(false, returnType, methodName, parameterTypes);
    }

    public MethodSignature(boolean isStatic, @Nonnull String methodName) {
        this(isStatic, Void.TYPE, methodName, EMPTY_CLASS_ARRAY);
    }

    public MethodSignature(boolean isStatic, @Nonnull Class<?> returnType, @Nonnull String methodName) {
        this(isStatic, returnType, methodName, EMPTY_CLASS_ARRAY);
    }

    public MethodSignature(boolean isStatic, @Nonnull String methodName, Class<?>... parameterTypes) {
        this(isStatic, Void.TYPE, methodName, parameterTypes);
    }

    public MethodSignature(boolean isStatic, @Nonnull Class<?> returnType, @Nonnull String methodName, Class<?>... parameterTypes) {
        this.isStatic = isStatic;
        this.returnType = returnType;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes == EMPTY_CLASS_ARRAY ? EMPTY_CLASS_ARRAY : copyClassArray(parameterTypes);
        this.parameterClassnames = new String[this.parameterTypes.length];
        for (int i = 0; i < this.parameterTypes.length; i++) {
            this.parameterClassnames[i] = this.parameterTypes[i].getName();
        }
    }

    @Nonnull
    public Class<?> getReturnType() {
        return returnType;
    }

    @Nonnull
    public String getMethodName() {
        return methodName;
    }

    @Nonnull
    public Class<?>[] getParameterTypes() {
        return copyClassArray(parameterTypes);
    }

    public boolean isStatic() {
        return isStatic;
    }

    private Class<?>[] copyClassArray(Class<?>[] array) {
        Class<?>[] copy = new Class<?>[array.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    public String toString() {
        return (isStatic ? "static " : "") +
            returnType.getName() + " " +
            methodName + "(" +
            join(parameterTypes, ",") +
            ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodSignature that = (MethodSignature) o;

        return returnType.equals(that.returnType) &&
            methodName.equals(that.methodName) &&
            Arrays.equals(parameterClassnames, that.parameterClassnames) &&
            isStatic == that.isStatic;
    }

    @Override
    public int hashCode() {
        int result = methodName.hashCode();
        result = 31 * result + returnType.hashCode();
        result = 31 * result + Arrays.hashCode(parameterClassnames);
        result = 31 * result + (isStatic ? 1 : 0);
        return result;
    }

    public int compareTo(MethodSignature other) {
        return new CompareToBuilder()
            .append(methodName, other.methodName)
            .append(parameterClassnames, other.parameterClassnames)
            .append(returnType, other.returnType)
            .append(isStatic, other.isStatic)
            .toComparison();

    }

    private static String join(Object[] self, String separator) {
        StringBuilder buffer = new StringBuilder();
        boolean first = true;

        if (separator == null) separator = "";

        for (Object value : self) {
            if (first) {
                first = false;
            } else {
                buffer.append(separator);
            }
            buffer.append(String.valueOf(value));
        }
        return buffer.toString();
    }
}