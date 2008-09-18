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

//import org.codehaus.groovy.griffon.commons.GriffonClassUtils as GCU
//import groovy.text.SimpleTemplateEngine

Ant.property(environment:"env")                             
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

includeTargets << new File ( "${griffonHome}/scripts/Package.groovy" )

target ('default': "Cleans a Griffon project") {
	clean()
}   

target ( clean: "Implementation of clean") {
    depends( cleanCompiledSources, cleanGriffonApp) //, cleanWarFile)
}

target ( cleanCompiledSources : "Cleans compiled Java and Groovy sources") {
	//def webInf = "${basedir}/web-app/WEB-INF"
	//Ant.delete(dir:"${webInf}/classes")		
	Ant.delete(dir:"${basedir}/test/reports", failonerror:false)		
	//Ant.delete(file:webXmlFile.absolutePath, failonerror:false)
	//Ant.delete(dir:"${webInf}/lib")
	Ant.delete(dir:classesDirPath)
	Ant.delete(dir:resourcesDirPath)
	Ant.delete(dir:testDirPath)
    Ant.delete(dir:config.griffon.jars.destDir)
}

target (cleanGriffonApp : "Cleans the Griffon application sources") {
	//def appDir = "${basedir}/web-app/WEB-INF/griffon-app"
	//Ant.delete(dir:appDir)
}

//target (cleanWarFile : "Cleans the deployable .war file") {
//    def fileName = griffonAppName
//    def version = Ant.antProject.properties.'app.version'
//    if (version) {
//        version = '-'+version
//    } else {
//        version = ''
//    }
//    warName = "${basedir}/${fileName}${version}.war"
//    Ant.delete(file:warName, failonerror:false)
//}
