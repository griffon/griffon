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
 * Gant script that handles the packaging of Griffon plug-ins
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

pluginResources = []

target( packagePlugins : "Packages any Griffon plugins that are installed for this project") {
	depends( classpath )
	try {

		pluginResources = getPluginDescriptors()
        basePlugin = getBasePluginDescriptor()
           for(p in pluginResources) {
	   		def pluginBase = p.file.parentFile.canonicalFile
	     	def pluginPath = pluginBase.absolutePath
			def pluginName = pluginBase.name
			def pluginNameWithVersion = pluginBase.name

	   		ant.sequential {
//				if(new File("${pluginBase}/griffon-app/conf/hibernate").exists()) {
//		   			copy(todir:classesDirPath, failonerror:false) {
//		   				fileset(dir:"${pluginBase}/griffon-app/conf/hibernate", includes:"**", excludes:"*.groovy")
//		   			}
//				}
				if(new File("${pluginBase}/griffon-app/conf").exists()) {
		   			copy(todir:classesDirPath, failonerror:false) {
		   				fileset(dir:"${pluginBase}/griffon-app/conf", includes:"*", excludes:"*.groovy")
		   			}
				}
//	            if(new File("${pluginPath}/web-app").exists()) {
//					ant.mkdir(dir:"${basedir}/web-app/plugins/${pluginName}")
//					if(basePlugin != p) {
//			  			copy(todir:"${basedir}/web-app/plugins/${pluginName}") {
//			   				fileset(dir:"${pluginBase}/web-app", includes:"**", excludes:"**/WEB-INF/**, **/META-INF/**")
//			   			}
//					}
//				}
	   		}
	   	}
	}
	catch(Exception e) {
        logError("Error packaging plugins", e)
	}
}

