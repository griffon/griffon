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
package org.codehaus.griffon.runtime.validation;

import griffon.plugins.validation.FieldObjectError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;

/**
 * @author Andres Almiray
 */
public class DefaultFieldObjectError extends DefaultObjectError implements FieldObjectError {
    private final String fieldName;
    private Object rejectedValue;

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String code) {
        this(fieldName, null, code, NO_ARGS, code);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String code, Object[] arguments) {
        this(fieldName, null, code, arguments, code);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String code, Object[] arguments, String defaultMessage) {
        this(fieldName, null, code, arguments, defaultMessage);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String code, String defaultMessage) {
        this(fieldName, null, code, NO_ARGS, defaultMessage);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String code) {
        this(fieldName, rejectedValue, code, NO_ARGS, code);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String code, Object[] arguments) {
        this(fieldName, rejectedValue, code, arguments, code);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String code, String defaultMessage) {
        this(fieldName, rejectedValue, code, NO_ARGS, defaultMessage);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String code, Object[] arguments, String defaultMessage) {
        super(code, arguments, defaultMessage);
        this.fieldName = requireNonBlank(fieldName, "Argument 'fieldName' cannot be blank");
        this.rejectedValue = rejectedValue;
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String[] codes) {
        this(fieldName, null, codes, NO_ARGS, null);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String[] codes, Object[] arguments) {
        this(fieldName, null, codes, arguments, null);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String[] codes, String defaultMessage) {
        this(fieldName, null, codes, NO_ARGS, defaultMessage);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nonnull String[] codes, Object[] arguments, String defaultMessage) {
        this(fieldName, null, codes, arguments, defaultMessage);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String[] codes) {
        this(fieldName, rejectedValue, codes, NO_ARGS, null);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String[] codes, Object[] arguments) {
        this(fieldName, rejectedValue, codes, arguments, null);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String[] codes, String defaultMessage) {
        this(fieldName, rejectedValue, codes, NO_ARGS, defaultMessage);
    }

    public DefaultFieldObjectError(@Nonnull String fieldName, @Nullable Object rejectedValue, @Nonnull String[] codes, Object[] arguments, String defaultMessage) {
        super(codes, arguments, defaultMessage);
        this.fieldName = requireNonBlank(fieldName, "Argument 'fieldName' cannot be blank");
        this.rejectedValue = rejectedValue;
    }

    @Nonnull
    public String getFieldName() {
        return fieldName;
    }

    @Nullable
    public Object getRejectedValue() {
        return rejectedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldObjectError)) return false;
        if (!super.equals(o)) return false;

        DefaultFieldObjectError that = (DefaultFieldObjectError) o;

        return fieldName.equals(that.fieldName) &&
            !(rejectedValue != null ? !rejectedValue.equals(that.rejectedValue) : that.rejectedValue != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + (rejectedValue != null ? rejectedValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldObjectError{" +
            "fieldName='" + fieldName + '\'' +
            ", rejectedValue=" + rejectedValue +
            ", codes=" + asList(getCodes()) +
            ", arguments=" + asList(getArguments()) +
            ", defaultMessage='" + getDefaultMessage() + '\'' +
            '}';
    }
}
