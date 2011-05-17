/*
 * Copyright 2010-2011 the original author or authors.
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
 * @author Andres Almiray
 */

includeTargets << griffonScript("Init")
includeTargets << griffonScript("CreateIntegrationTest")

target(createArchetype: "Creates a new Griffon application archetype") {
    depends(parseArguments)
    promptForName(type: "Application Archetype")
    def (pkg, name) = extractArtifactName(argsMap["params"][0])

    ant.mkdir(dir: name)
    ant.mkdir(dir: "$name/templates/artifacts")

    new File("$name/application.groovy").append('''
import griffon.util.Metadata

includeTargets << griffonScript("_GriffonPlugins")
includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("CreateMvc" )

target(name: 'createApplicationProject',
       description: 'Creates a new application project',
       prehook: null, posthook: null) {
    createProjectWithDefaults()
    createMVC()

    // to install plugins do the following
    // Metadata md = Metadata.getInstance(new File("${basedir}/application.properties"))
    // installPluginExternal md, pluginName, pluginVersion
    //
    // pluginVersion is optional
}
setDefaultTarget(createApplicationProject)
''')
}
setDefaultTarget(createArchetype)
