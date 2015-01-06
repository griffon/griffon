/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.plugins.preferences;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public interface PreferencesManager {
    /**
     * Returns the {@code Preferences} instance contained by this manager.</p>
     * Never returns null.
     *
     * @return the {@code Preferences} instance contained by this manager.
     */
    @Nonnull
    Preferences getPreferences();

    /**
     * <p>Merges all fields/properties annotated with {@code @Preference} to their
     * corresponding {@code PreferencesNode} within the contained {@code Preferences}.</p>
     * <p>Values will be transformed their literal representation if a <code>format</code>
     * is present in their {@code @Preference} annotation. Null values will cause
     * the removal of the key in the {@code PreferencesNode}.</p>
     *
     * @param instance an object with fields/properties annotated with {@code @Preference}.
     */
    void save(@Nonnull Object instance);
}
