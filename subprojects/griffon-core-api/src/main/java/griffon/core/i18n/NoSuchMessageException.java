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
package griffon.core.i18n;

import griffon.annotations.core.Nonnull;

import java.util.Locale;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Alexander Klein
 * @since 2.0.0
 */
public class NoSuchMessageException extends RuntimeException {
    private static final long serialVersionUID = 9098095569037988099L;

    private final String key;
    private final Locale locale;

    /**
     * Create a new exception.
     *
     * @param key    key that could not be resolved for given locale
     * @param locale locale that was used to search for the code within
     */
    public NoSuchMessageException(@Nonnull String key, @Nonnull Locale locale) {
        this(key, locale, null);
    }

    /**
     * Create a new exception.
     *
     * @param key key that could not be resolved for given locale
     */
    public NoSuchMessageException(@Nonnull String key) {
        this(key, Locale.getDefault());
    }

    /**
     * Create a new exception.
     *
     * @param key   key that could not be resolved for given locale
     * @param cause throwable that caused this exception
     */
    public NoSuchMessageException(@Nonnull String key, @Nonnull Throwable cause) {
        this(key, Locale.getDefault(), cause);
    }

    /**
     * Create a new exception.
     *
     * @param key    key that could not be resolved for given locale
     * @param locale locale that was used to search for the code within
     * @param cause  throwable that caused this exception
     */
    public NoSuchMessageException(@Nonnull String key, @Nonnull Locale locale, @Nonnull Throwable cause) {
        super("No message found under key '" + requireNonBlank(key, "key") + "' for locale '" + requireNonNull(locale, "locale") + "'.", cause);
        this.key = key;
        this.locale = locale;
    }

    /**
     * Get the key without a valid value
     *
     * @return The key
     */
    @Nonnull
    public String getKey() {
        return key;
    }

    /**
     * Get the locale without a valid value
     *
     * @return The locale
     */
    @Nonnull
    public Locale getLocale() {
        return locale;
    }
}