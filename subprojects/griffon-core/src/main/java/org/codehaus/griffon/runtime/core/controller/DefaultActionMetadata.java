/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package org.codehaus.griffon.runtime.core.controller;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionMetadata;
import griffon.core.controller.ActionParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class DefaultActionMetadata implements ActionMetadata {
    private final String actionName;
    private final String fullyQualifiedName;
    private final Class<?> returnType;
    private final Annotation[] annotations;
    private final ActionParameter[] parameters;
    private final boolean contextual;

    public DefaultActionMetadata(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method) {
        this.actionName = requireNonBlank(actionName, "Argument 'actionName' must not be blank");
        requireNonNull(controller, "Argument 'controller' must not be null");
        requireNonNull(method, "Argument 'method' must not be null");
        this.fullyQualifiedName = controller.getTypeClass().getName() + "." + actionName;
        this.returnType = method.getReturnType();

        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        annotations = new Annotation[declaredAnnotations.length];
        arraycopy(declaredAnnotations, 0, annotations, 0, annotations.length);

        this.parameters = new ActionParameter[method.getParameterCount()];
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (this.parameters.length > 0) {
            boolean c = false;
            for (int index = 0; index < this.parameters.length; index++) {
                this.parameters[index] = createActionParameter(index, parameterTypes[index], parameterAnnotations[index]);
                c |= this.parameters[index].isContextual();
            }
            this.contextual = c;
        } else {
            this.contextual = false;
        }
    }

    @Nonnull
    protected ActionParameter createActionParameter(int index, @Nonnull Class<?> type, @Nullable Annotation[] annotations) {
        return new DefaultActionParameter(this, index, type, annotations);
    }

    @Nonnull
    @Override
    public Annotation[] getAnnotations() {
        return annotations;
    }

    @Nonnull
    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    @Nonnull
    @Override
    public ActionParameter[] getParameters() {
        return parameters;
    }

    @Nonnull
    @Override
    public String getActionName() {
        return actionName;
    }

    @Nonnull
    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    @Override
    public boolean hasContextualArgs() {
        return contextual;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultActionMetadata{");
        sb.append("actionName='").append(actionName).append('\'');
        sb.append(", fullyQualifiedName='").append(fullyQualifiedName).append('\'');
        sb.append(", returnType=").append(returnType);
        sb.append(", annotations=").append(Arrays.toString(annotations));
        sb.append(", parameters=").append(Arrays.toString(parameters));
        sb.append('}');
        return sb.toString();
    }
}
