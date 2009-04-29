/*
 * Copyright 2004-2005 the original author or authors.
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

includeTargets << griffonScript("_GriffonEvents")

target ( cleanAll: "Cleans a Griffon project" ) {
	clean()
	cleanTestReports()
}

target ( clean: "Implementation of clean" ) {
    event("CleanStart", [])
    depends(classpath, cleanCompiledSources/*, cleanWarFile*/)
    event("CleanEnd", [])
}

target ( cleanCompiledSources: "Cleans compiled Java and Groovy sources" ) {
    //def webInf = "${basedir}/web-app/WEB-INF"
    //ant.delete(dir:"${webInf}/classes")
    //ant.delete(file:webXmlFile.absolutePath, failonerror:false)
    //ant.delete(dir:"${webInf}/lib")
    ant.delete(dir:classesDirPath)
    ant.delete(dir:resourcesDirPath)
    ant.delete(dir:testDirPath)

    if(configFile.exists()) {
        config = configSlurper.parse(configFile.toURL())
        config.setConfigFile(configFile.toURL())
    }
    ant.delete(dir:ant.antProject.replaceProperties(config.griffon.jars.destDir), includes:'**/*.*')

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

//target ( cleanWarFile: "Cleans the deployable .war file" ) {
//    if (config.griffon.war.destFile) {
//        warName = config.griffon.war.destFile
//    }
//    else {
//        def fileName = griffonAppName
//        def version = metadata.'app.version'
//        if (version) {
//            fileName += "-$version"
//        }
//        warName = "${basedir}/${fileName}.war"
//    }
//
//    ant.delete(file:warName, failonerror:false)
//}
