/*
 * Copyright 2008-2011 the original author or authors.
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
    runApp()
}

target('runApp': "Runs the application from the command line") {
    if(isPluginProject) {
        println "Cannot run application: project is a plugin!"
        exit(1)
    }
    depends(checkVersion, configureProxy, parseArguments, prepackage)

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
    if (buildConfig.griffon.memory?.min) {
        javaOpts << "-Xms$buildConfig.griffon.memory.min"
    }
    if (buildConfig.griffon.memory?.max) {
        javaOpts << "-Xmx$buildConfig.griffon.memory.max"
    }
    if (buildConfig.griffon.memory?.maxPermSize) {
        javaOpts << "-XX:maxPermSize=$buildConfig.griffon.memory.maxPermSize"
    }
    if (isMacOSX) {
        javaOpts << "-Xdock:name=$griffonAppName"
        javaOpts << "-Xdock:icon=${griffonHome}/media/griffon.icns"
    }

    debug("Running JVM options:")
    javaOpts.each{ debug("  $it") }

    def runtimeClasspath = runtimeJars.collect { f ->
        f.absolutePath - jardir.absolutePath - File.separator
    }.join(File.pathSeparator)

    runtimeClasspath = [i18nDir, resourcesDir, runtimeClasspath, classesDir, pluginClassesDir].join(File.pathSeparator)

    // start the process
    try {
        def cmd = [javaVM]
        // let's make sure no empty/null String is added
        javaOpts.each { s -> if(s) cmd << s }
        [proxySettings, '-classpath', runtimeClasspath, griffonApplicationClass].each { s -> if(s) cmd << s }
        args?.tokenize().each { s -> if(s) cmd << s }
        Process p = Runtime.runtime.exec(cmd as String[], null, jardir)

        // pipe the output
        p.consumeProcessOutput(System.out, System.err)
    
        // wait for it.... wait for it...
        p.waitFor()
    } finally {
// XXX -- NATIVE
        if(platformDir.exists()) {
            ant.delete(dir: platformDir)
        }
        if(platformDir2.exists()) {
            ant.delete(dir: platformDir2)
        }
// XXX -- NATIVE
    }
}
