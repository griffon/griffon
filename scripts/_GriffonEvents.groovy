/*
 * Copyright 2004-2005 the original author or authors.
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

import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.codehaus.griffon.util.GriffonUtil

/**
 * Gant script containing the Griffon build event system.
 *
 * @author Peter Ledbrook
 *
 * @since 1.1
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_events_called")) return
_griffon_events_called = true

includeTargets << griffonScript("_GriffonClasspath")

// Class loader to use for loading events scripts.
eventsClassLoader = new GroovyClassLoader(classLoader)

// A map of events to lists of handlers. The handlers provided by plugin
// and application Events scripts are put in here.
globalEventHooks = [
        StatusFinal: [ {message -> println message } ],
        StatusUpdate: [ {message -> println message + ' ...' } ],
        StatusError: [ {message -> System.err.println message } ],
        CreatedArtefact: [ {artefactType, artefactName -> println "Created $artefactType for $artefactName" } ]
]

// Send a scripting event notification to any and all event hooks in plugins/user scripts
hooksLoaded = false

event = {String name, def args ->
    if (!hooksLoaded) {
        hooksLoaded = true

        // Set up the classpath for the event hooks.
        classpath()

        // Now load them.
        loadEventHooks()

        // Give scripts a chance to modify classpath
        event('SetClasspath', [classLoader])
    }

    globalEventHooks[name].each() { handler ->
        try {
            handler.delegate = this
            handler(* args)
        }
        catch (MissingPropertyException e) {
        }
    }
}

loadEventHooks = {
    // Look for user script
    def f = findEventsScript(new File("${griffonSettings.griffonWorkDir}/scripts"))
    if (f != null) {
        println "Found user events script"
        loadEventScript(f)
    }

    // Look for app-supplied scripts
    f = findEventsScript(new File(basedir, "scripts"))
    if (f != null) {
        println "Found application events script"
        loadEventScript(f)
    }

    // Look for plugin-supplied scripts
    def pluginDirs = GriffonPluginUtils.getPluginBaseDirectories()
    pluginDirs.each { String dir ->
        def pluginsDir = new File(dir)
        if (pluginsDir.exists()) {
            pluginsDir.eachDir() {
                f = findEventsScript(new File(it, "scripts"))
                if (f != null) {
                    println "Found events script in plugin ${it.name}"
                    loadEventScript(f)
                }
            }
        }
    }
}

/**
 * Locates an events script in the given directory. It first looks for
 * "_Events.groovy", but if that doesn't exist, it looks for "Events.groovy"
 * instead. If it finds one with the latter name, it prints out a deprecation
 * warning.
 */
File findEventsScript(File dir) {
    def f = new File(dir, "_Events.groovy")
    if (!f.exists()) {
        f = new File(dir, "Events.groovy")
        if (f.exists()) {
            println "Use of 'Events.groovy' is DEPRECATED.  Please rename to '_Events.groovy'."
        }
    }

    return f.exists() ? f : null
}

loadEventScript = {File theFile ->
    try {
        // Load up the given events script.
        def script = eventsClassLoader.parseClass(theFile).newInstance()

        // Pass the global binding to the script.
        script.binding = getBinding()

        // Execute the script.
        script.run()

        // The binding should now contain the event hooks provided by
        // script, so we remove them and add them to the 'eventHooks'
        // map.
        def entriesToRemove = []
        script.binding.variables.each {key, value ->
            // Check whether this binding variable is an event hook.
            def m = key =~ /event([A-Z]\w*)/
            if (m.matches()) {
                // It is, so add the hook to the global map of event
                // hooks.
                def eventName = m[0][1]
                def hooks = globalEventHooks[eventName]
                if (hooks == null) {
                    hooks = []
                    globalEventHooks[eventName] = hooks
                }

                hooks << value

                // This entry should now be removed from the global
                // binding.
                entriesToRemove << key
            }
        }

        // Remove the event hooks from the global binding.
        entriesToRemove.each { script.binding.variables.remove(it) }
    } catch (Throwable t) {
        println "Unable to load event script $theFile: ${t.message}"
        GriffonUtil.deepSanitize(t)
        t.printStackTrace()
    }
}
