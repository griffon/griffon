/*
 * Copyright 2004-2014 the original author or authors.
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

/**
 * Gant script that loads the Griffon console
 *
 * @author Graeme Rocher (Grails 0.4)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_console_called')) return
_console_called = true

includeTargets << griffonScript('_GriffonBootstrap')

target(name: 'console', description: "Runs an embedded application in a Groovy console", prehook: null, posthook: null) {
    try {
        def console = createConsole()
        console.run()
        while (console.consoleControllers) { sleep(3500) }
    } catch (Exception e) {
        event("StatusFinal", ["Error starting console: ${e.message}"])
    }
}
setDefaultTarget(console)

createConsole = {
    if (!isPluginProject && !isArchetypeProject) bootstrap()
    def b = new Binding()
    if (!isPluginProject && !isArchetypeProject) b.app = griffonApp
    def cl = griffonApp?.class?.classLoader ?: classLoader

    def console = new groovy.ui.Console(cl, b)

    return console
}
