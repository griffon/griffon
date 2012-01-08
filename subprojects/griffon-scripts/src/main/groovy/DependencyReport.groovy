/*
 * Copyright 2004-2012 the original author or authors.
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

target(dependencyReport: "Produces a dependency report for the current Griffon application") {
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

    def conf = args.trim().replace('\n' as char, ',' as char) ?: 'build, compile, provided, runtime, test'
    def confs = []
    for (type in conf.split(',')) {
        type = type.trim()
        try {
            if (griffonSettings."${type}Dependencies") confs << type
        } catch (x) { /* ignore */ }
    }

    if (confs) {
        ivy.report(organisation: 'org.codehaus.griffon.internal', module: griffonAppName, todir: targetDir, conf: confs.join(', '))
        println "Dependency report output to [$targetDir]"
    } else {
        println "Can't generate dependency report for configuration${conf.size() ? 's' : ''} $conf"
    }
}
setDefaultTarget(dependencyReport)
