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

import grails.doc.DocPublisher
import griffon.util.ArtifactSettings
import griffon.util.GriffonNameUtils
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.documentation.PdfBuilder
import org.codehaus.griffon.resolve.IvyDependencyManager

/**
 * @author Graeme Rocher (Grails 1.0)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_docs_called")) return
_griffon_docs_called = true

includeTargets << griffonScript('_GriffonCompile')
includeTargets << griffonScript('_GriffonPackage')
includeTargets << griffonScript('_GriffonPackageArtifact')

// javadocDir = "${griffonSettings.docsOutputDir}/api"
groovydocDir = "${griffonSettings.docsOutputDir}/api"
docEncoding = "UTF-8"
docSourceLevel = "1.5"
links = [
        [packages: "java.,org.xml.,javax.,org.xml.", href: "http://java.sun.com/j2se/1.5.0/docs/api"],
        [packages: "org.apache.ant.,org.apache.tools.ant.", href: "http://www.dpml.net/api/ant/1.7.0"],
        [packages: "org.junit.,junit.framework.", href: "http://junit.sourceforge.net/junit3.8.1/javadoc/"],
        [packages: "groovy.,org.codehaus.groovy.", href: "http://groovy.codehaus.org/api/"],
        [packages: 'griffon.,org.codehaus.griffon.', href: 'http://dist.codehaus.org/griffon/guide/api/']
]

docsDisabled = { argsMap.nodoc == true }
pdfEnabled = { argsMap.pdf == true }

createdManual = false
createdPdf = false

ant.taskdef(name: "groovydoc", classname: "org.codehaus.groovy.ant.Groovydoc")

target(docs: "Produces documentation for a Griffon project") {
    if (argsMap.init) {
        ant.mkdir(dir: "${basedir}/src/docs/img")
        ant.copy(todir: "${basedir}/src/docs/img") {
            fileset(dir: "${griffonHome}/media/", includes: "*.png")
        }
        ant.mkdir(dir: "${basedir}/src/docs/guide")
        ant.mkdir(dir: "${basedir}/src/docs/ref/Items")
        new File("${basedir}/src/docs/guide/1. Introduction.gdoc").write '''
This is an example documentation template. The syntax format is similar to "Textile":http://textile.thresholdstate.com/.
        
You can apply formatting such as *bold*, _italic_ and @code@. Bullets are possible too:
        
* Bullet 1 
* Bullet 2
        
As well as numbered lists:
        
# Number 1
# Number 2
        
The documentation also handles links to [guide items|guide:1. Introduction] as well as [reference|items]        
        '''

        new File("${basedir}/src/docs/ref/Items/reference.gdoc").write '''
h1. example

h2. Purpose

This is an example reference item. 

h2. Examples

You can use code snippets:
    
{code}
def example = new Example()
{code}
        
h2. Description

And provide a detailed description        
        '''

        println "Example documentation created in ${basedir}/src/docs. Use 'griffon doc' to publish."
    } else {
        docsInternal()
    }
}

target(docsInternal: "Actual documentation task") {
    depends(compile, /*javadoc,*/ groovydoc, refdocs, pdf, createIndex)

    ant.replace(dir: "${griffonSettings.docsOutputDir}/manual") {
        include(name: "**/*.html")
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.application.name@", value: GriffonNameUtils.getPropertyName(griffonAppName))
        replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll(/\s/, '.').toLowerCase())
    }
}

target(setupDoc: "Sets up the doc directories") {
    ant.mkdir(dir: griffonSettings.docsOutputDir)
    ant.mkdir(dir: groovydocDir)
    // ant.mkdir(dir:javadocDir)
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    dependencyManager.loadDependencies('docs')
}

target(groovydoc: "Produces groovydoc documentation") {
    depends(setupDoc)

    if (docsDisabled()) {
        event("DocSkip", ['groovydoc'])
        return
    }

    ant.taskdef(name: "groovydoc", classname: "org.codehaus.groovy.ant.Groovydoc")
    event("DocStart", ['groovydoc'])
    try {
        invokeGroovydoc(
                destdir: groovydocDir,
                windowtitle: "${griffonAppName} ${griffonAppVersion}",
                doctitle: "${griffonAppName} ${griffonAppVersion}",
                sourcepath: "src/main, griffon-app/controllers, griffon-app/models, griffon-app/services"
        )
    }
    catch (Exception e) {
        event("StatusError", ["Error generating groovydoc: ${e.message}"])
    }
    event("DocEnd", ['groovydoc'])
}

