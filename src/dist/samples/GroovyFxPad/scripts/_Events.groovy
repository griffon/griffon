/*
 * Copyright 2009-2012 the original author or authors.
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
 * @author Dean Iverson
 * @author Andres Almiray
 */
//eventClasspathEnd = {
eventCompileSourcesStart = {
    final jfxrtFile = new File(ant.project.properties['environment.JAVAFX_HOME'], 'rt/lib/jfxrt.jar')
    final jfxrtJarPath = ant.path {
        pathElement(location: jfxrtFile.absolutePath)
    }

    ant.project.references['griffon.compile.classpath'].append(jfxrtJarPath)
    ant.project.references['griffon.test.classpath'].append(jfxrtJarPath)

    griffonSettings.updateDependenciesFor 'compile', [jfxrtFile]
    griffonSettings.updateDependenciesFor 'test', [jfxrtFile]
}

/**
* Add the actual JavaFX runtime jar from its real location so that it can find
* it's hard-coded native library dependencies.
*/
eventRunAppTweak = { message ->
    def originalSetupRuntimeJars = setupRuntimeJars
    setupRuntimeJars = {
        def runtimeJars = []

        if (originalSetupRuntimeJars)
            runtimeJars = originalSetupRuntimeJars()

        def javafxrt = new File("${System.getenv('JAVAFX_HOME')}/rt/lib/jfxrt.jar")
        if (javafxrt)
            runtimeJars << javafxrt

        return runtimeJars
    }
}
