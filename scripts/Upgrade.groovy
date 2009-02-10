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
 * Gant script that handles upgrading of a Griffon applications
 *
 * @author Graeme Rocher
 * @author Sergey Nebolsin
 *
 * @since 0.4
 */

import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.codehaus.griffon.commons.GriffonContext

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonClean")

target(upgrade: "Upgrades a Griffon application from a previous version of Griffon") {

    depends(createStructure)

    boolean force = args?.indexOf('-force') > -1 ? true : false

    if (appGriffonVersion != griffonVersion) {
        def gv = appGriffonVersion == null ? "?Unknown?" : appGriffonVersion
        event("StatusUpdate", ["NOTE: Your application currently expects griffon version [$gv], " +
                "this target will upgrade it to Griffon ${griffonVersion}"])
    }

    if (!force) {
        //TODO warn user about descructive changes

//        ant.input(message: """
//        WARNING: Something bad might happen
//				   """,
//                validargs: "y,n",
//                addproperty: "griffon.upgrade.warning")
//
//        def answer = ant.antProject.properties."griffon.upgrade.warning"
//
//        if (answer == "n") exit(0)
//
//        if ((griffonVersion.startsWith("1.0")) &&
//                !(['utf-8', 'us-ascii'].contains(System.getProperty('file.encoding')?.toLowerCase()))) {
//            ant.input(message: """
//        WARNING: Something else bad might happen
//	                   """,
//                    validargs: "y,n",
//                    addproperty:"griffon.another.warning")
//            answer = ant.antProject.properties."griffon.another.warning"
//            if (answer == "n") exit(0)
//        }
    }

    clean()

    ant.sequential {
        // removed from grails: move test dir, also has source control chceks

        delete(dir: "${basedir}/tmp", failonerror: false)

        // Unpack the shared files into a temporary directory, and then
        // copy over the IDE files.
        def tmpDir = new File("${basedir}/tmp-upgrade")
        griffonUnpack(dest: tmpDir.path, src: "griffon-shared-files.jar")
        copy(todir: "${basedir}") {
            fileset(dir: tmpDir.path, includes: "*")
        }
        delete(dir: tmpDir.path)
        launderIDESupportFiles()

        // remove from grails: a bunch of servlet stuff
        // remove from grails: adding new files in grails-app/conf

        mkdir(dir: "${basedir}/test")
        mkdir(dir: "${basedir}/test/integration")
        mkdir(dir: "${basedir}/test/unit")

        // remove from grails: URLMappings
        // if Config.groovy exists and it does not contain values added
        // since 0.0 then sensible defaultsare provided which keep previous
        // behavior even if it is not the default in the current version.
        def configFile = new File(baseFile, '/griffon-app/conf/Config.groovy')
        if (configFile.exists()) {
            def configSlurper = new ConfigSlurper(System.getProperty(GriffonContext.ENVIRONMENT))
            def configObject = configSlurper.parse(configFile.toURI().toURL())

            def packJars = configObject.griffon?.jars?.pack
            def signJars = configObject.griffon?.jars?.sign
            def extensionJars = configObject.griffon?.extensions?.jarUrls
            def extensionJNLPs = configObject.griffon?.extensions?.jnlpUrls
            def signingKeyFile = configObject.signingkey?.params?.sigfile

            if ([packJars, signJars, extensionJars, extensionJNLPs, signingKeyFile].contains([:])) {
                event("StatusUpdate", ["Adding properties to Config.groovy"])
                configFile.withWriterAppend {
                    def indent = ''
                    it.writeLine '\n// The following properties have been added by the Upgrade process...'
                    if (!Boolean.valueOf(System.getProperty(GriffonContext.ENVIRONMENT_DEFAULT))) {
                        indent = '        '
                        it.writeLine "environments {\n    ${System.getProperty(GriffonContext.ENVIRONMENT)} {"
                    }
                    if (packJars == [:]) it.writeLine "${indent}griffon.jars.pack=false // jars were not automatically packed in Griffon 0.0"
                    if (signJars == [:]) it.writeLine "${indent}griffon.jars.sign=true // jars were automatically signed in Griffon 0.0"
                    if (extensionJars == [:]) it.writeLine "${indent}griffon.extensions.jarUrls = [] // remote jars were not possible in Griffon 0.1"
                    if (extensionJNLPs == [:]) it.writeLine "${indent}griffon.extensions.jnlpUrls = [] // remote jars were not possible in Griffon 0.1"
                    if (signingKeyFile == [:]) it.writeLine "${indent}signingkey.params.sigfile='GRIFFON' // may safely be removed, but calling upgrade will restore it"
                    it.writeLine "// you may now tweak memory parameters"
                    it.writeLine "//${indent}griffon.memory.min='16m'"
                    it.writeLine "//${indent}griffon.memory.max='64m'"
                    it.writeLine "//${indent}griffon.memory.maxPermSize='64m'"
                    if (indent != '') {
                        it.writeLine('    }\n}')
                    }
                }
            }
        }

        // if Application.groovy exists and it does not contain values added
        // since 0.0 then sensible defaults are provided which keep previous
        // behavior even if it is not the default in the current version.
        def applicationFile = new File(baseFile, '/griffon-app/conf/Application.groovy')
        if (applicationFile.exists()) {
            def configSlurper = new ConfigSlurper(System.getProperty(GriffonContext.ENVIRONMENT))
            def configObject = configSlurper.parse(applicationFile.toURI().toURL())

            def startupGroups = configObject.application.startupGroups
            def autoShutdown = configObject.application.autoShutdown

            if ([startupGroups, autoShutdown].contains([:])) {
                event("StatusUpdate", ["Adding properties to Application.groovy"])
                applicationFile.withWriterAppend {
                    def indent = ''
                    it.writeLine '\n// The following properties have been added by the Upgrade process...'
                    if (startupGroups == [:]) it.writeLine "${indent}application.startupGroups=['root'] // default startup group from 0.0"
                    if (autoShutdown == [:]) it.writeLine "${indent}application.autoShutdown=true // default autoShutdown from 0.0"
                }
            }
        }

        touch(file: "${basedir}/griffon-app/i18n/messages.properties")

        event("StatusUpdate", ["Updating application.properties"])
        propertyfile(file: "${basedir}/application.properties",
                comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                        "DO NOT put application configuration in here, it is not the right place!") {
            entry(key: "app.name", value: "$griffonAppName")
            entry(key: "app.griffon.version", value: "$griffonVersion")
        }
    }
    // ensure all .jnlp files have a memory hook, unlessa already tweaked
    // ensure all .jnlp files support remote jnlps
    fileset(dir:"${basedir}/griffon-app/conf/", includes:"**/*.jnlp").each {
        def fileText = it.getFile().getText()
        ant.replace(file: it.toString()) {
            replacefilter(token: '<j2se version="1.5+"/>', value: '<j2se version="1.5+" @memoryOptions@/>')
            if (!fileText.contains('@jnlpExtensions@')) {
            	replacefilter(token: '</resources>', value: '@jnlpExtensions@ \n</resources>')
            }
        }
    }
    
    // ensure that applet code supports jnlp extensions and remote jars
    fileset(dir:"${basedir}/griffon-app/conf/", includes:"**/*.html").each {
    	def fileText = it.getFile().getText()
   	    if (!fileText.contains('@griffonJnlps@')) {
    		ant.replace(file: it.toString()) {
    			replacefilter(token: '@griffonAppCodebase@/applet.jnlp', value:'@griffonAppCodebase@/applet.jnlp @griffonJnlps@')
            }
    	}
    	if (!fileText.contains('@griffonJnlpAppletExtensions@')) {
    		ant.replace(file: it.toString()) {
	    		replacefilter(token: '</APPLET>', value:'@griffonJnlpAppletExtensions@ \n </APPLET>')
    		}
        }
    }


// proceed plugin-specific upgrade logic contained in 'scripts/_Upgrade.groovy' under plugin's root
    def plugins = GriffonPluginUtils.getPluginBaseDirectories(pluginsHome)
    if (plugins) {
        for (pluginDir in plugins) {
            def f = new File(pluginDir)
            if (f.isDirectory() && f.name != 'core') {
                // fix for Windows-style path with backslashes

                def pluginBase = "${basedir}/plugins/${f.name}".toString().replaceAll("\\\\", "/")
                // proceed _Upgrade.groovy plugin script if exists
                def upgradeScript = new File("${pluginBase}/scripts/_Upgrade.groovy")
                if (upgradeScript.exists()) {
                    event("StatusUpdate", ["Executing ${f.name} plugin upgrade script"])
                    // instrumenting plugin scripts adding 'pluginBasedir' variable
                    def instrumentedUpgradeScript = "def pluginBasedir = '${pluginBase}'\n" + upgradeScript.text
                    // we are using text form of script here to prevent Gant caching
                    includeTargets << instrumentedUpgradeScript
                }
            }
        }

    }

    //TODO create an upgrade README
    //event("StatusUpdate", ["Please make sure you view the README for important information about changes to your source code."])

    event("StatusFinal", ["Project upgraded"])
}

setDefaultTarget(upgrade)
