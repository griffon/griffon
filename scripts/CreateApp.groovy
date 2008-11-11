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
 * Gant script that handles the creation of Griffon applications
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

import org.codehaus.griffon.commons.GriffonClassUtils as GCU

defaultTarget("Creates a Griffon project, including the necessary directory structure and commons files")  {
   createApp()
}

includeTargets << griffonScript("CreateMvc" )
includeTargets << griffonScript("Package")

target( createApp: "The implementation target")  {
    depends( appName, createStructure, updateAppProperties, init )

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    griffonUnpack(dest: basedir, src: "griffon-app-files.jar")

    createIDESupportFiles()
    ant.replace(dir:"${basedir}/griffon-app/conf", includes:"**/*.*", standardGriffonFilters)

    classpath()
    //loadPlugins()
	//generateWebXml()

    // Create a message bundle to get the user started.
    Ant.touch(file: "${basedir}/griffon-app/i18n/messages.properties")

    args = griffonAppName//.replaceAll( /\s/, '.' ).toLowerCase()
    createMVC()

	// Set the default version number for the application
    Ant.propertyfile(file:"${basedir}/application.properties") {
        entry(key:"app.version", value:"0.1")
    }

    event("StatusFinal", ["Created Griffon Application at $basedir"])
}

target( createIDESupportFiles: "Creates the IDE support files (Eclipse, TextMate etc.), changing file names and replacing tokens in files where appropriate.") {
    griffonUnpack(dest: basedir, src: "griffon-ide-files.jar")

    ant.move(file: "${basedir}/.launch", tofile: "${basedir}/${griffonAppName}.launch", overwrite: true)
    ant.move(file: "${basedir}/project.tmproj", tofile: "${basedir}/${griffonAppName}.tmproj", overwrite: true)

    ant.replace(dir:"${basedir}", includes:"*.*", standardGriffonFilters)
    ant.replace(dir:"${basedir}", includes:"*.*") {
        replacefilter(token: "@griffon.eclipse.libs@", value: eclipseClasspathLibs())
        replacefilter(token: "@griffon.eclipse.jar@", value: eclipseClasspathGriffonJars())
    }
}

target ( appName : "Evaluates the application name") {
	if(!args) {
		Ant.input(message:"Application name not specified. Please enter:",
				  addProperty:"griffon.app.name")
		griffonAppName = Ant.antProject.properties.'griffon.app.name'
	}
	else {
		griffonAppName = args.trim()
		if(griffonAppName.indexOf('\n') > -1)
			griffonAppName = griffonAppName.replaceAll(/\n/, " ")
	}
	basedir = "${basedir}/${griffonAppName}"
	appClassName = GCU.getClassNameRepresentation(griffonAppName)
}

// Generates Eclipse .classpath entries for all the Griffon dependencies,
// i.e. a string containing a "<classpath entry ..>" element for each
// of Griffon' library JARs.
eclipseClasspathLibs = {
    def result = ''
    (new File("${griffonHome}/lib")).eachFileMatch(~/.*\.jar/) {file ->
        if (!file.name.startsWith("gant-")) {
            result += "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/lib/${file.name}\" />\n\n"
        }
    }
    result
}

// Generates Eclipse .classpath entries for the Griffon distribution
// JARs.
eclipseClasspathGriffonJars = {args ->
    result = ''
    (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file ->
        result += "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/dist/${file.name}\" />\n\n"
    }
    result
}

