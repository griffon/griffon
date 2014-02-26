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

import griffon.plugins.validation.ObjectError;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;

/**
 * @author Andres Almiray
 */
public class DefaultObjectError implements ObjectError {
    private final String code;
    private final @Nonnull String[] codes;
    private final Object[] arguments;
    private final String defaultMessage;

    private static final String NO_MESSAGE = "griffon.plugins.validation.ObjectError.NO_MESSAGE";

    public DefaultObjectError(@Nonnull String code) {
        this(code, NO_ARGS, code);
    }

    public DefaultObjectError(@Nonnull String code, Object[] arguments) {
        this(code, arguments, code);
    }

    public DefaultObjectError(@Nonnull String code, String defaultMessage) {
        this(code, NO_ARGS, defaultMessage);
    }

    public DefaultObjectError(@Nonnull String code, Object[] arguments, String defaultMessage) {
        this.code = requireNonBlank(code, "Argument 'code' cannot be blank");
        this.codes = new String[]{code};
        this.arguments = null == arguments ? NO_ARGS : copy(arguments);
        this.defaultMessage = isBlank(defaultMessage) ? code : defaultMessage;
    }

    public DefaultObjectError(@Nonnull String[] codes) {
        this(codes, NO_ARGS, null);
    }

    public DefaultObjectError(@Nonnull String[] codes, Object[] arguments) {
        this(codes, arguments, null);
    }

    public DefaultObjectError(@Nonnull String[] codes, String defaultMessage) {
        this(codes, NO_ARGS, defaultMessage);
    }

    public DefaultObjectError(@Nonnull String[] codes, Object[] arguments, String defaultMessage) {
        requireState(codes != null && codes.length > 0, "Argument 'codes' cannot be null nor empty");

        this.code = codes[codes.length - 1];
        this.codes = copy(codes);
        this.arguments = null == arguments ? NO_ARGS : copy(arguments);
        this.defaultMessage = isBlank(defaultMessage) || NO_MESSAGE.equals(defaultMessage) ? code : defaultMessage;
    }

    public Object[] getArguments() {
        return copy(arguments);
    }

    @Nonnull
    public String getCode() {
        return code;
    }

    @Nonnull
    public String[] getCodes() {
        return copy(codes);
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    protected String[] copy(String[] array) {
        String[] tmp = new String[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        return tmp;
    }

    protected Object[] copy(Object[] array) {
        Object[] tmp = new Object[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultObjectError)) return false;

        DefaultObjectError that = (DefaultObjectError) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(arguments, that.arguments)) return false;
        if (!code.equals(that.code)) return false;
        if (!Arrays.equals(codes, that.codes)) return false;
        if (!defaultMessage.equals(that.defaultMessage)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + Arrays.hashCode(codes);
        result = 31 * result + Arrays.hashCode(arguments);
        result = 31 * result + defaultMessage.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ObjectError{" +
            "codes=" + asList(getCodes()) +
            ", arguments=" + asList(getArguments()) +
            ", defaultMessage='" + defaultMessage + '\'' +
            '}';
    }
}
