/*
 * Copyright 2004-2013 the original author or authors.
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


import griffon.util.ArtifactSettings
import org.codehaus.griffon.artifacts.maven.PluginDependenciesParser
import org.codehaus.griffon.artifacts.maven.PomGenerator
import org.codehaus.griffon.artifacts.model.Plugin

import static org.codehaus.griffon.artifacts.maven.PluginDependenciesParser.traversePluginDependencies

/**
 * @author Andres Almiray
 */

if (getBinding().variables.containsKey('_griffon_package_plugin_called')) return
_griffon_package_plugin_called = true

includeTargets << griffonScript('_GriffonPackageArtifact')
includeTargets << griffonScript('_GriffonPackage')
includeTargets << griffonScript('_GriffonPackageAddon')
includeTargets << griffonScript('_GriffonDocs')
includeTargets << griffonScript('_GriffonClean')

PLUGIN_RESOURCES = [
    INCLUSIONS: [
        'griffon-app/conf/**',
        'lib/**',
        'scripts/**',
        'src/templates/**',
        'LICENSE*',
        'README*'
    ],
    EXCLUSIONS: [
        'griffon-app/conf/Application.groovy',
        'griffon-app/conf/Builder.groovy',
        'griffon-app/conf/Config.groovy',
        'griffon-app/conf/BuildConfig.groovy',
        'griffon-app/conf/metainf/**',
        '**/.svn/**',
        'test/**',
        '**/CVS/**'
    ]
]

target(name: 'packagePlugin', description: 'Packages a Griffon plugin',
    prehook: null, posthook: null) {
    depends(cleanAll)
    pluginDescriptor = ArtifactSettings.getPluginDescriptor(basedir)
    if (!pluginDescriptor?.exists()) {
        event('StatusFinal', ['Current directory does not appear to be a Griffon plugin project.'])
        exit(1)
    }

    artifactInfo = loadArtifactInfo(Plugin.TYPE, pluginDescriptor)
    pluginName = artifactInfo.name
    pluginVersion = artifactInfo.version
    packageArtifact()
}

setDefaultTarget(packagePlugin)

target(name: 'package_plugin', description: '',
    prehook: null, posthook: null) {
    depends(compile, packageAddon, pluginDocs, pluginTest)

    Map<String, List<PluginDependenciesParser.Dependency>> dependencies = [
        runtime: [],
        compile: [],
        build: [],
        test: []
    ]
    if (griffonSettings.dependencyManager.hasApplicationDependencies()) {
        ant.copy(file: "$basedir/griffon-app/conf/BuildConfig.groovy",
            tofile: "$artifactPackageDirPath/dependencies.groovy", failonerror: false)
        dependencies = traversePluginDependencies(new File("$artifactPackageDirPath/dependencies.groovy"))
    }

    File runtimeJar = new File("${artifactPackageDirPath}/dist/griffon-${pluginName}-runtime-${pluginVersion}.jar")
    File compileJar = new File("${artifactPackageDirPath}/dist/griffon-${pluginName}-compile-${pluginVersion}.jar")
    File testJar = new File("${artifactPackageDirPath}/dist/griffon-${pluginName}-test-${pluginVersion}.jar")

    Map pomParams = [:]
    pomParams.putAll(artifactInfo)
    pomParams.runtime = runtimeJar.exists()
    pomParams.compile = compileJar.exists()
    pomParams.test = testJar.exists()

    List modules = []
    String compile = ''
    PomGenerator pomGenerator = new PomGenerator(griffonSettings, pomParams, artifactPackageDirPath)
    if (runtimeJar.exists()) {
        compile = "compile(group: '${artifactInfo.group}', name: 'griffon-${pluginName}-runtime', version: '${pluginVersion}')"
        pomGenerator.generatePluginPom('runtime', dependencies.runtime + dependencies.compile)
        modules << 'runtime'
    }
    if (compileJar.exists()) {
        compile += "\n\tbuild(group: '${artifactInfo.group}', name: 'griffon-${pluginName}-compile', version: '${pluginVersion}')"
        pomGenerator.generatePluginPom('compile', dependencies.build)
        modules << 'compile'
    }
    String test = ''
    if (testJar.exists()) {
        test = "test(group: '${artifactInfo.group}', name: 'griffon-${pluginName}-test', version: '${pluginVersion}')"
        pomGenerator.generatePluginPom('test', dependencies.test)
        modules << 'test'
    }
    if (modules) {
        pomGenerator.generatePluginParentPom(modules)
        pomGenerator.generatePluginBom(modules)
    }

    if (compile || test) {
        File dependencyDescriptor = new File("${artifactPackageDirPath}/plugin-dependencies.groovy")
        dependencyDescriptor.text = """
        |griffon.project.dependency.resolution = {
        |    repositories {
        |        flatDir(name: 'plugin ${pluginName}-${pluginVersion}', dirs: [
        |            "\${pluginDirPath}/dist"
        |        ])
        |    }
        |    dependencies {
        |        ${compile.trim()}
        |        $test
        |    }
        |}""".stripMargin().trim()
    }
}

