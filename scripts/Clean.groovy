/*
 * Copyright 2004-2008 the original author or authors.
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
 * @author Graeme Rocher
 *
 * @since 0.4
 */

defaultTarget("Cleans a Griffon project") {
    clean()
}

includeTargets << griffonScript("Init" )
includeTargets << griffonScript("Package" )

target ( clean: "Implementation of clean") {
    event("CleanStart", [])
    depends( classpath, cleanCompiledSources, cleanTestReports)
    event("CleanEnd", [])
}

target ( cleanCompiledSources : "Cleans compiled Java and Groovy sources") {
    Ant.delete(dir:"${basedir}/test/reports", failonerror:false)
    Ant.delete(dir:classesDirPath)
    Ant.delete(dir:resourcesDirPath)
    Ant.delete(dir:testDirPath)

    if(configFile.exists()) {
        config = configSlurper.parse(configFile.toURL())
        config.setConfigFile(configFile.toURL())
    }
    Ant.delete(dir:Ant.antProject.replaceProperties(config.griffon.jars.destDir), includes:'**/*.*')
}

target ( cleanTestReports:"Cleans the test reports") {
	Ant.delete(dir:"${basedir}/test/reports", failonerror:false)
}
