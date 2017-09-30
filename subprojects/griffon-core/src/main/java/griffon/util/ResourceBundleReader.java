/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.util;

import griffon.core.env.Environment;
import griffon.core.env.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.isNotBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResourceBundleReader {
    private static final String ENVIRONMENTS_METHOD = "environments";

    private final Map<String, String> conditionValues = new LinkedHashMap<>();

    public static class Provider implements javax.inject.Provider<ResourceBundleReader> {
        @Inject private Metadata metadata;
        @Inject private Environment environment;

        @Override
        public ResourceBundleReader get() {
            ResourceBundleReader propertiesReader = new ResourceBundleReader();
            propertiesReader.registerConditionalBlock("environments", environment.getName());
            propertiesReader.registerConditionalBlock("projects", metadata.getApplicationName());
            propertiesReader.registerConditionalBlock("platforms", GriffonApplicationUtils.getPlatform());
            return propertiesReader;
        }
    }

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
    public ResourceBundle read(final @Nonnull ResourceBundle bundle) {
        requireNonNull(bundle, "Argument 'bundle' must not be null");
        return new AbstractMapResourceBundle() {
            @Override
            protected void initialize(@Nonnull Map<String, Object> entries) {
                entries.putAll(processBundle(bundle));
            }
        };
    }

    @Nonnull
    protected Map<String, Object> processBundle(@Nonnull ResourceBundle input) {
        Map<String, Object> output = new LinkedHashMap<>();

        for (String key : input.keySet()) {
            ConditionalBlockMatch match = resolveConditionalBlockMatch(key);
            if (match != null) {
                if (match.key != null) {
                    output.put(match.key, input.getObject(key));
                }
            } else {
                output.put(key, input.getObject(key));
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

    private static class ConditionalBlockMatch {
        public final String key;

        private ConditionalBlockMatch(String key) {
            this.key = key;
        }
    }
}
