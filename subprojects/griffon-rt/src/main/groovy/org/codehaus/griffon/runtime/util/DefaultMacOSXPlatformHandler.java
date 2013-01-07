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

package org.codehaus.griffon.runtime.util;

import griffon.core.GriffonApplication;
import griffon.util.ApplicationClassLoader;
import griffon.util.PlatformHandler;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.net.URL;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.ConfigUtils.getConfigValueAsString;
import static griffon.util.GriffonNameUtils.capitalize;

/**
 * Handles OSX' menubar, about, quit and preferences menus.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class DefaultMacOSXPlatformHandler implements PlatformHandler {
    private static Object macOSXHandler = null;

    public void handle(GriffonApplication app) {
        // use unified menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // set menu bar title
        String title = getConfigValueAsString(app.getConfig(), "application.title", "Griffon");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", capitalize(title));
        // we may want to have a more specific option like application.shortTitle, that is used first

        // exit handler
        if (macOSXHandler == null) {
            try {
                Binding bindings = new Binding();
                bindings.setVariable("app", app);
                bindings.setVariable("skipAbout", getConfigValueAsBoolean(app.getConfig(), "osx.noabout", false));
                bindings.setVariable("skipPrefs", getConfigValueAsBoolean(app.getConfig(), "osx.noprefs", false));
                bindings.setVariable("skipQuit", getConfigValueAsBoolean(app.getConfig(), "osx.noquit", false));

                GroovyShell shell = new GroovyShell(ApplicationClassLoader.get(), bindings);
                String resourceName = "META-INF/" + DefaultMacOSXPlatformHandler.class.getName() + ".txt";
                URL scriptUrl = ApplicationClassLoader.get().getResource(resourceName);
                macOSXHandler = shell.evaluate(DefaultGroovyMethods.getText(scriptUrl));
            } catch (Throwable t) {
                t.printStackTrace(System.out);
            }
        }
    }
}
