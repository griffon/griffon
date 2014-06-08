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
package org.codehaus.griffon.compile.core;

import java.lang.reflect.Modifier;

/**
 * @author Andres Almiray
 */
public class MethodDescriptor {
    private static final TypeParam[] EMPTY_PARAMETERS = new TypeParam[0];
    private static final Type[] EMPTY_TYPES = new Type[0];
    private static final Type NO_BOUND = new Type("_");

    public static class Type {
        public final String type;
        public final Type[] parameters;
        public final Type[] annotations;
        public final int dimensions;
        public String signature;

        public Type(String type) {
            this(EMPTY_TYPES, type, 0, EMPTY_TYPES);
        }

        public Type(String type, int dimensions) {
            this(EMPTY_TYPES, type, dimensions, EMPTY_TYPES);
        }

        public Type(String type, Type[] parameters) {
            this(EMPTY_TYPES, type, 0, parameters);
        }

        public Type(String type, int dimensions, Type[] parameters) {
            this(EMPTY_TYPES, type, dimensions, parameters);
        }

        public Type(Type[] annotations, String type) {
            this(annotations, type, 0, EMPTY_TYPES);
        }

        public Type(Type[] annotations, String type, int dimensions) {
            this(annotations, type, dimensions, EMPTY_TYPES);
        }

        public Type(Type[] annotations, String type, Type[] parameters) {
            this(annotations, type, 0, parameters);
        }

        public Type(Type[] annotations, String type, int dimensions, Type[] parameters) {
            this.type = type;
            this.dimensions = dimensions;
            this.parameters = parameters != null ? parameters : EMPTY_TYPES;
            this.annotations = annotations != null ? annotations : EMPTY_TYPES;
        }

        public String signature() {
            if (signature == null) {
                signature = createTypeSignature();
            }
            return signature;
        }

        protected String createTypeSignature() {
            StringBuilder b = new StringBuilder("");
            for (Type annotation : annotations) {
                b.append("@")
                    .append(annotation.signature())
                    .append(" ");
            }
            b.append(type);
            if (parameters.length > 0) {
                b.append("<");
                for (int i = 0; i < parameters.length; i++) {
                    b.append(parameters[i].signature());
                    if (i < parameters.length - 1) b.append(", ");
                }
                b.append(">");
            }
            for (int d = 0; d < dimensions; d++) {
                b.append("[]");
            }
            return b.toString();
        }

        public String toString() {
            return signature();
        }
    }

    public static class Wildcard extends Type {
        public static final String EXTENDS = "extends";
        public static final String SUPER = "super";
        public static final String NONE = "_";

        public final String bound;

        public Wildcard() {
            this(NONE, EMPTY_TYPES);
        }

        public Wildcard(Type[] parameters) {
            this(EXTENDS, parameters);
        }

        public Wildcard(String bound, Type[] parameters) {
            super("?", parameters);
            this.bound = bound;
        }

        public boolean isExtends() {
            return EXTENDS.equals(bound);
        }

        public boolean isSuper() {
            return SUPER.equals(bound);
        }

        protected String createTypeSignature() {
            StringBuilder b = new StringBuilder(type);
            if (parameters.length > 0) {
                b.append(" ").append(bound).append(" ");
                for (int i = 0; i < parameters.length; i++) {
                    b.append(parameters[i].signature());
                    if (i < parameters.length - 1) b.append(", ");
                }
            }
            return b.toString();
        }
    }

    public static class TypeParam {
        public final String type;
        public final Type bound;
        public final String signature;

        public TypeParam(String type) {
            this(type, NO_BOUND);
        }

        public TypeParam(String type, Type bound) {
            this.type = type;
            this.bound = bound != null ? bound : NO_BOUND;
            this.signature = createTypeParamSignature();
        }

        public boolean isBound() {
            return bound != NO_BOUND;
        }

        private String createTypeParamSignature() {
            StringBuilder b = new StringBuilder(type);
            if (bound != NO_BOUND) {
                b.append(" extends ").append(bound.signature());
            }
            return b.toString();
        }

        public String toString() {
            return signature;
        }
    }

    public final int modifiers;
    public final String methodName;
    public final Type returnType;
    public final TypeParam[] typeParameters;
    public final Type[] annotations;
    public final Type[] exceptions;
    public final Type[] arguments;
    public final String signature;

    public MethodDescriptor(Type returnType, TypeParam[] typeParameters, String methodName, Type[] arguments, Type[] exceptions) {
        this(Modifier.PUBLIC, returnType, typeParameters, methodName, arguments, exceptions, EMPTY_TYPES);
    }

