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
package org.codehaus.griffon.runtime.core.bundles;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.env.Environment;
import griffon.core.env.Metadata;
import griffon.core.util.GriffonApplicationUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static griffon.util.StringUtils.isBlank;
import static griffon.util.StringUtils.isNotBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class PropertiesReader {
    private static final String ENVIRONMENTS_METHOD = "environments";

    private final Map<String, String> conditionValues = new LinkedHashMap<>();

    public void registerConditionalBlock(@Nullable String blockName, @Nullable String blockValue) {
        if (isNotBlank(blockName)) {
            if (isBlank(blockValue)) {
                conditionValues.remove(blockName);
            } else {
                conditionValues.put(blockName, blockValue);
            }
        }
    }

    @Nonnull
    public Map<String, String> getConditionalBlockValues() {
        return Collections.unmodifiableMap(conditionValues);
    }

    @Nonnull
    public String getEnvironment() {
        return conditionValues.get(ENVIRONMENTS_METHOD);
    }

    public void setEnvironment(String environment) {
        conditionValues.put(ENVIRONMENTS_METHOD, environment);
    }

    @Nonnull
    public Properties load(@Nonnull URL location) throws IOException {
        return load(requireNonNull(location, "Argument 'location' must not be null").openStream());
    }

    @Nonnull
    public Properties load(@Nonnull InputStream stream) throws IOException {
        Properties p = new Properties();
        p.load(stream);
        return processProperties(p);
    }

    @Nonnull
    public Properties load(@Nonnull Reader reader) throws IOException {
        Properties p = new Properties();
        p.load(reader);
        return processProperties(p);
    }

    @Nonnull
    protected Properties processProperties(@Nonnull Properties input) {
        Properties output = new Properties();

        for (String key : input.stringPropertyNames()) {
            ConditionalBlockMatch match = resolveConditionalBlockMatch(key);
            if (match != null) {
                if (match.key != null) {
                    output.put(match.key, input.getProperty(key));
                }
            } else {
                output.put(key, input.getProperty(key));
            }
        }

        return output;
    }

    @Nullable
    private ConditionalBlockMatch resolveConditionalBlockMatch(@Nonnull String key) {
        for (Map.Entry<String, String> e : conditionValues.entrySet()) {
            String blockName = e.getKey();
            if (!key.startsWith(blockName + ".")) {
                continue;
            }

            String prefix = blockName + "." + e.getValue() + ".";
            if (key.startsWith(prefix)) {
                String subkey = key.substring(prefix.length());
                ConditionalBlockMatch match = resolveConditionalBlockMatch(subkey);
                if (match == null) {
                    match = new ConditionalBlockMatch(subkey);
                }
                return match;
            } else {
                return new ConditionalBlockMatch(null);
            }
        }

        return null;
    }

    public static class Provider implements javax.inject.Provider<PropertiesReader> {
        @Inject
        private Metadata metadata;
        @Inject
        private Environment environment;

        @Override
        public PropertiesReader get() {
            PropertiesReader propertiesReader = new PropertiesReader();
            propertiesReader.registerConditionalBlock("environments", environment.getName());
            propertiesReader.registerConditionalBlock("projects", metadata.getApplicationName());
            propertiesReader.registerConditionalBlock("platforms", GriffonApplicationUtils.getPlatform());
            return propertiesReader;
        }
    }

    private static class ConditionalBlockMatch {
        public final String key;

        private ConditionalBlockMatch(String key) {
            this.key = key;
        }
    }
}
