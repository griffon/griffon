/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJQuitHandler;
import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;

import static griffon.util.GriffonNameUtils.capitalize;
import static java.util.Collections.singletonList;

/**
 * Handles Linux integration.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultMacOSXPlatformHandler extends DefaultPlatformHandler {
    @Override
    public void handle(@Nonnull GriffonApplication application) {
        super.handle(application);

        // use unified menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // set menu bar title
        String title = application.getConfiguration().getAsString("application.title", "Griffon");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", capitalize(title));


        boolean skipAbout = application.getConfiguration().getAsBoolean("osx.noabout", false);
        boolean skipPrefs = application.getConfiguration().getAsBoolean("osx.noprefs", false);
        boolean skipQuit = application.getConfiguration().getAsBoolean("osx.noquit", false);

        GriffonMacOSXSupport handler = new GriffonMacOSXSupport(application, skipQuit);
        if (!skipAbout) { MRJApplicationUtils.registerAboutHandler(handler); }
        if (!skipPrefs) { MRJApplicationUtils.registerPrefsHandler(handler); }
        MRJApplicationUtils.registerQuitHandler(handler);
    }

    private static class GriffonMacOSXSupport implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler {
        private final GriffonApplication application;
        private final boolean noquit;

        private GriffonMacOSXSupport(@Nonnull GriffonApplication application, boolean noquit) {
            this.application = application;
            this.noquit = noquit;
        }

        @Override
        public void handleAbout() {
            application.getEventRouter().publishEvent("OSXAbout", singletonList(application));
        }

        @Override
        public void handlePrefs() throws IllegalStateException {
            application.getEventRouter().publishEvent("OSXPrefs", singletonList(application));
        }

        @Override
        public void handleQuit() {
            if (noquit) {
                application.getEventRouter().publishEvent("OSXQuit", singletonList(application));
            } else {
                application.shutdown();
            }
        }
    }
}