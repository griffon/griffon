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
package griffon.core.i18n;

import griffon.annotations.core.Nonnull;

import java.util.Locale;

/**
 * @author Alexander Klein
 * @since 2.0.0
 */
public class NoSuchMessageException extends javax.application.i18n.NoSuchMessageException {
    private static final long serialVersionUID = -7528419469022645775L;

    /**
     * Create a new exception.
     *
     * @param key    key that could not be resolved for given locale
     * @param locale locale that was used to search for the code within
     */
    public NoSuchMessageException(@Nonnull String key, @Nonnull Locale locale) {
        super(key, locale);
    }

    /**
     * Create a new exception.
     *
     * @param key key that could not be resolved for given locale
     */
    public NoSuchMessageException(@Nonnull String key) {
        super(key);
    }

    /**
     * Create a new exception.
     *
     * @param key   key that could not be resolved for given locale
     * @param cause throwable that caused this exception
     */
    public NoSuchMessageException(@Nonnull String key, @Nonnull Throwable cause) {
        super(key, cause);
    }

    /**
     * Create a new exception.
     *
     * @param key    key that could not be resolved for given locale
     * @param locale locale that was used to search for the code within
     * @param cause  throwable that caused this exception
     */
    public NoSuchMessageException(@Nonnull String key, @Nonnull Locale locale, @Nonnull Throwable cause) {
        super(key, locale, cause);
    }
}