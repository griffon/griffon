/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.core.controller;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.annotations.inject.Contextual;
import griffon.core.controller.ActionMetadata;
import griffon.core.controller.ActionParameter;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.isNotBlank;
import static java.lang.System.arraycopy;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class DefaultActionParameter implements ActionParameter {
    private static final Annotation[] EMPTY_ARRAY = new Annotation[0];

    private final int index;
    private final Class<?> type;
    private final String name;
    private final Annotation[] annotations;
    private final boolean contextual;
    private final boolean nullable;

    public DefaultActionParameter(@Nonnull ActionMetadata actionMetadata, int index, @Nonnull Class<?> type, @Nullable Annotation[] annotations) {
        requireState(index >= 0, "Parameter index must be equal or greater than 0 [" + index + "]");
        this.index = index;
        this.type = type;

        if (annotations != null) {
            this.annotations = new Annotation[annotations.length];
            arraycopy(annotations, 0, this.annotations, 0, annotations.length);

            boolean c = false;
            boolean u = false;
            String n = null;
            for (Annotation annotation : this.annotations) {
                if (Contextual.class.isAssignableFrom(annotation.annotationType())) {
                    c = true;
                }
                if (Nonnull.class.isAssignableFrom(annotation.annotationType())) {
                    u = true;
                }
                if (Named.class.isAssignableFrom(annotation.annotationType())) {
                    n = ((Named) annotation).value();
                }
            }
            this.contextual = c;
            this.nullable = !u;
            this.name = isNotBlank(n) ? n : resolveParameterName(actionMetadata, index);
        } else {
            this.annotations = EMPTY_ARRAY;
            this.contextual = false;
            this.nullable = false;
            this.name = resolveParameterName(actionMetadata, index);
        }
    }

    @Nonnull
    protected String resolveParameterName(@Nonnull ActionMetadata actionMetadata, int index) {
        return actionMetadata.getFullyQualifiedName() + ".arg" + index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Nonnull
    @Override
    public Annotation[] getAnnotations() {
        return annotations;
    }

    @Nonnull
    @Override
    public Class<?> getType() {
        return type;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isContextual() {
        return contextual;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultActionParameter{");
        sb.append("index=").append(index);
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", annotations=").append(Arrays.toString(annotations));
        sb.append(", contextual=").append(contextual);
        sb.append(", nullable=").append(nullable);
        sb.append('}');
        return sb.toString();
    }
}
