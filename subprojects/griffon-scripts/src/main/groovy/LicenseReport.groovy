/*
 * Copyright 2013 the original author or authors.
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

import org.codehaus.griffon.resolve.IvyDependencyManager
import groovy.xml.MarkupBuilder
import griffon.util.Metadata

/**
 * @author Andres Almiray
 */

APACHE_LICENSE = 'Apache Software License 2.0'
MIT_LICENSE = 'MIT License'
KNOWN_DEPENDENCIES = [
    'groovy-all':     [APACHE_LICENSE],
    'griffon-rt':     [APACHE_LICENSE],
    'slf4j-api':      [MIT_LICENSE],
    'slf4j-log4j12':  [MIT_LICENSE],
    'jcl-over-slf4j': [MIT_LICENSE],
    'jul-to-slf4j':   [MIT_LICENSE],
    'log4j':          [APACHE_LICENSE],
]

target(name: 'licenseReport', description: "Produces a license report of the project's runtime dependencies", prehook: null, posthook: null) {
    String targetDir = "$projectTargetDir/license-report"
    ant.delete(dir: targetDir, failonerror: false)
    ant.mkdir(dir: targetDir)

    Map plugins = griffonSettings.pluginSettings.plugins

    println "Obtaining dependency data...\n"
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    def resolveReport = dependencyManager.resolveDependencies(IvyDependencyManager.RUNTIME_CONFIGURATION)

    Map resolvedDependencies = [:]
    resolveReport.dependencies.each { dependency ->
        String dependencyName = dependency.resolvedId.moduleId.name
        List licenses = (dependency.md.licenses.name ?: KNOWN_DEPENDENCIES[dependencyName]) ?: []
        if (dependencyName =~ /griffon-(.+)-runtime/ ) {
            String pluginName = dependencyName - 'griffon-' - '-runtime'
            licenses = [plugins[pluginName].release.artifact.license]
        }
        resolvedDependencies[dependencyName] = [
            version: dependency.resolvedId.revision,
            licenses: licenses
        ]
    }

    int namePadding = 0
    int versionPadding = 0

    resolvedDependencies.each { dependencyName, props ->
        if (dependencyName.size() > namePadding) namePadding = dependencyName.size()
        if (props.version.size() > versionPadding) versionPadding = props.version.size()
    }

    namePadding += 1
    versionPadding += 1
    versionPadding = versionPadding > 8 ? versionPadding : 8

    Date timestamp = new Date()
    String textReport = """
        Created on: $timestamp
        Name: ${Metadata.current.getApplicationName()}
        Version: ${Metadata.current.getApplicationVersion()}
        Griffon: ${Metadata.current.getGriffonVersion()}
    """.stripIndent(8).trim()
    textReport += '\n\n'
    textReport += 'Dependency'.padRight(namePadding, ' ')
    textReport += 'Version'.padRight(versionPadding, ' ')
    textReport += 'License\n'
    textReport += ('-' * 80) + '\n'
    resolvedDependencies.sort().each { dependencyName, props ->
        textReport += dependencyName.padRight(namePadding, ' ')
        textReport += props.version.padRight(versionPadding, ' ')
        boolean first = true
        for (String license : props.licenses) {
            if (first) {
                textReport += license + '\n'
                first = false
            } else {
                textReport += (' ' * (namePadding+versionPadding)) + license + '\n'
            }
        }
        if (!props.licenses) textReport += '**UNKNOWN**\n'
    }
    new File(targetDir, 'licenses.txt').text = textReport
    println textReport

    def sw = new StringWriter()
    MarkupBuilder builder = new MarkupBuilder(sw)
    builder.'license-report' {
        'created-on'(timestamp)
        application(name: Metadata.current.getApplicationName(),
            version: Metadata.current.getApplicationVersion(),
            'griffon-version': Metadata.current.getGriffonVersion())
        dependencies {
            resolvedDependencies.sort().each { dependencyName, props ->
                dependency(name: dependencyName, version: props.version) {
                    licenses {
                        props.licenses.each { l -> license(l) }
                        if (!props.licenses) license('**UNKNOWN**')
                    }
                }
            }
        }
    }
    new File(targetDir, 'licenses.xml').text = sw

    event 'StatusFinal', ["Dependency report output to [${targetDir}"]
}
setDefaultTarget(licenseReport)
