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
package org.codehaus.griffon.runtime.prefs;

import griffon.core.GriffonApplication;
import griffon.plugins.preferences.Preferences;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Andres Almiray
 */
public class DefaultPreferencesManager extends AbstractPreferencesManager {
    private final Preferences preferences;

    @Inject
    public DefaultPreferencesManager(@Nonnull GriffonApplication application) {
        super(application);
        preferences = new DefaultPreferences();
        init();
    }

    @Nonnull
    public Preferences getPreferences() {
        return preferences;
    }
}