    public MethodDescriptor(Type returnType, TypeParam[] typeParameters, String methodName, Type[] arguments, Type[] exceptions, Type[] annotations) {
        this(Modifier.PUBLIC, returnType, typeParameters, methodName, arguments, exceptions, annotations);
    }

    public MethodDescriptor(int modifiers, Type returnType, TypeParam[] typeParameters, String methodName, Type[] arguments, Type[] exceptions) {
        this(modifiers, returnType, typeParameters, methodName, arguments, exceptions, EMPTY_TYPES);
    }

    public MethodDescriptor(int modifiers, Type returnType, TypeParam[] typeParameters, String methodName, Type[] arguments, Type[] exceptions, Type[] annotations) {
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.methodName = methodName;
        this.typeParameters = typeParameters != null ? typeParameters : EMPTY_PARAMETERS;
        this.arguments = arguments != null ? arguments : EMPTY_TYPES;
        this.exceptions = exceptions != null ? exceptions : EMPTY_TYPES;
        this.annotations = annotations != null ? annotations : EMPTY_TYPES;
        this.signature = createMethodSignature();
    }

    public String toString() {
        return signature;
    }

    private String createMethodSignature() {
        StringBuilder b = new StringBuilder();
        for (Type annotation : annotations) {
            b.append("@")
                .append(annotation.signature())
                .append(" ");
        }
        b.append(Modifier.toString(modifiers)).append(" ");
        if (typeParameters.length > 0) {
            b.append("<");
            for (int i = 0; i < typeParameters.length; i++) {
                b.append(typeParameters[i].signature);
                if (i < typeParameters.length - 1) b.append(", ");
            }
            b.append("> ");
        }
        b.append(returnType.signature())
            .append(" ")
            .append(methodName)
            .append("(");
        if (arguments.length > 0) {
            for (int i = 0; i < arguments.length; i++) {
                b.append(arguments[i].signature())
                    .append(" arg")
                    .append(i);
                if (i < arguments.length - 1) b.append(", ");
            }
        }
        b.append(")");
        if (exceptions.length > 0) {
            b.append(" throws ");
            for (int i = 0; i < exceptions.length; i++) {
                b.append(exceptions[i].signature());
                if (i < exceptions.length - 1) b.append(", ");
            }
        }
        return b.toString();
    }

    public static Wildcard wildcard(String... types) {
        return new Wildcard(types(types));
    }

    public static Wildcard wildcardWithParams(Type... types) {
        return new Wildcard(types);
    }

    public static Type annotation(String type) {
        return new Type(type);
    }

    public static Type[] annotations(String... types) {
        Type[] annotations = new Type[types.length];
        for (int i = 0; i < types.length; i++) {
            annotations[i] = annotation(types[i]);
        }
        return annotations;
    }

    public static Type type(String type, String... types) {
        return new Type(type, types(types));
    }

    public static Type type(String type, int dimensions, String... types) {
        return new Type(type, dimensions, types(types));
    }

    public static Type annotatedType(Type[] annotations, String type, String... types) {
        return new Type(annotations, type, types(types));
    }

    public static Type annotatedType(Type[] annotations, String type, int dimensions, String... types) {
        return new Type(annotations, type, dimensions, types(types));
    }

    public static Type typeWithParams(String type, Type... types) {
        return new Type(type, types);
    }

    public static Type typeWithParams(String type, int dimensions, Type... types) {
        return new Type(type, dimensions, types);
    }

    public static Type typeWithParams(Type[] annotations, String type, Type... types) {
        return new Type(annotations, type, types);
    }

    public static Type typeWithParams(Type[] annotations, String type, int dimensions, Type... types) {
        return new Type(annotations, type, dimensions, types);
    }

    public static TypeParam[] typeParams(String... typeParameters) {
        TypeParam[] params = new TypeParam[typeParameters.length];
        for (int i = 0; i < typeParameters.length; i++) {
            params[i] = new TypeParam(typeParameters[i]);
        }
        return params;
    }

    public static TypeParam[] typeParams(TypeParam... typeParameters) {
        return typeParameters;
    }

    public static TypeParam typeParam(String type) {
        return new TypeParam(type);
    }

    public static TypeParam typeParam(String type, String bound) {
        return new TypeParam(type, type(bound));
    }

    public static TypeParam typeParam(String type, Type bound) {
        return new TypeParam(type, bound);
    }

    public static Type[] types(String... types) {
        Type[] t = new Type[types.length];
        for (int i = 0; i < types.length; i++) {
            t[i] = type(types[i]);
        }
        return t;
    }

