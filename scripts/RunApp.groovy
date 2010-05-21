/*
 * Copyright 2008-2010 the original author or authors.
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

import static griffon.util.GriffonApplicationUtils.isMacOSX

includeTargets << griffonScript("Package")
includeTargets << griffonScript("_GriffonBootstrap")
includeTargets << griffonScript("_PackagePlugins" )

target('default': "Runs the application from the command line") {
    depends(checkVersion, configureProxy, parseArguments, prepackage)

    // calculate the needed jars
    File jardir = new File(ant.antProject.replaceProperties(config.griffon.jars.destDir))
    // launch event after jardir has been defined

    event("RunAppStart",[])
    runtimeJars = setupRuntimeJars()

    // setup the vm
    if (!binding.variables.javaVM) {
        javaVM = [System.properties['java.home'], 'bin', 'java'].join(File.separator)
    }

    def javaOpts = setupJavaOpts(true)
    if (argsMap.containsKey('debug')) {
        def portNum = argsMap.debugPort?:'18290'  //default is 'Gr' in ASCII
        def addr = argsMap.debugAddr?:'127.0.0.1'
        def debugSocket = ''
        if (portNum =~ /\d+/) {
            if (addr == '127.0.0.1') {
                debugSocket = ",address=$portNum"
            } else {
                debugSocket = ",address=$addr:$portNum"
            }
        }
        javaOpts << "-Xrunjdwp:transport=dt_socket$debugSocket,suspend=n,server=y"
    }
    if (config.griffon.memory?.min) {
        javaOpts << "-Xms$config.griffon.memory.min"
    }
    if (config.griffon.memory?.max) {
        javaOpts << "-Xmx$config.griffon.memory.max"
    }
    if (config.griffon.memory?.maxPermSize) {
        javaOpts << "-XX:maxPermSize=$config.griffon.memory.maxPermSize"
    }
    if (isMacOSX) {
        javaOpts << "-Xdock:name=$griffonAppName"
        javaOpts << "-Xdock:icon=${griffonHome}/bin/griffon.icns"
    }

    def runtimeClasspath = runtimeJars.collect { f ->
        f.absolutePath - jardir.absolutePath - File.separator
    }.join(File.pathSeparator)

    runtimeClasspath = [classesDir, i18nDir, resourcesDir, runtimeClasspath].join(File.pathSeparator)

    // start the processess
    try {
        def cmd = [javaVM]
        // let's make sure no empty/null String is added
        javaOpts.each { s -> if(s) cmd << s }
        [proxySettings, '-classpath', runtimeClasspath, griffonApplicationClass].each { s -> if(s) cmd << s }
        Process p = Runtime.runtime.exec(cmd as String[], new String[0], jardir)

        // pipe the output
        p.consumeProcessOutput(System.out, System.err)
    
        // wait for it.... wait for it...
        p.waitFor()
        event("RunAppEnd",[])
    } finally {
// XXX -- NATIVE
        if(platformDir.exists()) {
            ant.delete(dir: platformDir)
        }
// XXX -- NATIVE
    }
}
