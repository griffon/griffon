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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class PropertyException extends GriffonException {
    private static final long serialVersionUID = 6721682115856360089L;
    private static final String CAUSE = "cause";
    private static final String BEAN = "bean";
    private static final String PROPERTY_NAME = "propertyName";

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName, @Nullable Object value) {
        super(formatArgs(bean, propertyName, value));
    }

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName, @Nullable Object value, @Nonnull Throwable cause) {
        super(formatArgs(bean, propertyName, value), checkNonNull(cause, CAUSE));
    }

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName) {
        super(formatArgs(bean, propertyName));
    }

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName, @Nonnull Throwable cause) {
        super(formatArgs(bean, propertyName), checkNonNull(cause, CAUSE));
    }

    public PropertyException(@Nonnull Class<?> klass, @Nonnull String propertyName) {
        super(formatArgs(klass, propertyName));
    }

    public PropertyException(@Nonnull Class<?> klass, @Nonnull String propertyName, @Nonnull Throwable cause) {
        super(formatArgs(klass, propertyName), checkNonNull(cause, CAUSE));
    }

    @Nonnull
    private static String formatArgs(@Nonnull Object bean, @Nonnull String propertyName, @Nullable Object value) {
        checkNonNull(bean, BEAN);
        checkNonBlank(propertyName, PROPERTY_NAME);
        return "Cannot set property " + propertyName + " with value " + value + " on " + bean;
    }

    @Nonnull
    private static String formatArgs(@Nonnull Object bean, @Nonnull String propertyName) {
        checkNonNull(bean, BEAN);
        checkNonBlank(propertyName, PROPERTY_NAME);
        return "Cannot get property " + propertyName + " from " + bean;
    }

    @Nonnull
    private static String formatArgs(@Nonnull Class<?> klass, @Nonnull String propertyName) {
        checkNonNull(klass, "klass");
        checkNonBlank(propertyName, PROPERTY_NAME);
        return klass + " does not have a property named " + propertyName;
    }
}
