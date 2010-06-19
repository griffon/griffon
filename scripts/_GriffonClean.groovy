/*
 * Copyright 2004-2010 the original author or authors.
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
 * Gant script that cleans a Griffon project
 *
 * @author Graeme Rocher (Grails 0.4)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_clean_called")) return
_griffon_clean_called = true


includeTargets << griffonScript("_GriffonEvents")

target ( cleanAll: "Cleans a Griffon project" ) {
    clean()
    cleanTestReports()
}

target ( clean: "Implementation of clean" ) {
    depends(classpath, cleanCompiledSources, cleanPackaging)
}

target ( cleanCompiledSources: "Cleans compiled Java and Groovy sources" ) {
    ant.delete(dir:classesDirPath)
    ant.delete(dir:pluginClassesDirPath, failonerror:false)
    ant.delete(dir:resourcesDirPath)
    ant.delete(dir:testDirPath)
    ant.delete(dir:testResourcesDirPath)
    ant.delete(dir:projectTargetDir)
}

target ( cleanTestReports: "Cleans the test reports" ) {
    // Delete all reports *except* TEST-TestSuites.xml which we need
    // for the "--rerun" option to work.
    ant.delete(failonerror:false, includeemptydirs: true) {
        fileset(dir:griffonSettings.testReportsDir.path) {
            include(name: "**/*")
            exclude(name: "TESTS-TestSuites.xml")
        }
    }
}

target ( cleanPackaging : "Cleans the distribtion directories" ) {
    if(buildConfigFile.exists()) {
        def cfg = configSlurper.parse(buildConfigFile.toURL())
        cfg.setConfigFile(buildConfigFile.toURL())

        def distDir =  cfg.griffon.dist.dir ?: "${basedir}/dist"
        def destDir =  cfg.griffon.jars.destDir

        ant.delete(dir:ant.antProject.replaceProperties(distDir))
        ant.delete(dir:ant.antProject.replaceProperties(destDir))
        //todo per package directories?
    }
    if(isPluginProject) {
        ant.delete(dir: "${basedir}/docs")
    }
}
