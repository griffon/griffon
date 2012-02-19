/*
* Copyright 2004-2012 the original author or authors.
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

import static griffon.util.GriffonNameUtils.capitalize

/**
 * Gant script that handles upgrading of a Griffon applications
 *
 * @author Graeme Rocher (Grails 0.4)
 * @author Sergey Nebolsin (Grails 0.4)
 */

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

    projectType = 'app'
    if (isPluginProject) projectType = 'plugin'
    if (isArchetypeProject) projectType = 'archetype'
    createStructure()

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    ant.unzip(src: "${basedir}/griffon-wrapper-files.zip", dest: basedir)
    ant.delete(file: "${basedir}/griffon-wrapper-files.zip", quiet: true)

    File upgradeDir = new File("${basedir}/upgrade")
    ant.mkdir(dir: upgradeDir)
    File inBuildConfigFile = new File("${basedir}/griffon-app/conf/BuildConfig.groovy")
    File inConfigFile = new File("${basedir}/griffon-app/conf/Config.groovy")
    File inBuilderConfigFile = new File("${basedir}/griffon-app/conf/Builder.groovy")
    File outBuildConfigFile = new File("${upgradeDir}/BuildConfig.groovy")
    File outConfigFile = new File("${upgradeDir}/Config.groovy")
    File outBuilderConfigFile = new File("${upgradeDir}/Builder.groovy")

    if (inBuildConfigFile.exists() && !outBuildConfigFile.exists()) ant.move(file: inBuildConfigFile, tofile: outBuildConfigFile)
    if (inConfigFile.exists() && !outConfigFile.exists()) ant.move(file: inConfigFile, tofile: outConfigFile)
    if (inBuilderConfigFile.exists() && !outBuilderConfigFile.exists()) ant.move(file: inBuilderConfigFile, tofile: outBuilderConfigFile)

    // Unpack the shared files into a temporary directory
    File tmpdir = new File("${basedir}/tmp-upgrade")
    griffonUnpack(dest: tmpdir.path, src: "griffon-${projectType}-files.jar")

    try {
        switch (projectType) {
            case 'app':
                ant.copy(todir: "${basedir}/griffon-app/conf", file: "${tmpdir}/griffon-app/conf/BuildConfig.groovy")
                ant.copy(todir: "${basedir}/griffon-app/conf", file: "${tmpdir}/griffon-app/conf/Config.groovy")
                ant.copy(todir: "${basedir}/griffon-app/conf", file: "${tmpdir}/griffon-app/conf/Builder.groovy")

                def value = outBuildConfigFile.text =~ /.*app.fileType = (.*)/
                if (value) inBuildConfigFile.append("""\n${value[0][0]}\n""")
                value = outBuildConfigFile.text =~ /.*app.defaultPackageName = (.*)/
                if (value) inBuildConfigFile.append("""\n${value[0][0]}\n""")

                // copy new icons to griffon-app/conf/webstart
                // copy new icons to griffon-app/resources
                ant.copy(todir: "${basedir}") {
                    fileset(dir: tmpdir.path, includes: "**/*.png")
                }
                break
            case 'plugin':
                ant.copy(todir: "${basedir}/griffon-app/conf", file: "${tmpdir}/griffon-app/conf/BuildConfig.groovy")
                break
            case 'archetype':
                ant.copy(todir: "${basedir}/griffon-app/conf", file: "${tmpdir}/griffon-app/conf/BuildConfig.groovy")
        }

        ant.replace(dir: "${basedir}/griffon-app/conf", includes: "**/*.*") {
            replacefilter(token: "@griffonAppName@", value: capitalize(griffonAppName))
            replacefilter(token: "@griffonAppVersion@", value: griffonAppVersion ?: "0.1")
        }
    } finally {
        delete(dir: tmpdir.path)
    }

    if (!isArchetypeProject) {
        // remove GriffonApplicationHelper from Initialize.groovy
        def initializeFile = new File(basedir, '/griffon-app/lifecycle/Initialize.groovy')
        if (initializeFile.exists()) {
            initializeFile.text -= 'import griffon.util.GriffonPlatformHelper\n'
            initializeFile.text -= 'GriffonPlatformHelper.tweakForNativePlatform(app)\n'
        }

        touch(file: "${basedir}/griffon-app/i18n/messages.properties")

        event("StatusUpdate", ["Updating application.properties"])
        propertyfile(file: "${basedir}/application.properties",
                comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                        "DO NOT put application configuration in here, it is not the right place!") {
            entry(key: "app.name", value: griffonAppName)
            entry(key: "app.griffon.version", value: griffonVersion)
            if (!isPluginProject) {
                entry(key: "plugins.swing", value: '0.9.5')
                entry(key: "archetype.default", value: griffonVersion)
                entry(key: "app.toolkit", value: 'swing')
            }
        }
    }

    def wrapperConfig = new File(basedir, '/wrapper/griffon-wrapper.properties')
    if (wrapperConfig.exists()) {
        def wrapperProps = new Properties()
        wrapperConfig.eachLine {l ->
            if (l.startsWith('#')) return
            List kv = l.tokenize('=')
            kv[1] = kv[1].replace('\\', '')
            wrapperProps.put(kv[0], kv[1])
        }
        wrapperProps.put('distributionVersion', griffonVersion)
        wrapperConfig.withOutputStream { o ->
            wrapperProps.store(o, "Griffon $griffonVersion upgrade")
        }
    }

    printFramed("""
        | Project ${griffonAppName} has been upgraded.
        |
        | Griffon ${griffonVersion} brings substantial changes to logging
        | and packaging configuration. The upgrade process has taken the
        | precuation of making copies of your configuration files and placed
        | them inside
        |
        |   ${upgradeDir.absolutePath}
        |
        | Please review and copy over any project specific values into their
        | respective configuration files, taking into account that builder
        | configuration will be set automatically as plugins are installed.
        |
        | Also, review that the values of 'application.properties' are
        | appropriate for the current project.
        |""".stripMargin())

    event("StatusFinal", ["Project upgraded"])
}

setDefaultTarget(upgrade)
