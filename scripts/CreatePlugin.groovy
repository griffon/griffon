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
 * Gant script that handles the creation of Griffon plugins
 *
 * @author Graeme Rocher
 * @author Sergey Nebolsin
 *
 * @since 0.4
 */
import org.codehaus.griffon.commons.GriffonClassUtils as GCU

appName = ""

defaultTarget ("Creates a Griffon plug-in project, including the necessary directory structure and commons files") {
   createPlugin()
}

includeTargets << griffonScript("CreateApp")

target( createPlugin: "The implementation target")  {
    depends( appName, createStructure, updateAppProperties )

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    griffonUnpack(dest: basedir, src: "griffon-plugin-files.jar")
    createIDESupportFiles()

    // Rename the plugin descriptor.
    pluginName = GCU.getNameFromScript(griffonAppName)
    ant.move(
            file: "${basedir}/GriffonPlugin.groovy",
            tofile: "${basedir}/${pluginName}GriffonPlugin.groovy",
            overwrite: true)

    // Insert the name of the plugin into whatever files need it.
    ant.replace(dir:"${basedir}") {
        include(name: "*GriffonPlugin.groovy")
        include(name: "scripts/*")
        replacefilter(token: "@plugin.name@", value: pluginName)
    }

    event("StatusFinal", [ "Created plugin ${pluginName}"])
}
