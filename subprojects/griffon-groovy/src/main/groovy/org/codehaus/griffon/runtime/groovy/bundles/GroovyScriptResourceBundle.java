/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.bundles;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.util.groovy.ConfigReader;
import groovy.lang.Script;
import groovy.util.ConfigObject;

import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static griffon.core.util.ConfigUtils.getConfigValue;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GroovyScriptResourceBundle extends ResourceBundle {
    private static final String ERROR_READER_NULL = "Argument 'reader' must not be null";
    private final ConfigObject config;
    private final Set<String> keys = new LinkedHashSet<>();
    private final String source;

    public GroovyScriptResourceBundle(@Nonnull ConfigReader reader, @Nonnull URL location) {
        this(requireNonNull(reader, ERROR_READER_NULL).parse(requireNonNull(location, "Argument 'location' must not be null")), location.toString());
    }

    public GroovyScriptResourceBundle(@Nonnull ConfigReader reader, @Nonnull Script script) {
        this(requireNonNull(reader, ERROR_READER_NULL).parse(requireNonNull(script, "Argument 'script' must not be null")), script.getClass().getName());
    }

    public GroovyScriptResourceBundle(@Nonnull ConfigReader reader, @Nonnull String script) {
        this(requireNonNull(reader, ERROR_READER_NULL).parse(requireNonBlank(script, "Argument 'script' must not be blank")), "<<INLINE SCRIPT>>");
    }

    public GroovyScriptResourceBundle(@Nonnull ConfigReader reader, @Nonnull Class<? extends Script> scriptClass) {
        this(requireNonNull(reader, ERROR_READER_NULL).parse(requireNonNull(scriptClass, "Argument 'scriptClass' must not be null")), scriptClass.getName());
    }

    @SuppressWarnings("unchecked")
    private GroovyScriptResourceBundle(@Nonnull ConfigObject config, @Nonnull String source) {
        this.config = requireNonNull(config, "Argument 'config' must not be null");
        this.source = source;
        keys.addAll(this.config.flatten(new LinkedHashMap<>()).keySet());
    }

    @Nonnull
    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + source + "]";
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected Object handleGetObject(@Nonnull String key) {
        Object value = getConfigValue(config, requireNonBlank(key, "Argument 'key' must not be blank"), null);
        if (null == value) { return null; }
        if (value instanceof ConfigObject) {
            return ((ConfigObject) value).isEmpty() ? null : value;
        }
        return value;
    }

    @Nonnull
    @Override
    public Enumeration<String> getKeys() {
        final Iterator<String> keysIterator = keys.iterator();
        return new Enumeration<String>() {
            public boolean hasMoreElements() {
                return keysIterator.hasNext();
            }

            public String nextElement() {
                return keysIterator.next();
            }
        };
    }

    @Override
    public Set<String> keySet() {
        return new LinkedHashSet<>(keys);
    }
}