target(javadoc: "Produces javadoc documentation") {
    depends(setupDoc)

    if (docsDisabled()) {
        event("DocSkip", ['javadoc'])
        return
    }

    event("DocStart", ['javadoc'])
    File javaDir = new File("${griffonSettings.sourceDir}/main")
    if (javaDir.listFiles().find { !it.name.startsWith(".")}) {
        try {
            invokeJavadoc(access: "protected",
                    destdir: javadocDir,
                    encoding: docEncoding,
                    classpathref: "griffon.compile.classpath",
                    use: "yes",
                    windowtitle: griffonAppName,
                    docencoding: docEncoding,
                    charset: docEncoding,
                    source: docSourceLevel,
                    useexternalfile: "yes",
                    breakiterator: "true",
                    linksource: "yes",
                    maxmemory: "128m",
                    failonerror: false,
                    sourcepath: javaDir.absolutePath) {
            }
        }
        catch (Exception e) {
            event("StatusError", ["Error generating javadoc: ${e.message}"])
            // ignore, empty src/java directory
        }
    }
    event("DocEnd", ['javadoc'])
}

target(refdocs: "Generates Griffon style reference documentation") {
    depends(createConfig, setupDoc)

    if (docsDisabled()) return

    def srcDocs = new File("${basedir}/src/docs")
    def srcDocsGuide = new File("${basedir}/src/docs/guide")
    def srcDocsRef = new File("${basedir}/src/docs/ref")
    def srcDocsImg = new File("${basedir}/src/docs/img")

    if (srcDocsGuide.exists() || srcDocsRef.exists()) {
        File refDocsDir = new File("${griffonSettings.docsOutputDir}/manual")
        ant.mkdir(dir: "${refDocsDir}/img")
        if (srcDocsImg.exists()) {
            ant.copy(todir: "${refDocsDir}/img") {
                fileset(dir: srcDocsImg, includes: "*.png")
            }
        }
        def publisher = new DocPublisher(srcDocs, refDocsDir)
        publisher.ant = ant
        publisher.title = griffonAppName
        publisher.subtitle = griffonAppName
        publisher.version = griffonAppVersion
        publisher.authors = ""
        publisher.license = ""
        publisher.copyright = ""
        publisher.footer = ""
        publisher.engineProperties = buildConfig?.griffon?.doc
        // if this is a plugin obtain additional metadata from the plugin
        readPluginMetadataForDocs(publisher)
        readDocProperties(publisher)
        configureAliases()

        publisher.publish()

        createdManual = true

        println "Built user manual at ${refDocsDir}/index.html"
    }
}

target(pdf: "Produces PDF documentation") {
    File refDocsDir = new File("${griffonSettings.docsOutputDir}/manual")
    File singleHtml = new File(refDocsDir, 'guide/single.html')

    if (docsDisabled() || !pdfEnabled() || !singleHtml.exists()) {
        event("DocSkip", ['pdf'])
        return
    }

    event("DocStart", ['pdf'])

    PdfBuilder.build(griffonSettings.docsOutputDir.canonicalPath, griffonHome)

    createdPdf = true

    println "Built user manual PDF at ${refDocsDir}/guide/single.pdf"

    event("DocEnd", ['pdf'])
}

target(createIndex: "Produces an index.html page in the root directory") {
    if (docsDisabled()) {
        return
    }

    new File("${griffonSettings.docsOutputDir}/index.html").withWriter { writer ->
        writer.write """\
<html>

       <head>
               <title>$griffonAppName Documentation</title>
       </head>
    
       <body>
               <a href="api/index.html">API docs</a><br />
"""

        if (createdManual) {
            writer.write '\t\t<a href="manual/index.html">Manual (Frames)</a><br />\n'
            writer.write '\t\t<a href="manual/guide/single.html">Manual (Single)</a><br />\n'
        }

        if (createdPdf) {
            writer.write '\t\t<a href="manual/guide/single.pdf">Manual (PDF)</a><br />\n'
        }

        writer.write """\
       </body>
</html>
"""
    }
}


