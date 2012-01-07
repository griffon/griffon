/*
 * Copyright 2004-2011 the original author or authors.
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
 * @author Graeme Rocher (Grails 0.4)
 * @author Sergey Nebolsin (Grails 0.4)
 */

import griffon.util.Environment
import org.codehaus.griffon.artifacts.ArtifactUtils

includeTargets << griffonScript('_GriffonClean')

target(upgrade: "Upgrades a Griffon application from a previous version of Griffon") {
    boolean force = argsMap.force || !isInteractive ?: false

    if (appGriffonVersion != griffonVersion) {
        def gv = appGriffonVersion ?: "?Unknown?"
        event("StatusUpdate", ["NOTE: Your application currently expects griffon version [$gv], " +
                "this target will upgrade it to Griffon ${griffonVersion}"])
    } else {
        ant.input(message: """
        WARNING: Your application appears to be configured for Griffon ${griffonVersion} already.
        Are you sure you want to continue?
                   """,
                validargs: "y,n",
                addproperty: "griffon.overwrite.warning")

        def answer = ant.antProject.properties."griffon.overwrite.warning"
        if (answer == "n") exit(0)
        force = true
    }

    if (!force) {
        ant.input(message: """
        WARNING: This target will upgrade an older Griffon application to ${griffonVersion}.
        Are you sure you want to continue?
                   """,
                validargs: "y,n",
                addproperty: "griffon.upgrade.warning")

        def answer = ant.antProject.properties."griffon.upgrade.warning"
        if (answer == "n") exit(0)
    }

    clean()

    boolean isPost09 = ArtifactUtils.compareVersions(appGriffonVersion, '0.9') >= 0

    ant.sequential {
        delete(dir: "${basedir}/tmp", failonerror: false)

        createStructure()

        griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
        ant.unzip(src: "${basedir}/griffon-wrapper-files.zip", dest: basedir)
        ant.delete(file: "${basedir}/griffon-wrapper-files.zip", quiet: true)

        // if Config.groovy exists and it does not contain values added
        // since 0.0 then sensible defaultsare provided which keep previous
        // behavior even if it is not the default in the current version.
        def configFile = new File(baseFile, '/griffon-app/conf/Config.groovy')
        if (!isPost09 && configFile.exists()) {
            def configSlurper = new ConfigSlurper(System.getProperty(Environment.KEY))
            def configObject = configSlurper.parse(configFile.toURI().toURL())

            def packJars = configObject.griffon?.jars?.pack
            def signJars = configObject.griffon?.jars?.sign
            def extensionJars = configObject.griffon?.extensions?.jarUrls
            def extensionJNLPs = configObject.griffon?.extensions?.jnlpUrls
            def signingKeyFile = configObject.signingkey?.params?.sigfile
            def signingKeyStore = configObject.signingkey?.params?.keystore
            def deploy = configObject.deploy

            if ([packJars, signJars, extensionJars, extensionJNLPs, signingKeyFile].contains([:])) {
                event("StatusUpdate", ["Adding properties to Config.groovy"])
                configFile.withWriterAppend {
                    def indent = ''
                    it.writeLine '\n// The following properties have been added by the Upgrade process...'
                    if (!Boolean.valueOf(System.getProperty(Environment.DEFAULT))) {
                        indent = '        '
                        it.writeLine "environments {\n    ${System.getProperty(Environment.KEY)} {"
                    }
                    if (packJars == [:]) it.writeLine "${indent}griffon.jars.pack=false // jars were not automatically packed in Griffon 0.0"
                    if (signJars == [:]) it.writeLine "${indent}griffon.jars.sign=true // jars were automatically signed in Griffon 0.0"
                    if (extensionJars == [:]) it.writeLine "${indent}griffon.extensions.jarUrls = [] // remote jars were not possible in Griffon 0.1"
                    if (extensionJNLPs == [:]) it.writeLine "${indent}griffon.extensions.jnlpUrls = [] // remote jars were not possible in Griffon 0.1"
                    if (signingKeyFile == [:] || signingKeyStore == [:]) {
                        it.writeLine "// may safely be removed, but calling upgrade will restore it"
                        it.writeLine "${indent}def env = griffon.util.Environment.current.name"
                        it.writeLine "${indent}signingkey.params.sigfile='GRIFFON' + env"
                        it.writeLine "${indent}signingkey.params.keystore = \"\${basedir}/griffon-app/conf/keys/\${env}Keystore\""
                        it.writeLine "${indent}signingkey.params.alias = env"
                        it.writeLine "${indent}// signingkey.params.storepass = 'BadStorePassword'"
                        it.writeLine "${indent}// signingkey.params.keyPass = 'BadKeyPassword'"
                        it.writeLine "${indent}signingkey.params.lazy = true // only sign when unsigned"
                    }

                    it.writeLine "// you may now tweak memory parameters"
                    it.writeLine "//${indent}griffon.memory.min='16m'"
                    it.writeLine "//${indent}griffon.memory.max='64m'"
                    it.writeLine "//${indent}griffon.memory.maxPermSize='64m'"
                    if (indent != '') {
                        it.writeLine('    }\n}')
                    }
                }
            }

            if (deploy == [:]) {
                configFile.append('''deploy {
    application {
        title = '@griffonAppName@ @griffonAppVersion@'
        vendor = System.properties['user.name']
        homepage = 'http://localhost/@griffonAppName@'
        description {
            complete = '@griffonAppName@ @griffonAppVersion@'
            oneline  = '@griffonAppName@ @griffonAppVersion@'
            minimal  = '@griffonAppName@ @griffonAppVersion@'
            tooltip  = '@griffonAppName@ @griffonAppVersion@'
        }
        icon {
            fallback {
                name = 'griffon-icon-48x48.png'
                width = '48'
                height = '48'
            }
            splash {
                name = 'griffon.png'
                width = '391'
                height = '123'
            }
            menu {
                name = 'griffon-icon-16x16.png'
                width = '48'
                height = '48'
            }
            desktop {
                name = 'griffon-icon-32x32.png'
                width = '32'
                height = '32'
            }
        }
    }
}
''')
            }
        }

        // if Application.groovy exists and it does not contain values added
        // since 0.0 then sensible defaults are provided which keep previous
        // behavior even if it is not the default in the current version.
        def applicationFile = new File(baseFile, '/griffon-app/conf/Application.groovy')
        if (!isPost09 && applicationFile.exists()) {
            def configSlurper = new ConfigSlurper(System.getProperty(Environment.KEY))
            def configObject = configSlurper.parse(applicationFile.toURI().toURL())

            def startupGroups = configObject.application.startupGroups
            def autoShutdown = configObject.application.autoShutdown

            if ([startupGroups, autoShutdown].contains([:])) {
                event("StatusUpdate", ["Adding properties to Application.groovy"])
                if (startupGroups == [:]) {
                    configObject.application.startupGroups = ['root']
                }

                if (startupGroups == [:]) {
                    configObject.application.autoShutdown = true
                }
            }

            // update MVCGroups if we are pre 0.2
            if (appGriffonVersion =~ /0\.[01].*/) {
                event("StatusUpdate", ["Re-ordering MVCGroups"])

                configObject.mvcGroups.each {group, ConfigObject portions ->
                    if (portions.keySet().contains('view')) {
                        portions.view = portions.remove('view')
                    }
                }
            }

            // GRIFFON-147 make sure group names that contain hyphens are correctly scaped
            def groupsToFix = []
            configObject.mvcGroups.each { k, v ->
                if (k.contains('-')) groupsToFix << k
            }
            groupsToFix.each { k ->
                def v = configObject.mvcGroups.remove(k)
                configObject.mvcGroups.put("'$k'".toString(), v)
            }

            configObject.writeTo(new FileWriter(new File(baseFile, '/griffon-app/conf/Application.groovy')))
        }

        // remove GriffonApplicationHelper from Initialize.groovy
        def initializeFile = new File(baseFile, '/griffon-app/lifecycle/Initialize.groovy')
        if (initializeFile.exists()) {
            initializeFile.text -= 'import griffon.util.GriffonPlatformHelper\n'
            initializeFile.text -= 'GriffonPlatformHelper.tweakForNativePlatform(app)\n'
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

    // Unpack the shared files into a temporary directory, and then
    // copy over the IDE files.
    def tmpDir = new File("${basedir}/tmp-upgrade")
    griffonUnpack(dest: tmpDir.path, src: "griffon-app-files.jar")
    // copy new icons to griffon-app/conf/webstart
    // copy new icons to griffon-app/resources
    copy(todir: "${basedir}") {
        fileset(dir: tmpDir.path, includes: "**/*.png")
    }
    delete(dir: tmpDir.path)

    // Create BuildConfig.groovy if it does not exist
    def bcf = new File(baseFile, '/griffon-app/conf/BuildConfig.groovy')
    def cf = new File(baseFile, '/griffon-app/conf/Config.groovy')
    if (!bcf.exists()) {
        bcf.text = cf.text
        bcf.append("""griffon.project.dependency.resolution = {
    // inherit Griffon' default dependencies
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        griffonPlugins()
        griffonHome()
        griffonCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
    }
}

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error  'org.codehaus.griffon'
}

griffon {
    doc {
        logo = '<a href="http://griffon.codehaus.org" target="_blank"><img alt="The Griffon Framework" src="../img/griffon.png" border="0"/></a>'
        sponsorLogo = "<br/>"
        footer = "<br/><br/>Made with Griffon ($griffonVersion)"
    }
}

deploy {
    application {
        title = '@griffonAppName@ @griffonAppVersion@'
        vendor = System.properties['user.name']
        homepage = 'http://localhost/@griffonAppName@'
        description {
            complete = '@griffonAppName@ @griffonAppVersion@'
            oneline  = '@griffonAppName@ @griffonAppVersion@'
            minimal  = '@griffonAppName@ @griffonAppVersion@'
            tooltip  = '@griffonAppName@ @griffonAppVersion@'
        }
        icon {
            fallback {
                name = 'griffon-icon-48x48.png'
                width = '48'
                height = '48'
            }
            splash {
                name = 'griffon.png'
                width = '391'
                height = '123'
            }
            menu {
                name = 'griffon-icon-16x16.png'
                width = '48'
                height = '48'
            }
            desktop {
                name = 'griffon-icon-32x32.png'
                width = '32'
                height = '32'
            }
        }
    }
}""")
        cf.text = '''// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error  'org.codehaus.griffon'

    info   'griffon.util',
           'griffon.core',
           'griffon.swing',
           'griffon.app'
}
'''
    }

    // ensure a href= is in the application
    // ensure all .jnlp files have a memory hook, unless already tweaked
    // ensure all .jnlp files support remote jnlps
    // add splash to jnlps
    // set icons for jnlps
    fileset(dir: "${basedir}/griffon-app/conf/", includes: "**/*.jnlp").each {
        def fileText = it.getFile().getText()
        ant.replace(file: it.toString()) {
            if (!fileText.contains('href="@jnlpFileName@"')) {
                replacefilter(token: 'codebase=', value: 'href="@jnlpFileName@" codebase=')
            }
            replacefilter(token: '<j2se version="1.5+"/>', value: '<j2se version="1.5+" @memoryOptions@/>')
            replacefilter(token: '<title>@griffonAppName@</title>',
                    value: '<title>@griffon.application.title@</title>')
            replacefilter(token: '<vendor>@griffonAppName@</vendor>',
                    value: '<vendor>@griffon.application.vendor@</vendor>')
            replacefilter(token: '<!--<homepage href="http://app.example.com/"/>-->',
                    value: '<homepage href="@griffon.application.homepage@"/>')
            replacefilter(token: '<description>@griffonAppName@</description>',
                    value: '<description>@griffon.application.description.complete@</description>')
            replacefilter(token: '<description kind="one-line">@griffonAppName@</description>',
                    value: '<description kind="one-line">@griffon.application.description.oneline@</description>')
            replacefilter(token: '<description kind="short">@griffonAppName@</description>',
                    value: '<description kind="short">@griffon.application.description.minimal@</description>')
            replacefilter(token: '<description kind="tooltip">@griffonAppName@</description>',
                    value: '<description kind="tooltip">@griffon.application.description.tooltip@</description>')
            replacefilter(token: '<!--<icon href="http://example.com/icon.gif" kind="splash" width="" height=""/>-->',
                    value: '<icon href="@griffon.application.icon.fallback@" kind="default" width="@griffon.application.icon.fallback.width@" height="@griffon.application.icon.fallback.height@"/>')
            replacefilter(token: '<!--<icon href="http://example.com/icon.gif" kind="default" width="" height=""/>-->',
                    value: '<icon href="@griffon.application.icon.splash@" kind="default" width="@griffon.application.icon.splash.width@" height="@griffon.application.icon.splash.height@"/>')
            replacefilter(token: '<!--<icon href="http://example.com/icon.gif" kind="shortcut" width="16" height="16"/>-->',
                    value: '<icon href="@griffon.application.icon.menu@" kind="default" width="@griffon.application.icon.menu.width@" height="@griffon.application.icon.menu.height@"/>')
            replacefilter(token: '<!--<icon href="http://example.com/icon.gif" kind="shortcut" width="32" height="32"/>-->',
                    value: '<icon href="@griffon.application.icon.desktop@" kind="default" width="@griffon.application.icon.desktop.width@" height="@griffon.application.icon.desktop.height@"/>')
            if (!fileText.contains('@jnlpExtensions@')) {
                replacefilter(token: '</resources>', value: '@jnlpExtensions@ \n</resources>')
            }
        }
    }

    // update the icons in the html
    fileset(dir: "${basedir}/griffon-app/conf/", includes: "**/*.html").each {
        ant.replace(file: it.toString()) {
            replacefilter(token: "image:'griffon.jpeg'", value: "image:'griffon.png'")
        }
    }

    def wrapperConfig = new File(baseFile, '/wrapper/griffon-wrapper.properties')
    if (isPost09 && wrapperConfig.exists()) {
        def wrapperProps = new Properties()
        wrapperConfig.eachLine {l ->
            if (l.startsWith('#')) return
            List kv = l.tokenize('=')
            kv[1] = kv[1].replace('\\', '')
            wrapperProps.put(kv[0], kv[1])
        }
        wrapperProps.put('distributionVersion', griffonVersion)
        wrapperConfig.withOutputStream {o ->
            wrapperProps.store(o, "Griffon $griffonVersion upgrade")
        }
    }

    //TODO create an upgrade README
    //event("StatusUpdate", ["Please make sure you view the README for important information about changes to your source code."])

    event("StatusFinal", ["Project upgraded"])
}

setDefaultTarget(upgrade)