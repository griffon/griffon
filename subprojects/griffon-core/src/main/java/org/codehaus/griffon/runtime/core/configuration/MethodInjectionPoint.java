/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class MethodInjectionPoint extends InjectionPoint {
    private static final Logger LOG = LoggerFactory.getLogger(FieldInjectionPoint.class);

    private final Method writeMethod;
    private final Class type;

    public MethodInjectionPoint(@Nonnull Method writeMethod, @Nonnull String configuration, @Nonnull String key, @Nonnull String format, @Nonnull Class<? extends PropertyEditor> editor) {
        super(configuration, key, format, editor);
        this.writeMethod = requireNonNull(writeMethod, "Argument 'writeMethod' must not be null");
        this.type = writeMethod.getParameterTypes()[0];
    }

    @Nonnull
    public Method getWriteMethod() {
        return writeMethod;
    }

    public void setValue(@Nonnull Object instance, Object value) {
        requireNonNull(instance, "Argument 'instance' must not be null");
        try {
            writeMethod.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Cannot set value on method " + getConfiguration() + "() of instance " + instance, sanitize(e));
            }
        }
    }

    @Nonnull
    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodInjectionPoint{");
        sb.append("writeMethod=").append(writeMethod);
        sb.append(", type=").append(type);
        sb.append(", configuration='").append(getConfiguration()).append('\'');
        sb.append(", key='").append(getKey()).append('\'');
        sb.append(", format='").append(getFormat()).append('\'');
        sb.append(", editor='").append(getEditor()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