    public static Type[] types(Type... types) {
        return types;
    }

    public static Type[] args(Type... types) {
        return types;
    }

    public static Type[] throwing(Type... types) {
        return types;
    }

    public static MethodDescriptor method(Type type, TypeParam[] typeParameters, String methodName, Type[] args) {
        return new MethodDescriptor(type, typeParameters, methodName, args, EMPTY_TYPES);
    }

    public static MethodDescriptor method(Type type, TypeParam[] typeParameters, String methodName) {
        return new MethodDescriptor(type, typeParameters, methodName, EMPTY_TYPES, EMPTY_TYPES);
    }

    public static MethodDescriptor method(Type type, String methodName, Type[] args) {
        return new MethodDescriptor(type, EMPTY_PARAMETERS, methodName, args, EMPTY_TYPES);
    }

    public static MethodDescriptor method(Type type, String methodName) {
        return new MethodDescriptor(type, EMPTY_PARAMETERS, methodName, EMPTY_TYPES, EMPTY_TYPES);
    }

    public static MethodDescriptor method(Type type, TypeParam[] typeParameters, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(type, typeParameters, methodName, args, exceptions);
    }

    public static MethodDescriptor method(Type type, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(type, EMPTY_PARAMETERS, methodName, args, exceptions);
    }

    public static MethodDescriptor method(int modifiers, Type type, TypeParam[] typeParameters, String methodName, Type[] args) {
        return new MethodDescriptor(modifiers, type, typeParameters, methodName, args, EMPTY_TYPES);
    }

    public static MethodDescriptor method(int modifiers, Type type, TypeParam[] typeParameters, String methodName) {
        return new MethodDescriptor(modifiers, type, typeParameters, methodName, EMPTY_TYPES, EMPTY_TYPES);
    }

    public static MethodDescriptor method(int modifiers, Type type, String methodName, Type[] args) {
        return new MethodDescriptor(modifiers, type, EMPTY_PARAMETERS, methodName, args, EMPTY_TYPES);
    }

    public static MethodDescriptor method(int modifiers, Type type, String methodName) {
        return new MethodDescriptor(modifiers, type, EMPTY_PARAMETERS, methodName, EMPTY_TYPES, EMPTY_TYPES);
    }

    public static MethodDescriptor method(int modifiers, Type type, TypeParam[] typeParameters, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(modifiers, type, typeParameters, methodName, args, exceptions);
    }

    public static MethodDescriptor method(int modifiers, Type type, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(modifiers, type, EMPTY_PARAMETERS, methodName, args, exceptions);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, Type type, TypeParam[] typeParameters, String methodName, Type[] args) {
        return new MethodDescriptor(type, typeParameters, methodName, args, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, Type type, TypeParam[] typeParameters, String methodName) {
        return new MethodDescriptor(type, typeParameters, methodName, EMPTY_TYPES, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, Type type, String methodName, Type[] args) {
        return new MethodDescriptor(type, EMPTY_PARAMETERS, methodName, args, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, Type type, String methodName) {
        return new MethodDescriptor(type, EMPTY_PARAMETERS, methodName, EMPTY_TYPES, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, Type type, TypeParam[] typeParameters, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(type, typeParameters, methodName, args, exceptions, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, Type type, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(type, EMPTY_PARAMETERS, methodName, args, exceptions, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, int modifiers, Type type, TypeParam[] typeParameters, String methodName, Type[] args) {
        return new MethodDescriptor(modifiers, type, typeParameters, methodName, args, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, int modifiers, Type type, TypeParam[] typeParameters, String methodName) {
        return new MethodDescriptor(modifiers, type, typeParameters, methodName, EMPTY_TYPES, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, int modifiers, Type type, String methodName, Type[] args) {
        return new MethodDescriptor(modifiers, type, EMPTY_PARAMETERS, methodName, args, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, int modifiers, Type type, String methodName) {
        return new MethodDescriptor(modifiers, type, EMPTY_PARAMETERS, methodName, EMPTY_TYPES, EMPTY_TYPES, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, int modifiers, Type type, TypeParam[] typeParameters, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(modifiers, type, typeParameters, methodName, args, exceptions, annotations);
    }

    public static MethodDescriptor annotatedMethod(Type[] annotations, int modifiers, Type type, String methodName, Type[] args, Type[] exceptions) {
        return new MethodDescriptor(modifiers, type, EMPTY_PARAMETERS, methodName, args, exceptions, annotations);
    }
}
