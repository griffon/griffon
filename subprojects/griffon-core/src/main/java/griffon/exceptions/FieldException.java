/*
 * Copyright 2008-2015 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.1.0
 */
public class FieldException extends GriffonException {
    private static final long serialVersionUID = 4319304904847269368L;

    public FieldException(@Nonnull Object bean, @Nonnull String fieldName, @Nullable Object value) {
        super(formatArgs(bean, fieldName, value));
    }

    public FieldException(@Nonnull Object bean, @Nonnull String fieldName, @Nullable Object value, @Nonnull Throwable cause) {
        super(formatArgs(bean, fieldName, value), checkNonNull(cause, "cause"));
    }

    public FieldException(@Nonnull Object bean, @Nonnull String fieldName) {
        super(formatArgs(bean, fieldName));
    }

    public FieldException(@Nonnull Object bean, @Nonnull String fieldName, @Nonnull Throwable cause) {
        super(formatArgs(bean, fieldName), checkNonNull(cause, "cause"));
    }

    public FieldException(@Nonnull Class<?> klass, @Nonnull String fieldName) {
        super(formatArgs(klass, fieldName));
    }

    public FieldException(@Nonnull Class<?> klass, @Nonnull String fieldName, @Nonnull Throwable cause) {
        super(formatArgs(klass, fieldName), checkNonNull(cause, "cause"));
    }

    @Nonnull
    private static String formatArgs(@Nonnull Object bean, @Nonnull String fieldName, @Nullable Object value) {
        checkNonNull(bean, "bean");
        checkNonBlank(fieldName, "fieldName");
        return "Cannot set field " + fieldName + " with value " + value + " on " + bean;
    }

    @Nonnull
    private static String formatArgs(@Nonnull Object bean, @Nonnull String fieldName) {
        checkNonNull(bean, "bean");
        checkNonBlank(fieldName, "fieldName");
        return "Cannot get field " + fieldName + " from " + bean;
    }

    @Nonnull
    private static String formatArgs(@Nonnull Class<?> klass, @Nonnull String fieldName) {
        checkNonNull(klass, "klass");
        checkNonBlank(fieldName, "fieldName");
        return klass + " does not have a field named " + fieldName;
    }
}
