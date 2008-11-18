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
 * Gant script that evaluates all installed scripts to create help output
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

import org.codehaus.griffon.commons.GriffonClassUtils as GCU

defaultTarget("Prints out the help for each script") {
    helpTarget()
}

includeTargets << griffonScript("Init")

class HelpEvaluatingCategory {
	static defaultTask = ""
	static target(Object obj, Map args, Closure callable) {
		def e = args.find { e -> e.key == "default" }?.value
		if(e) {
			defaultTask = e
		}
	}
	static getDefaultTask(Object obj) {
		return defaultTask
	}

}

File getHelpFile(File script) {
    File helpDir = new File(griffonTmp, "help")
    if (!helpDir.exists()) helpDir.mkdir()
    String scriptname = script.getName()
	return new File(helpDir, scriptname.substring(0, scriptname.lastIndexOf('.')) + ".txt")
}

boolean shouldGenerateHelp(File script) {
	File file = getHelpFile(script)
    return (!file.exists() || file.lastModified() < script.lastModified() )
}


target ( 'helpTarget' : "Prints out the help for each script") {
    Ant.mkdir(dir:griffonTmp)
	def scripts = getAllScripts().collect { it.file }

	def helpText = ""


	if(args) {
        def fileName = GCU.getNameFromScript(args) + ".groovy"
		def file = scripts.find { it.name == fileName }


		println """
Usage (optionals marked with *):
griffon [environment]*
		"""
		def gcl = new GroovyClassLoader()
		use(HelpEvaluatingCategory.class) {
			if (shouldGenerateHelp(file)) {
				try {
					def script = gcl.parseClass(file).newInstance()
					script.binding = binding
					script.run()

                    def scriptName = GCU.getScriptName(file.name)

					helpText = "griffon ${scriptName} -- ${getDefaultTask()}"
					File helpFile = getHelpFile(file)
					if(!helpFile.exists())
					    helpFile.createNewFile()
                    helpFile.write(helpText)
				}
				catch(Throwable t) {
					println "Warning: Error caching created help for ${file}: ${t.message}"
					println helpText
				}
			} else {
				helpText = getHelpFile(file).text
			}
			println helpText
		}
	}
	else {
			println """
		Usage (optionals marked with *):
		griffon [environment]* [target] [arguments]*

		Examples:
		griffon dev run-app
		griffon create-app books

		Available Targets (type griffon help 'target-name' for more info):"""

	    scripts.unique { it.name }. sort{ it.name }.each { file ->
            def scriptName = GCU.getScriptName(file.name)
			println "griffon ${scriptName}"
		}
	}
}

target( showHelp: "Show help for a particular command") {
	def gcl = new GroovyClassLoader()
	use(HelpEvaluatingCategory.class) {
		if (shouldGenerateHelp(file)) {
			try {
				def script = gcl.parseClass(file).newInstance()
				script.binding = binding
				script.run()

                def scriptName = GCU.getScriptName(file.name)

				helpText = "griffon ${scriptName} -- ${getDefaultTask()}"
				getHelpFile(file).write(helpText)
			}
			catch(Throwable t) {
				println "Error creating help for ${file}: ${t.message}"
				t.printStackTrace(System.out)
			}
		} else {
			helpText = getHelpFile(file).text
		}
		println helpText
	}

}
