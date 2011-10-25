/*
 * Copyright 2009-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.util

import griffon.util.PlatformHandler
import static griffon.util.GriffonNameUtils.capitalize
import griffon.core.GriffonApplication

/**
 * Handles OSX' menubar, about, quit and preferences menus.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
class DefaultMacOSXPlatformHandler implements PlatformHandler {
    private static macOSXHandler = null
    void handle(GriffonApplication app) {
        // use unified menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true")

        // set menu bar title
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", capitalize(app.config.application?.title ?: 'Griffon'))
        // we may want to have a more specific option like application.shortTitle, that is used first

        // exit handler
        if (!macOSXHandler) {
            try {
                Binding bindings = new Binding()
                bindings.app = app
                bindings.skipAbout = app.config.osx.noabout ?: false
                bindings.skipPrefs = app.config.osx.noprefs ?: false
                bindings.skipQuit = app.config.osx.noquit ?: false

                GroovyShell shell = new GroovyShell(DefaultMacOSXPlatformHandler.class.classLoader, bindings)
                macOSXHandler = shell.evaluate('''
                        package griffon.util

                        import griffon.core.GriffonApplication
                        import com.apple.mrj.*

                        class GriffonMacOsSupport implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler {
                            final GriffonApplication app
                            final boolean noquit

                            GriffonMacOsSupport(GriffonApplication app, boolean noquit) {
                                this.app = app
                                this.noquit = noquit
                            }

                            public void handleAbout() {
                                app.event('OSXAbout', [app])
                            }

                            public void handlePrefs() throws IllegalStateException {
                                app.event('OSXPrefs', [app])
                            }

                            public void handleQuit() throws IllegalStateException {
                                noquit? app.event('OSXQuit', [app]) : app.shutdown()
                            }
                        }

                        def handler = new GriffonMacOsSupport(app, skipQuit)
                        if(!skipAbout) MRJApplicationUtils.registerAboutHandler(handler)
                        if(!skipPrefs) MRJApplicationUtils.registerPrefsHandler(handler)
                        MRJApplicationUtils.registerQuitHandler(handler)

                        return handler
                    ''')
            } catch (Throwable t) {
                t.printStackTrace(System.out)
            }
        }
    }
}
