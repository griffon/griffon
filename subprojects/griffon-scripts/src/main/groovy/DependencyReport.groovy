/*
 * Copyright 2004-2013 the original author or authors.
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

import groovy.xml.NamespaceBuilder
import org.codehaus.griffon.resolve.IvyDependencyManager

/**
 * Generates an Ivy dependency report for the current Griffon application 
 *
 * @author Graeme Rocher (Grails 1.2)
 */

target(name: 'dependencyReport', description: "Produces a dependency report for the current Griffon project", prehook: null, posthook: null) {
    // create ivy namespace
    ivy = NamespaceBuilder.newInstance(ant, 'antlib:org.apache.ivy.ant')

    String targetDir = "$projectTargetDir/dependency-report"
    ant.delete(dir: targetDir, failonerror: false)
    ant.mkdir(dir: targetDir)

    println "Obtaining dependency data..."
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    for (conf in IvyDependencyManager.ALL_CONFIGURATIONS) {
        dependencyManager.resolveDependencies(conf)
    }

    def conf = argsMap.params.join(', ').trim() ?: 'build, compile, runtime, test'
    ivy.report(organisation: 'org.codehaus.griffon.internal', module: griffonAppName, todir: targetDir, conf: conf)

    // Copy the runtime dependency report to 'index.html' for easy opening.
    ant.copy file: "${targetDir}/org.codehaus.griffon.internal-${griffonAppName}-runtime.html",
             tofile: "${targetDir}/index.html"

    event 'StatusFinal', ["Dependency report output to [${targetDir}/index.html"]
}
setDefaultTarget(dependencyReport)