target(name: 'post_package_plugin', description: '',
    prehook: null, posthook: null) {
    ant.zip(destfile: "${artifactPackageDirPath}/${artifactZipFileName}", update: true, filesonly: true) {
        fileset(dir: basedir) {
            PLUGIN_RESOURCES.INCLUSIONS.each {
                include(name: it)
            }
            PLUGIN_RESOURCES.EXCLUSIONS.each {
                exclude(name: it)
            }
        }

        if (descriptorInstance.metaClass.hasProperty(descriptorInstance, 'pluginIncludes')) {
            def additionalIncludes = descriptorInstance.pluginIncludes
            if (additionalIncludes) {
                zipfileset(dir: basedir) {
                    additionalIncludes.each { f ->
                        include(name: f)
                    }
                }
            }
        }
    }
}

target(name: 'pluginDocs', description: 'Generates and packages plugin documentation',
    prehook: null, posthook: null) {
    pluginDocDir = "${artifactPackageDirPath}/docs"
    ant.mkdir(dir: pluginDocDir)

    // copy 'raw' docs if they exists
    def srcDocsMisc = new File("${basedir}/src/docs/misc")
    if (srcDocsMisc.exists()) {
        ant.copy(todir: pluginDocDir, failonerror: false) {
            fileset(dir: srcDocsMisc)
        }
    }

    // package sources
    def srcMainDir = new File("${basedir}/src/main")
    def projectTestDir = new File("${basedir}/src/test")
    def projectTestDirPath = new File(griffonSettings.testClassesDir, 'shared')

    boolean hasSrcMain = hasJavaOrGroovySources(srcMainDir)
    boolean hasTestSources = hasJavaOrGroovySources(projectTestDir)
    List sources = []
    List excludedPaths = ['resources', 'i18n', 'conf']
    for (dir in new File("${basedir}/griffon-app").listFiles()) {
        if (!excludedPaths.contains(dir.name) && dir.isDirectory() &&
            ant.fileset(dir: dir, includes: '**/*.groovy, **/*.java').size() > 0) {
            sources << dir.absolutePath
        }
    }
    buildConfig.griffon?.plugin?.pack?.additional?.sources?.each { source ->
        File dir = new File("${basedir}/${source}")
        if (dir.isDirectory() && ant.fileset(dir: dir, excludes: '**/CVS/**, **/.svn/**').size() > 0) {
            sources << dir.absolutePath
        }
    }

    if (isAddonPlugin || hasSrcMain || hasTestSources || sources) {
        String jarFileName = "${artifactPackageDirPath}/docs/griffon-${pluginName}-runtime-${pluginVersion}-sources.jar"

        ant.uptodate(property: 'pluginSourceJarUpToDate', targetfile: jarFileName) {
            sources.each { d ->
                srcfiles(dir: d, excludes: '**/CVS/**, **/.svn/**')
            }
            srcfiles(dir: basedir, includes: '*GriffonAddon*')
            if (hasSrcMain) srcfiles(dir: srcMainDir, includes: '**/*')
            if (hasTestSources) srcfiles(dir: projectTestDir, includes: '**/*')
            srcfiles(dir: projectMainClassesDir, includes: '**/*')
            if (hasTestSources) srcfiles(dir: projectTestDirPath, includes: '**/*')
        }
        boolean uptodate = ant.antProject.properties.pluginSourceJarUpToDate
        File metainfDir = new File("${basedir}/griffon-app/conf/metainf")
        if (!uptodate) {
            ant.jar(destfile: jarFileName) {
                sources.each { d -> fileset(dir: d, excludes: '**/CVS/**, **/.svn/**') }
                fileset(dir: basedir, includes: '*GriffonAddon*')
                if (hasSrcMain) fileset(dir: srcMainDir, includes: '**/*.groovy, **/*.java')
                if (hasTestSources) fileset(dir: projectTestDir, includes: '**/*.groovy, **/*.java')
                if (metainfDir.list()) {
                    metainf(dir: metainfDir)
                }
            }
        }

        List groovydocSources = []
        sources.each { source ->
            File dir = new File(source)
            if (ant.fileset(dir: dir, includes: '**/*.groovy, **/*.java').size() > 0) {
                groovydocSources << dir
            }
        }

        if (!argsMap.nodoc && (hasSrcMain || hasTestSources || groovydocSources)) {
            File javadocDir = new File("${projectTargetDir}/docs/api")
            invokeGroovydoc(destdir: javadocDir,
                sourcepath: [srcMainDir, projectTestDir] + groovydocSources,
                windowtitle: "${pluginName} ${pluginVersion}",
                doctitle: "${pluginName} ${pluginVersion}")
            if (javadocDir.list()) {
                jarFileName = "${artifactPackageDirPath}/docs/griffon-${pluginName}-runtime-${pluginVersion}-javadoc.jar"
                ant.jar(destfile: jarFileName) {
                    fileset(dir: javadocDir)
                }
                ant.delete(dir: javadocDir, quiet: true)
            }
        }
    }

    if (cliSourceDir.exists() && hasJavaOrGroovySources(cliSourceDir)) {
        String jarFileName = "${artifactPackageDirPath}/docs/griffon-${pluginName}-compile-${pluginVersion}-sources.jar"
        ant.jar(destfile: jarFileName) {
            fileset(dir: cliSourceDir)
        }

        if (!argsMap.nodoc) {
            File javadocDir = new File("${projectTargetDir}/docs/cli-api")
            invokeGroovydoc(destdir: javadocDir,
                sourcepath: [cliSourceDir],
                windowtitle: "${pluginName} CLI ${pluginVersion}",
                doctitle: "${pluginName} CLI ${pluginVersion}")
            if (javadocDir.list()) {
                jarFileName = "${artifactPackageDirPath}/docs/griffon-${pluginName}-compile-${pluginVersion}-javadoc.jar"
                ant.jar(destfile: jarFileName) {
                    fileset(dir: javadocDir)
                }
                ant.delete(dir: javadocDir, quiet: true)
            }
        }
    }
}

