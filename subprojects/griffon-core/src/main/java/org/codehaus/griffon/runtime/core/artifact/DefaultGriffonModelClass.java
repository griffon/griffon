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

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonModelClass;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static griffon.util.GriffonClassUtils.isEventHandler;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultGriffonModelClass extends DefaultGriffonClass implements GriffonModelClass {
    private static final String ERROR_MODEL_NULL = "Argument 'model' cannot be null";
    private static final String ERROR_PROPERTY_NAME_BLANK = "Argument 'propertyName' cannot be blank";

    protected final Set<String> propertiesCache = new LinkedHashSet<>();
    private static final Set<String> BINDABLE_PROPERTIES = new LinkedHashSet<>(
        Arrays.asList("propertyChangeListeners", "vetoableChangeListeners"));

    public DefaultGriffonModelClass(@Nonnull Class<?> clazz) {
        super(clazz, TYPE, TRAILING);
    }

    public void resetCaches() {
        super.resetCaches();
        propertiesCache.clear();
    }

    @Nonnull
    public String[] getPropertyNames() {
        if (propertiesCache.isEmpty()) {
            for (String propertyName : getPropertiesWithFields()) {
                if (!propertiesCache.contains(propertyName) &&
                    !isEventHandler(propertyName) &&
                    !STANDARD_PROPERTIES.contains(propertyName) &&
                    !BINDABLE_PROPERTIES.contains(propertyName)) {
                    propertiesCache.add(propertyName);
                }
            }
        }

        return propertiesCache.toArray(new String[propertiesCache.size()]);
    }

    public void setModelPropertyValue(@Nonnull GriffonModel model, @Nonnull String propertyName, @Nullable Object value) {
        requireNonNull(model, ERROR_MODEL_NULL);
        requireNonBlank(propertyName, ERROR_PROPERTY_NAME_BLANK);
        GriffonClassUtils.setPropertyValue(model, propertyName, value);
    }

    @Nullable
    public Object getModelPropertyValue(@Nonnull GriffonModel model, @Nonnull String propertyName) {
        requireNonNull(model, ERROR_MODEL_NULL);
        requireNonBlank(propertyName, ERROR_PROPERTY_NAME_BLANK);
        return GriffonClassUtils.getPropertyValue(model, propertyName);
    }
}
