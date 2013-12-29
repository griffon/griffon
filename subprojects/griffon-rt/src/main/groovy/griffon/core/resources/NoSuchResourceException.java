/*
 * Copyright 2010-2014 the original author or authors.
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

package griffon.core.resources;

import java.util.Locale;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 1.1.0
 */
public class NoSuchResourceException extends RuntimeException {
    private String key;
    private Locale locale;

    /**
     * Create a new exception.
     *
     * @param key    message that could not be resolved for given locale
     * @param locale locale that was used to search for the code within
     */
    public NoSuchResourceException(String key, Locale locale) {
        super("No resorce found under key '" + key + "' for locale '" + locale + "'.");
        this.key = key;
        this.locale = locale;
    }

    /**
     * Create a new exception.
     *
     * @param key key that could not be resolved for given locale
     */
    public NoSuchResourceException(String key) {
        this(key, Locale.getDefault());
    }

    /**
     * Get the key without a valid value
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the locale without a valid value
     *
     * @return The locale
     */
    public Locale getLocale() {
        return locale;
    }
}