target(name: 'pluginTest', description: '',
    prehook: null, posthook: null) {
    def projectTestDir = new File("${basedir}/src/test")
    def testResourcesDir = new File("${basedir}/test/resources")

    boolean hasTestSources = hasJavaOrGroovySources(projectTestDir)
    boolean hasTestResources = hasFiles(dir: testResourcesDir, excludes: '**/*.svn/**, **/CVS/**')

    if (hasTestSources || hasTestResources) {
        String jarFileName = "${artifactPackageDirPath}/dist/griffon-${pluginName}-test-${pluginVersion}.jar"

        ant.uptodate(property: 'pluginTestJarUpToDate', targetfile: jarFileName) {
            if (hasTestSources) {
                srcfiles(dir: projectTestDir, includes: "**/*")
                srcfiles(dir: projectTestClassesDir, includes: "**/*")
            }
            if (hasTestResources) {
                srcfiles(dir: testResourcesDir, includes: "**/*")
                srcfiles(dir: griffonSettings.testResourcesDir, includes: "**/*")
            }
        }
        boolean uptodate = ant.antProject.properties.pluginTestJarUpToDate
        if (!uptodate) {
            ant.jar(destfile: jarFileName) {
                if (hasTestSources) fileset(dir: projectTestClassesDir, includes: '**/*.class')
                if (hasTestResources) fileset(dir: testResourcesDir)
            }
        }
    }
}