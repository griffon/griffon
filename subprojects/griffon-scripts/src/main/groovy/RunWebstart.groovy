/*
 * Copyright 2008-2012 the original author or authors.
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

import static griffon.util.GriffonNameUtils.quote
import static griffon.util.GriffonNameUtils.getNaturalName

/**
 * Created by IntelliJ IDEA.
 * @author Danno.Ferrin
 * Date: Aug 5, 2008
 * Time: 10:35:06 PM
 */

includeTargets << griffonScript('Package')
includeTargets << griffonScript('_GriffonBootstrap')

target(tweakConfig: ' tweaks for webstart') {
    configTweaks << { buildConfig.griffon.jars.sign = true }
}

target('runWebstart': "Runs the application with Java Webstart") {
    if (isPluginProject) {
        println "Cannot run application: project is a plugin!"
        exit(1)
    }
    doRunWebstart()
}

target('doRunWebstart': "Runs the application with Java Webstart") {
    depends(tweakConfig, createConfig, package_webstart)

    if ((buildConfig.griffon.jars.sign != [:]) && !buildConfig.griffon.jars.sign) {
        event("StatusFinal", ["Cannot run WebStart application because Webstart requires code signing.\n in BuildConfig.groovy griffon.jars.sign = false"])
        exit(1)
    }

    // calculate the needed jars
    File jardir = new File(ant.antProject.replaceProperties(buildConfig.griffon.jars.destDir))
    // launch event after jardir has been defined
    event("RunWebstartTweak", [])

    // setup the vm
    if (!binding.variables.webstartVM) {
        webstartVM = [System.properties['java.home'], 'bin', 'javaws'].join(File.separator)
    }

    def javaOpts = setupJavaOpts(false)
    debug("Running JVM options:")
    javaOpts.each { debug("  $it") }
    javaOpts = "-J" + javaOpts.join(" -J")

    def sysprops = []
    sysProperties.'griffon.application.name' = getNaturalName(griffonAppName)
    debug("System properties:")
    sysProperties.each { key, value ->
        debug("$key = $value")
        sysprops << "-D${key}=${quote(value)}"
    }
    sysprops = "-J" + sysprops.join(" -J")

    // TODO set proxy settings
    // start the processess
    debug("Executing $webstartVM $javaOpts $sysprops ${buildConfig.griffon.webstart.jnlp}")
    Process p = "$webstartVM $javaOpts $sysprops ${buildConfig.griffon.webstart.jnlp}".execute(null as String[], jardir)

    // pipe the output
    p.consumeProcessOutput(System.out, System.err)

    // wait for it.... wait for it...
    p.waitFor()
}

setDefaultTarget(runWebstart)
