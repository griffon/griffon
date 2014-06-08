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
package org.codehaus.griffon.runtime.prefs;

import griffon.core.ApplicationEvent;
import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;
import griffon.plugins.preferences.PreferencesManager;
import griffon.plugins.preferences.PreferencesPersistor;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileNotFoundException;
import java.io.IOException;

import static griffon.core.GriffonExceptionHandler.sanitize;

/**
 * @author Andres Almiray
 */
@Named("preferences")
public class PreferencesAddon extends AbstractGriffonAddon {
    @Inject
    private PreferencesManager preferencesManager;

    @Inject
    private PreferencesPersistor preferencesPersistor;

    public void init(@Nonnull GriffonApplication application) {
        boolean preferencesWereRead = false;
        try {
            preferencesPersistor.read(preferencesManager);
            preferencesWereRead = true;
        } catch (FileNotFoundException fnfe) {
            // most likely means preferences have not been initialized yet
            // let it continue
            preferencesWereRead = true;
        } catch (IOException e) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Cannot read preferences", sanitize(e));
            }
        }

        if (preferencesWereRead) {
            application.getEventRouter().addEventListener(ApplicationEvent.SHUTDOWN_START.getName(), new CallableWithArgs<Void>() {
                @Override
                @Nullable
                public Void call(@Nullable Object... args) {
                    try {
                        preferencesPersistor.write(preferencesManager);
                    } catch (IOException e) {
                        if (getLog().isWarnEnabled()) {
                            getLog().warn("Cannot persist preferences", sanitize(e));
                        }
                    }
                    return null;
                }
            });
        }
    }
}
