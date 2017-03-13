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
package org.codehaus.griffon.runtime.core.configuration;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class MethodConfigurationDescriptor extends ConfigurationDescriptor {
    private final Method writeMethod;

    public MethodConfigurationDescriptor(@Nonnull Method writeMethod, @Nonnull String configuration, @Nonnull String key, @Nonnull String defaultValue, @Nonnull String format) {
        super(configuration, key, defaultValue, format);
        this.writeMethod = requireNonNull(writeMethod, "Argument 'writeMethod' must not be null");
    }

    @Nonnull
    public Method getWriteMethod() {
        return writeMethod;
    }

    @Nonnull
    public InjectionPoint asInjectionPoint() {
        return new MethodInjectionPoint(writeMethod, getConfiguration(), getKey(), getFormat());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodPreferenceDescriptor{");
        sb.append("writeMethod=").append(writeMethod);
        sb.append(", configuration='").append(getConfiguration()).append('\'');
        sb.append(", key='").append(getKey()).append('\'');
        sb.append(", defaultValue='").append(getDefaultValue()).append('\'');
        sb.append(", format='").append(getFormat()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