def readPluginMetadataForDocs(DocPublisher publisher) {
    def basePlugin = loadBasePlugin()
    if (basePlugin) {
        publisher.title = basePlugin.title
        publisher.subtitle = basePlugin.title
        publisher.version = basePlugin.version
        publisher.license = basePlugin.license
        publisher.authors = basePlugin.authors.collect([]) { it.name }.join(', ')
    }
}

def readDocProperties(DocPublisher publisher) {
    ['copyright', 'license', 'authors', 'footer', 'images',
            'css', 'style', 'encoding', 'logo', 'sponsorLogo'].each { readIfSet publisher, it }
}

def configureAliases() {
    // See http://jira.codehaus.org/browse/GRAILS-6484 for why this is soft loaded
    def docEngineClassName = "grails.doc.DocEngine"
    def docEngineClass = classLoader.loadClass(docEngineClassName)
    if (!docEngineClass) {
        throw new IllegalStateException("Failed to load $docEngineClassName to configure documentation aliases")
    }
    docEngineClass.ALIAS.putAll(buildConfig.griffon.doc.alias)
}


private readIfSet(DocPublisher publisher, String prop) {
    if (buildConfig.griffon.doc."$prop") {
        publisher[prop] = buildConfig.griffon.doc."$prop"
    }
}

private loadBasePlugin() {
    pluginDescriptor = ArtifactSettings.getPluginDescriptor(basedir)
    pluginDescriptor?.exists() ? loadArtifactInfo(Plugin.TYPE, pluginDescriptor) : null
}

// target(docs: "Produces documentation for a Griffon project") {
//    depends(compile)
//
//    if(!argsMap.nodoc) {
//        invokeGroovydoc(
//            destdir: javadocDir,
//            windowtitle: "${griffonAppName} ${griffonAppVersion}",
//            doctitle: "${griffonAppName} ${griffonAppVersion}",
//            sourcepath: "src/main, griffon-app/controllers, griffon-app/models, griffon-app/services"
//        )
//    }
// }

invokeGroovydoc = { Map args ->
    ant.mkdir(dir: args.destdir)

    Map groovydocProps = [
            use: 'true',
            'private': 'true',
            packagenames: '**.*'
    ]

    def additionalLinks = args.remove('links') ?: []
    def sources = args.remove('sourcepath') ?: ["${basedir}/src/main"]
    if (!(sources instanceof List)) sources = sources.toString().split(',')
    def groovydocSources = []
    sources.each { dir ->
        if (hasJavaOrGroovySources(dir)) groovydocSources << dir
    }

    if (groovydocSources) {
        def groovydocSourcePaths = ant.path {
            groovydocSources.each { pathElement(location: it.toString().trim()) }
        }

        ant.groovydoc([*: groovydocProps, *: args, sourcepath: groovydocSourcePaths]) {
            links.each { l -> link(packages: l.packages, href: l.href) }
            additionalLinks.each { l -> link(packages: l.packages, href: l.href) }
        }
    }
}

invokeJavadoc = { Map args ->
    Map javadocProps = [
            access: "protected",
            encoding: docEncoding,
            classpathref: "griffon.compile.classpath",
            use: "yes",
            windowtitle: griffonAppName,
            docencoding: docEncoding,
            charset: docEncoding,
            source: docSourceLevel,
            useexternalfile: "yes",
            breakiterator: "true",
            linksource: "yes",
            maxmemory: "128m"
    ]

    def additionalLinks = args.remove('links') ?: []
    def sources = args.remove('sources') ?: ["${basedir}/src/main"]
    if (!(sources instanceof List)) sources = sources.toString().split(',')

    ant.mkdir(dir: args.destdir)
    ant.javadoc([*: javadocProps, *: args]) {
        sources.each { srcdir -> fileset(dir: srcdir.toString().trim()) }
        links.each { l -> link(href: l) }
        additionalLinks.each { l -> link(href: l) }
    }
}