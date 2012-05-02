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

/**
 * Created by IntelliJ IDEA.
 * @author Danno.Ferrin
 * Date: Aug 5, 2008
 * Time: 10:35:06 PM
 */

import griffon.util.GriffonNameUtils
import static griffon.util.GriffonApplicationUtils.isMacOSX

includeTargets << griffonScript('Package')
includeTargets << griffonScript('_GriffonBootstrap')

target('runApp': "Runs the application from the command line") {
    if (isPluginProject) {
        println "Cannot run application: project is a plugin!"
        exit(1)
    }
    doRunApp()
}

target('doRunApp': "Runs the application from the command line") {
    depends(prepackage)

    // calculate the needed jars
    File jardir = new File(ant.antProject.replaceProperties(buildConfig.griffon.jars.destDir))
    // launch event after jardir has been defined
    event('RunAppTweak', [])

    runtimeJars = setupRuntimeJars()

    // setup the vm
    if (!binding.variables.javaVM) {
        def javaHome = ant.antProject.properties."environment.JAVA_HOME"
        javaVM = [javaHome, 'bin', 'java'].join(File.separator)
    }

    def javaOpts = setupJavaOpts(true)
    if (argsMap.containsKey('debug')) {
        argsMap['debug-port'] = argsMap.debugPort
        argsMap['debug-addr'] = argsMap.debugAddr
        def portNum = argsMap['debug-port'] ?: '18290'  //default is 'Gr' in ASCII
        def addr = argsMap['debug-addr'] ?: '127.0.0.1'
        def debugSocket = ''
        if (portNum =~ /\d+/) {
            if (addr == '127.0.0.1') {
                debugSocket = ",address=$portNum"
            } else {
                debugSocket = ",address=$addr:$portNum"
            }
        }
		def debugSuspend = 'n'
		if (argsMap.containsKey('debugSuspend')) {
			argsMap['debug-suspend'] = argsMap.debugSuspend
            debugSuspend = argsMap.debugSuspend
			if (argsMap.debugSuspend instanceof Boolean) {
				debugSuspend = argsMap.debugSuspend ? 'y' : 'n'
			} else if (argsMap.debugSuspend == "true") {
				debugSuspend = 'y'
			} else if (argsMap.debugSuspend == "false") {
				debugSuspend = 'n'
			} else {
				if (! (argsMap.debugSuspend ==~ /(?i)[yn]/)) {
					println("Unrecognized value in '--debugSuspend=${argsMap.debugSuspend}' : must be 'y' or 'n'.")
					println("   Forcing to 'n', debugged process will not suspend waiting for a connection at start.")
				    debugSuspend = 'n'
				} 
			}
            debugSuspend = debugSuspend.toLowerCase()
        }
       javaOpts << "-Xrunjdwp:transport=dt_socket$debugSocket,suspend=$debugSuspend,server=y"
    }
    if (buildConfig.griffon.memory?.min) {
        javaOpts << "-Xms$buildConfig.griffon.memory.min"
    }
    if (buildConfig.griffon.memory?.max) {
        javaOpts << "-Xmx$buildConfig.griffon.memory.max"
    }
    if (buildConfig.griffon.memory?.minPermSize && buildConfig.griffon.memory?.maxPermSize) {
        javaOpts << "-XX:MaxPermSize=$buildConfig.griffon.memory.maxPermSize"
        javaOpts << "-XX:PermSize=$buildConfig.griffon.memory.minPermSize"
    }
    if (isMacOSX) {
        javaOpts << "-Xdock:name=${GriffonNameUtils.capitalize(griffonAppName)}"
        javaOpts << "-Xdock:icon=${resolveApplicationIcnsFile().absolutePath}"
    }

    debug("Running JVM options:")
    javaOpts.each { debug("  $it") }

    def runtimeClasspath = runtimeJars.collect { f ->
        f.absolutePath.startsWith(jardir.absolutePath) ? f.absolutePath - jardir.absolutePath - File.separator : f
    }.join(File.pathSeparator)

    runtimeClasspath = [i18nDir, resourcesDir, runtimeClasspath, projectMainClassesDir].join(File.pathSeparator)

    event 'StatusUpdate', ['Launching application']
    // start the process
    try {
        def cmd = [javaVM]
        // let's make sure no empty/null String is added
        javaOpts.each { s -> if (s) cmd << s }
        [proxySettings, '-classpath', runtimeClasspath, griffonApplicationClass].each { s -> if (s) cmd << s }
        argsMap.params.each { s -> cmd << s.trim() }
        debug("Executing ${cmd.join(' ')}")
        Process p = Runtime.runtime.exec(cmd as String[], null, jardir)

        // pipe the output
        p.consumeProcessOutput(System.out, System.err)

        // wait for it.... wait for it...
        p.waitFor()
    } finally {
// XXX -- NATIVE
        if (platformDir.exists()) {
            ant.delete(dir: platformDir)
        }
        if (platformDir2.exists()) {
            ant.delete(dir: platformDir2)
        }
// XXX -- NATIVE
    }
}

setDefaultTarget(runApp)