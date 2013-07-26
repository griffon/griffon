/*
 * Copyright 2008-2013 the original author or authors.
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

import static griffon.util.GriffonApplicationUtils.isWindows
import static griffon.util.GriffonNameUtils.quote

/**
 * Created by IntelliJ IDEA.
 * @author Danno.Ferrin
 * Date: Aug 5, 2008
 * Time: 10:35:06 PM
 */

includeTargets << griffonScript('Package')
includeTargets << griffonScript('_GriffonBootstrap')

target(name: 'tweakConfig', description: ' tweaks for webstart', prehook: null, posthook: null) {
    configTweaks << { buildConfig.griffon.jars.sign = true }
}

target(name: 'runApplet', description: "Runs the applet from Java WebStart", prehook: null, posthook: null) {
    if (isPluginProject) {
        println "Cannot run application: project is a plugin!"
        exit(1)
    }
    doRunApplet()
}

target(name: 'doRunApplet', description: "Runs the applet from Java WebStart", prehook: null, posthook: null) {
    depends(tweakConfig, createConfig, package_applet)

    // calculate the needed jars
    File jardir = new File(ant.antProject.replaceProperties(buildConfig.griffon.jars.destDir))
    // launch event after jardir has been defined
    event("RunAppletTweak", [])

    // setup the vm
    if (!binding.variables.webstartVM) {
        webstartVM = [System.properties['java.home'], 'bin', 'javaws'].join(File.separator)
    }

    if (!(webstartVM instanceof File)) webstartVM = new File(webstartVM.toString())
    if (!webstartVM.exists() && !isWindows) webstartVM = new File(['', 'usr', 'bin', 'javaws'].join(File.separator))

    def javaOpts = setupJavaOpts(false)
    debug("Running JVM options:")
    javaOpts = javaOpts.collect { debug("  $it"); "-J$it" }

    def sysprops = []
    sysProperties.'griffon.application.name' = griffonAppName
    debug("System properties:")
    sysProperties.each { key, value ->
        if (null == value) return
        debug("  -D$key=${quote(value)}")
        sysprops << "-J-D${key}=${quote(value)}"
    }

    List<String> cmd = [webstartVM.toString()] + javaOpts + sysprops + buildConfig.griffon.webstart.jnlp

    // TODO set proxy settings
    // start the processess
    debug("Executing ${cmd.join(' ')}")
    ProcessBuilder pb = new ProcessBuilder(* cmd)
    pb.directory(jardir)
    Process p = pb.start()

    // pipe the output
    p.consumeProcessOutput(System.out, System.err)

    // wait for it.... wait for it...
    p.waitFor()
}

setDefaultTarget(runApplet)
