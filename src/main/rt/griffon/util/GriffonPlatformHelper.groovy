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

package griffon.util

import griffon.core.GriffonApplication

/**
 * Tweaks an application for an specific platform.
 *
 * @author Danno Ferrin
 */
class GriffonPlatformHelper {
    static void tweakForNativePlatform(GriffonApplication app) {
        if (GriffonApplicationUtils.isMacOSX) {
            tweakForMacOSX(app)
        }
    }

    static macOSXHandler = null

    static void tweakForMacOSX(GriffonApplication application) {

        // do all this a the end of bootstrap
        application.addApplicationEventListener("BootstrapEnd", {GriffonApplication app -> 
            // use unified menu bar
            System.setProperty("apple.laf.useScreenMenuBar", "true")

            // set menu bar title
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", GriffonNameUtils.capitalize(app.config.application?.title ?: 'Griffon'))
            // we may want to have a more specific option like application.shortTitle, that is used first

            // exit handler
            if (!macOSXHandler) {
                try {
                    Binding bindings = new Binding()
                    bindings.app = app
                    bindings.skipPrefs = app.config.osx.noprefs ?: false

                    GroovyShell shell = new GroovyShell(GriffonPlatformHelper.class.classLoader, bindings)
                    macOSXHandler = shell.evaluate('''
                        package griffon.util

                        import griffon.core.GriffonApplication
                        import com.apple.mrj.*

                        class GriffonMacOsSupport implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler {
                            final GriffonApplication app

                            GriffonMacOsSupport(GriffonApplication app) {
                                this.app = app
                            }

                            public void handleAbout() {
                                app.event('OSXAbout', [app])
                            }

                            public void handlePrefs() throws IllegalStateException {
                                app.event('OSXPrefs', [app])
                            }

                            public void handleQuit() throws IllegalStateException {
                                app.event('OSXQuit', [app])
                            }
                        }

                        def handler = new GriffonMacOsSupport(app)
                        MRJApplicationUtils.registerAboutHandler(handler)
                        MRJApplicationUtils.registerQuitHandler(handler)
                        if(!skipPrefs) MRJApplicationUtils.registerPrefsHandler(handler)

                        return handler
                    ''')
                } catch (Throwable t) {
                    t.printStackTrace(System.out)
                }
            }
        })
    }
}
