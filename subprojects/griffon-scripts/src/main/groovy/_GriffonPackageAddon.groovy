/*
 * Copyright 2009-2012 the original author or authors.
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

import aQute.lib.osgi.Analyzer
import java.util.jar.Manifest
import java.text.SimpleDateFormat

/**
 * Gant script that packages a Griffon addon inside of a plugin
 *
 * @author Danno Ferrin
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_package_addon_called')) return
_griffon_package_addon_called = true

includeTargets << griffonScript('_GriffonPackage')

target(name: 'packageAddon', description: 'Packages a Griffon addon',
        prehook: null, posthook: null) {
    projectType = 'plugin'
    createStructure()

    try {
        profile("compile") {
            compile()
        }
    } catch (Exception e) {
        logError("Compilation error", e)
        exit(1)
    }

    addonJarDir = "${artifactPackageDirPath}/dist"
    ant.mkdir(dir: addonJarDir)

    cliJarName = "griffon-${pluginName}-compile-${pluginVersion}.jar"
    if (cliSourceDir.exists()) {
        ant.jar(destfile: "$addonJarDir/$cliJarName") {
            fileset(dir: projectCliClassesDir)
        }
    }

    if (!isAddonPlugin) return

    profile("creating config") {
        createConfig()
    }

    packageResources()

    File metainfDirPath = new File("${basedir}/griffon-app/conf/metainf")
    addonJarName = "griffon-${pluginName}-runtime-${pluginVersion}.jar"

    Date buildTimeAndDate = new Date()
    String buildTime = new SimpleDateFormat('dd-MMM-yyyy').format(buildTimeAndDate)
    String buildDate = new SimpleDateFormat('hh:mm aa').format(buildTimeAndDate)
    String bundleName = "griffon-${pluginName}-runtime".toString()

    Map defaultManifestAttributes = [
        'Built-By': System.properties['user.name'],
        'Created-By': System.properties['java.version'] + " (" + System.properties['java.vendor'] + " "+ System.getProperty("java.vm.version") + ")",
        'Build-Date': buildTime,
        'Build-Time': buildDate,
        'Specification-Title': bundleName,
        'Specification-Version': pluginVersion,
        'Specification-Vendor': 'griffon-framework.org',
        'Implementation-Title': bundleName,
        'Implementation-Version': pluginVersion,
        'Implementation-Vendor': 'griffon-framework.org'
    ]
    Map osgiManifestAttributes = [
        (Analyzer.BUNDLE_NAME): bundleName,
        (Analyzer.BUNDLE_VERSION): pluginVersion,
        (Analyzer.BUNDLE_SYMBOLICNAME): bundleName,
        (Analyzer.BND_LASTMODIFIED): projectMainClassesDir.lastModified(),
        (Analyzer.BUNDLE_LICENSE): descriptorInstance.license,
        (Analyzer.BUNDLE_VENDOR): 'griffon-framework.org',
        (Analyzer.EXPORT_PACKAGE): "config,addon,*;-noimport:=false;version=${pluginVersion}",
        (Analyzer.IMPORT_PACKAGE): "*",
        (Analyzer.BUNDLE_ACTIVATIONPOLICY): "lazy"
    ]

    try {
        osgiManifestAttributes.putAll(descriptorInstance.manifest)
    } catch(MissingPropertyException mpe) {
        if (mpe.property != 'manifest') {
            event('StatusError', ["An error occured when processing OSGi manifest entries: ${mpe}"])
            exit 1
        }
    }

    Analyzer analyzer = new Analyzer()
    osgiManifestAttributes.each { k, v -> analyzer.setProperty(k, v.toString()) }
    analyzer.setJar(projectMainClassesDir)
    analyzer.setClasspath(griffonSettings.runtimeDependencies.toArray(
        new File[griffonSettings.runtimeDependencies.size()]
    ))
    Manifest osgiManifest = analyzer.calcManifest()
    Map mergedAttributes = [:]
    osgiManifest.mainAttributes.each { k, v -> mergedAttributes[k.toString()] = v }
    mergedAttributes.putAll(defaultManifestAttributes)

    ant.jar(destfile: "$addonJarDir/$addonJarName") {
        fileset(dir: projectMainClassesDir) {
            exclude(name: 'Config*.class')
            exclude(name: 'BuildConfig*.class')
            exclude(name: '*GriffonPlugin*.class')
            exclude(name: 'application.properties')
        }
        fileset(dir: i18nDir)
        fileset(dir: resourcesDir)

        manifest {
            mergedAttributes.sort().each { key, value ->
                attribute(name: key, value: value)
            }
        }

        if (metainfDirPath.list()) {
            metainf(dir: metainfDirPath)
        }
    }
}
