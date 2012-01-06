/*
 * Copyright 2004-2011 the original author or authors.
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

/**
 * Gant script that handles the installation of Griffon plugins
 *
 * @author Graeme Rocher (Grails 1.1)
 */

target(installDependency: "Install a JAR dependency into a project") {
    def dep
    if (argsMap.params) {
        dep = argsMap.params[0].toString()
    }
    else if (argsMap.group && argsMap.name && argsMap.version) {
        dep = argsMap
    }

    if (dep) {
        def manager = new IvyDependencyManager(griffonAppName, griffonAppVersion, griffonSettings)
        manager.parseDependencies {
            repositories {
                griffonPlugins()
                griffonHome()
                mavenLocal()
                mavenCentral()
                mavenRepo "http://snapshots.repository.codehaus.org"
                mavenRepo "http://repository.codehaus.org"
                mavenRepo "http://download.java.net/maven/2/"
                mavenRepo "http://repository.jboss.com/maven2/"
                mavenRepo "http://repository.springsource.com/maven/bundles/release"
                mavenRepo "http://repository.springsource.com/maven/bundles/external"
                mavenRepo "http://repository.springsource.com/maven/bundles/milestone"
                if (argsMap.repository) {
                    mavenRepo argsMap.repository.toString()
                }
            }

            compile dep
        }

        println "Installing dependency '${dep}'. Please wait.."
        def report = manager.resolveDependencies()
        if (report.hasError()) {
            println """
There was an error resolving the dependency '${dep}'.
This could be because you have passed an invalid dependency name or because the dependency was not found in one of the default repositories.
Try passing a valid Maven repository with the --repository argument."""
            exit 1
        }
        else {
            for (File file in report.allArtifactsReports.localFile) {
                if (argsMap.dir) {
                    ant.copy(file: file, todir: argsMap.dir)
                    println "Installed dependency '${dep}' to location '${argsMap.dir}'"
                }
                else {
                    println "Installed dependency '${dep}'."
                }
            }

        }
    }
}

setDefaultTarget(installDependency)