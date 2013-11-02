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
public class PropertyException extends GriffonException {
    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName, @Nullable Object value) {
        super(formatArgs(bean, propertyName, value));
    }

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName, @Nullable Object value, @Nonnull Throwable cause) {
        super(formatArgs(bean, propertyName, value), checkNonNull(cause, "cause"));
    }

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName) {
        super(formatArgs(bean, propertyName));
    }

    public PropertyException(@Nonnull Object bean, @Nonnull String propertyName, @Nonnull Throwable cause) {
        super(formatArgs(bean, propertyName), checkNonNull(cause, "cause"));
    }

    @Nonnull
    private static String formatArgs(@Nonnull Object bean, @Nonnull String propertyName, @Nullable Object value) {
        checkNonNull(bean, "bean");
        checkNonBlank(propertyName, "propertyName");
        return "Cannot set property " + propertyName + " with value " + value + " on " + bean;
    }

    @Nonnull
    private static String formatArgs(@Nonnull Object bean, @Nonnull String propertyName) {
        checkNonNull(bean, "bean");
        checkNonBlank(propertyName, "propertyName");
        return "Cannot get property " + propertyName + " from " + bean;
    }
}
