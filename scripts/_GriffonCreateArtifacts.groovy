/*
* Copyright 2008 the original author or authors.
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

import org.springframework.core.io.FileSystemResource
import org.codehaus.griffon.util.GriffonNameUtils

/**
 * Gant script for creating Griffon artifacts of all sorts.
 *
 * @author Peter Ledbrook
 */

target(createArtifact: "Creates a specific Griffon artifact") { Map args = [:] ->
    def suffix = args["suffix"]
    def type = args["type"]
    def artifactPath = args["path"]

    ant.mkdir(dir: "${basedir}/${artifactPath}")

    // Extract the package name if one is given.
    def name = args["name"]
    def pkg = null
    def pos = name.lastIndexOf('.')
    if (pos != -1) {
        pkg = name[0..<pos]
        name = name[(pos + 1)..-1]
    }

    // Convert the package into a file path.
    def pkgPath = ''
    if (pkg) {
        pkgPath = pkg.replace('.' as char, '/' as char)

        // Make sure that the package path exists! Otherwise we won't
        // be able to create a file there.
        ant.mkdir(dir: "${basedir}/${artifactPath}/${pkgPath}")

        // Future use of 'pkgPath' requires a trailing slash.
        pkgPath += '/'
    }

    // Convert the given name into class name and property name
    // representations.
    className = GriffonNameUtils.getClassNameRepresentation(name)
    propertyName = GriffonNameUtils.getPropertyNameRepresentation(name)
    artifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${suffix}.groovy"


    if (new File(artifactFile).exists()) {
        ant.input(addProperty: "${name}.${suffix}.overwrite", message: "${type} ${className}${suffix}.groovy already exists. Overwrite? [y/n]")
        if (ant.antProject.properties."${name}.${suffix}.overwrite" == "n")
            return
    }

    // first check for presence of template in application
    templateFile = new FileSystemResource("${basedir}/src/templates/artifacts/${type}.groovy")
    if (!templateFile.exists()) {
        // now check for template provided by plugins
        def pluginTemplateFiles = resolveResources("file:${pluginsHome}/*/src/templates/artifacts/${type}.groovy")
        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0]
        } else {
            // template not found in application, use default template
            templateFile = griffonResource("src/griffon/templates/artifacts/${type}.groovy")
        }
    }

    copyGriffonResource(artifactFile, templateFile)
//    ant.copy(file: templateFile, tofile: artifactFile, overwrite: true)
    ant.replace(file: artifactFile) {
        replacefilter(token: "@artifact.name@", value: "${className}${suffix}" )
        replacefilter(token: "@griffon.app.class.name@", value:appClassName )
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll( /\s/, '.' ).toLowerCase())
    }
    if (pkg) {
        ant.replace(file: artifactFile, token: "@artifact.package@", value: "package ${pkg}\n\n")
    }
    else {
        ant.replace(file: artifactFile, token: "@artifact.package@", value: "")
    }

    event("CreatedFile", [artifactFile])
    event("CreatedArtefact", [ type, className])
}

target (createIntegrationTest: "Creates an integration test for an artifact") { Map args = [:] ->
	createArtifact(name: args["name"], suffix: "${args['suffix']}Tests", type: "Tests", path: "test/integration")
}

target (createUnitTest: "Creates a unit test for an artifact") { Map args = [:] ->
	createArtifact(name: args["name"], suffix: "${args['suffix']}Tests", type: "Tests", path: "test/unit")
}

target(promptForName: "Prompts the user for the name of the Artifact if it isn't specified as an argument") { Map args = [:] ->
    if (!argsMap["params"]) {
        ant.input(addProperty: "artifact.name", message: "${args["type"]} name not specified. Please enter:")
        argsMap["params"] << ant.antProject.properties."artifact.name"
    }
}
