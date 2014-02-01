/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.plugins.preferences.PreferencesManager;
import griffon.plugins.preferences.PreferencesPersistor;
import griffon.plugins.preferences.persistors.SerializingPreferencesPersistor;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.prefs.DefaultPreferencesManager;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

/**
 * @author Andres Almiray
 */
@Named("preferences")
@ServiceProviderFor(Module.class)
public class PreferencesModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(PreferencesManager.class)
            .to(DefaultPreferencesManager.class)
            .asSingleton();

        bind(PreferencesPersistor.class)
            .to(SerializingPreferencesPersistor.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(PreferencesAddon.class)
            .asSingleton();
    }
}
