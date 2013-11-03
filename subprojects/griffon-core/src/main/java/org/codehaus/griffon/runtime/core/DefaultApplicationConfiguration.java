/*
 * Copyright 2009-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultApplicationConfiguration extends AbstractApplicationConfiguration {
    private static final String ERROR_KEY_BLANK = "Argument 'key' cannot be blank";
    private final ResourceBundle resourceBundle;
    private final Map<String, Object> flatMap = new LinkedHashMap<>();

    @Inject
    public DefaultApplicationConfiguration(@Nonnull @Named("applicationResourceBundle") ResourceBundle resourceBundle) {
        this.resourceBundle = requireNonNull(resourceBundle, "Argument 'resourceBundle' cannot be null");
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            flatMap.put(key, resourceBundle.getObject(key));
        }
    }

    public boolean containsKey(@Nonnull String key) {
        return resourceBundle.containsKey(requireNonBlank(key, ERROR_KEY_BLANK));
    }

    @Nonnull
    @Override
    public Map<String, Object> asFlatMap() {
        return unmodifiableMap(flatMap);
    }

    @Nullable
    @Override
    public Object get(@Nonnull String key) {
        try {
            return resourceBundle.getObject(requireNonBlank(key, ERROR_KEY_BLANK));
        } catch (MissingResourceException mre) {
            return null;
        }
    }
}
