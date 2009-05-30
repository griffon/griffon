/*
* Copyright 2009 the original author or authors.
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

import java.awt.Component
import java.beans.Introspector

includeTargets << griffonScript("Init")
includeTargets << griffonScript("_GriffonCreateArtifacts")
includeTargets << griffonScript("_GriffonCompile")


target(genViewScript: "Generates a view script from an existing class") {
    depends(checkVersion, parseArguments, classpath, compile)

    promptForName(type: "Class to Proxy View for:")
    String klassName = argsMap["params"][0]
    def (String pkg, String name) = extractArtifactName(klassName)

    def fields = []

    try {
        GroovyClassLoader gcl = new GroovyClassLoader(rootLoader, compConfig)
        Class klass = gcl.loadClass(klassName)
        while (klass != null && !(klass['package']?.name ==~ "java(x)?\\..*")) {
            klass.declaredFields.collect(fields) {it}
            klass = klass.superclass
        }


        String outputName = argsMap['view'] ?: argsMap['params'][1] ?: "${klassName}View"
        String outputFileName = "${basedir}/griffon-app/views/${outputName.replace('.', '/')}.groovy"
        String varName = Introspector.decapitalize(name);

        File outputFile = new File(outputFileName)
        if (outputFile.exists()) {
            def result = confirmInput("""
The target file already exists:
 -> $outputFile
If you continue all contents of that file will be deleted.
Do you wish to continue and overwrite the existing file?""")
            if(result == 'n') exit(0)
        }

        new File(outputFileName).withWriter {writer ->
            if (pkg) {
                writer << "package $pkg\n\n"
            }

            if (Component.isAssignableFrom(klass)) {
                writer <<  """// create instance of view object
widget(new $klassName(), id:'$varName')

"""
            } else {
                writer <<  """// create instance of view object
$varName = new $klassName()

"""
            }

            if( fields ) {
               writer << "noparent {\n"
            }
            fields.each {field ->
                writer << """    // $field.type.name $field.name declared in $field.declaringClass.name
    bean($varName.$field.name, id:'$field.name')

"""
            }
            if( fields ) {
               writer << """}
return $varName
"""
            }
        }

    } catch (ClassNotFoundException cnfe) {
        event('StatusFinal', ["Source class cound not be found: $klassName"])
        exit(1)
    }
}

setDefaultTarget(genViewScript)
