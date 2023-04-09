/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.util;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

import static griffon.util.ObjectUtils.PRIMITIVE_TYPE_COMPATIBLE_TYPES;
import static griffon.util.ObjectUtils.isAssignableOrConvertibleFrom;
import static griffon.util.ObjectUtils.isMatchBetweenPrimitiveAndWrapperTypes;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class MethodDescriptor implements Comparable<MethodDescriptor> {
    private static final String[] EMPTY_CLASS_PARAMETERS = new String[0];
    private final String methodName;
    private final String[] paramTypes;
    private final int hashCode;
    private final int modifiers;
    private final Annotation[] annotations;

    public MethodDescriptor(@Nonnull String methodName) {
        this(methodName, EMPTY_CLASS_PARAMETERS, Modifier.PUBLIC);
    }

    public MethodDescriptor(@Nonnull String methodName, int modifiers) {
        this(methodName, EMPTY_CLASS_PARAMETERS, modifiers, new Annotation[0]);
    }

    public MethodDescriptor(@Nonnull String methodName, @Nonnull Class<?>[] paramTypes) {
        this(methodName, paramTypes, Modifier.PUBLIC, new Annotation[0]);
    }

    public MethodDescriptor(@Nonnull String methodName, @Nonnull String[] paramTypes) {
        this(methodName, paramTypes, Modifier.PUBLIC, new Annotation[0]);
    }

    public MethodDescriptor(@Nonnull String methodName, @Nonnull Class<?>[] paramTypes, int modifiers) {
        this(methodName, paramTypes, Modifier.PUBLIC, new Annotation[0]);
    }

    public MethodDescriptor(@Nonnull String methodName, @Nonnull Class<?>[] paramTypes, int modifiers, @Nonnull Annotation[] annotations) {
        this.methodName = requireNonBlank(methodName, "Argment 'methodName' must not be blank");
        requireNonNull(paramTypes, "Argument 'paramTypes' must not be null");
        requireNonNull(annotations, "Argument 'annotations' must not be null");

        this.paramTypes = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            this.paramTypes[i] = paramTypes[i].getName();
        }

        this.modifiers = modifiers;
        this.annotations = Arrays.copyOf(annotations, annotations.length);
        Arrays.sort(this.annotations, Comparator.comparing(o -> o.getClass().getName()));

        this.hashCode = 31 * methodName.hashCode() + modifiers;
    }

    public MethodDescriptor(@Nonnull String methodName, @Nonnull String[] paramTypes, int modifiers) {
        this(methodName, paramTypes, modifiers, new Annotation[0]);
    }

    public MethodDescriptor(@Nonnull String methodName, @Nonnull String[] paramTypes, int modifiers, @Nonnull Annotation[] annotations) {
        this.methodName = requireNonBlank(methodName, "Argment 'methodName' must not be blank");
        requireNonNull(paramTypes, "Argument 'paramTypes' must not be null");
        requireNonNull(annotations, "Argument 'annotations' must not be null");

        this.paramTypes = Arrays.copyOf(paramTypes, paramTypes.length);

        this.modifiers = modifiers;
        this.annotations = Arrays.copyOf(annotations, annotations.length);
        Arrays.sort(this.annotations, Comparator.comparing(o -> o.getClass().getName()));

        this.hashCode = 31 * methodName.hashCode() + modifiers;
    }

    @Nonnull
    public String getName() {
        return methodName;
    }

    @Nonnull
    public String[] getParameterTypes() {
        return paramTypes;
    }

    @Nonnull
    public Annotation[] getAnnotations() {
        return annotations;
    }

    public int getModifiers() {
        return modifiers;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MethodDescriptor)) {
            return false;
        }
        MethodDescriptor md = (MethodDescriptor) obj;

        return methodName.equals(md.methodName) &&
            modifiers == md.modifiers &&
            areParametersCompatible(paramTypes, md.paramTypes) &&
            Arrays.equals(annotations, md.annotations);
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        String a = Arrays.toString(annotations);
        StringBuilder b = new StringBuilder(a.substring(1, a.length() - 1));
        b.append(Modifier.toString(modifiers)).append(" ");
        b.append(methodName).append("(");
        for (int i = 0; i < paramTypes.length; i++) {
            if (i != 0) {
                b.append(", ");
            }
            b.append(paramTypes[i]);
        }
        b.append(")");
        return b.toString();
    }

    public int compareTo(MethodDescriptor md) {
        int c = methodName.compareTo(md.methodName);
        if (c != 0) {
            return c;
        }
        c = modifiers - md.modifiers;
        if (c != 0) {
            return c;
        }
        c = paramTypes.length - md.paramTypes.length;
        if (c != 0) {
            return c;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            c = paramTypes[i].compareTo(md.paramTypes[i]);
            if (c != 0) {
                return c;
            }
        }

        return 0;
    }

    public boolean matches(@Nonnull MethodDescriptor md) {
        requireNonNull(md, "Argument 'methodDescriptor' must not be null");
        if (!methodName.equals(md.methodName) ||
            modifiers != md.modifiers ||
            paramTypes.length != md.paramTypes.length ||
            annotations.length != md.annotations.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> param1 = loadClass(paramTypes[i]);
            Class<?> param2 = loadClass(md.paramTypes[i]);
            if (param1 != null && param2 != null && !isAssignableOrConvertibleFrom(param1, param2)) {
                return false;
            }
        }

        return Arrays.equals(annotations, md.annotations);
    }

    public boolean matches(@Nonnull Class[] otherParamTypes) {
        requireNonNull(otherParamTypes, "Argument 'otherParamTypes' must not be null");

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> param1 = loadClass(paramTypes[i]);
            Class<?> param2 = otherParamTypes[i];
            if (param1 != null && param2 != null && !isAssignableOrConvertibleFrom(param1, param2)) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    private Class<?> loadClass(@Nonnull String classname) {
        try {
            return Class.forName(classname, true, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            if (PRIMITIVE_TYPE_COMPATIBLE_TYPES.containsKey(classname)) {
                try {
                    classname = PRIMITIVE_TYPE_COMPATIBLE_TYPES.get(classname);
                    return Class.forName(classname, true, getClass().getClassLoader());
                } catch (ClassNotFoundException e1) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @Nonnull
    public static MethodDescriptor forMethod(@Nonnull Method method) {
        return forMethod(method, false);
    }

    @Nonnull
    public static MethodDescriptor forMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, "Argument 'method' must not be null");
        int modifiers = method.getModifiers();
        if (removeAbstractModifier) {
            modifiers -= Modifier.ABSTRACT;
        }
        return new MethodDescriptor(method.getName(), method.getParameterTypes(), modifiers, method.getDeclaredAnnotations());
    }

    private static boolean areParametersCompatible(String[] params1, String[] params2) {
        if (params1.length != params2.length) {
            return false;
        }

        for (int i = 0; i < params1.length; i++) {
            String p1 = params1[i];
            String p2 = params2[i];
            if (!p1.equals(p2) && !isMatchBetweenPrimitiveAndWrapperTypes(p1, p2)) {
                return false;
            }
        }

        return true;
    }
}
