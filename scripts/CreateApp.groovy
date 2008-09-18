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

import org.codehaus.groovy.griffon.commons.GriffonClassUtils as GCU

griffonAppName = ""

Ant.property(environment:"env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"    

includeTargets << new File ( "${griffonHome}/scripts/CreateMvc.groovy" )
includeTargets << new File ( "${griffonHome}/scripts/Package.groovy" )


target ( "default" : "Creates a Griffon project, including the necessary directory structure and commons files") {
   createApp()
}

target( createApp: "The implementation target")  {
    depends( appName, createStructure, updateAppProperties, init )

    createIDESupportFiles()

    replaceTokens()

    classpath()
    //loadPlugins()
    //generateWebXml()

    args = griffonAppName//.replaceAll( /\s/, '.' ).toLowerCase()
    createMVC()

    // Set the default version number for the application
    Ant.propertyfile(file:"${basedir}/application.properties") {
        entry(key:"app.version", value:"0.1")
        entry(key:"app.servlet.version", value:servletVersion)
    }

    event("StatusFinal", ["Created Griffon Application at $basedir"])
}

target( createIDESupportFiles: "Creates the IDE suppot files (Eclipse, TextMate etc.) project files") {
    Ant.copy(todir:"${basedir}") {
        fileset(dir:"${griffonHome}/src/griffon/templates/ide-support/eclipse",
                excludes:".launch")
    }
    Ant.copy(todir:"${basedir}", file:"${griffonHome}/src/griffon/build.xml")
    Ant.copy(file:"${griffonHome}/src/griffon/templates/ide-support/eclipse/.launch",
            tofile:"${basedir}/${griffonAppName}.launch", overwrite:true)
    Ant.copy(file:"${griffonHome}/src/griffon/templates/ide-support/textmate/project.tmproj",
            tofile:"${basedir}/${griffonAppName}.tmproj", overwrite:true)
}

target( replaceTokens: "Replaces creation tokens in the ") {
    Ant.replace(dir:"${basedir}",includes:"**/*.*",
                token:"@griffon.libs@", value:"${getGriffonLibs()}" )
    Ant.replace(dir:"${basedir}", includes:"**/*.*",
                token:"@griffon.jar@", value:"${getGriffonJar()}" )
    Ant.replace(dir:"${basedir}", includes:"**/*.*",
                token:"@griffon.version@", value:"${griffonVersion}" )


    def appKey = griffonAppName.replaceAll( /\s/, '.' ).toLowerCase()

    Ant.replace(dir:"${basedir}", includes:"**/*.*",
                token:"@griffon.project.name@", value:"${griffonAppName}" )
    Ant.replace(dir:"${basedir}", includes:"**/*.*",
                token:"@griffon.app.class.name@", value:"${appClassName}" )
    Ant.replace(dir:"${basedir}", includes:"**/*.*",
                token:"@griffon.project.key@", value:"${appKey}" )
}

target ( appName : "Evaluates the application name") {
    if(!args) {
        Ant.input(message:"Application name not specified. Please enter:",
                  addProperty:"griffon.app.name")
        griffonAppName = Ant.antProject.properties."griffon.app.name"
    }
    else {
        griffonAppName = args.trim()
        if(griffonAppName.indexOf('\n') > -1)
            griffonAppName = griffonAppName.replaceAll(/\n/, " ")
    }

    basedir = "${basedir}/${griffonAppName}"
    appClassName = GCU.getClassNameRepresentation(griffonAppName)
}
