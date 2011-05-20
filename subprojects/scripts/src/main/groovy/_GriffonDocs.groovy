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

import org.codehaus.griffon.documentation.DocumentationContext
import org.codehaus.griffon.documentation.DocumentedMethod
import org.codehaus.griffon.resolve.IvyDependencyManager;

import griffon.util.GriffonUtil
import grails.doc.DocPublisher
import org.codehaus.griffon.documentation.PdfBuilder

/**
 * @author Graeme Rocher (Grails 1.0)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_docs_called")) return
_griffon_docs_called = true

includeTargets << griffonScript("_GriffonCompile")
includeTargets << griffonScript("_GriffonPackage")

// javadocDir = "${griffonSettings.docsOutputDir}/api"
groovydocDir = "${griffonSettings.docsOutputDir}/api"
docEncoding = "UTF-8"
docSourceLevel = "1.5"
links = [
    [packages: "java.,org.xml.,javax.,org.xml.",        href: "http://java.sun.com/j2se/1.5.0/docs/api"],
    [packages: "org.apache.ant.,org.apache.tools.ant.", href: "http://www.dpml.net/api/ant/1.7.0"],
    [packages: "org.junit.,junit.framework.",           href: "http://junit.sourceforge.net/junit3.8.1/javadoc/"],
    [packages: "groovy.,org.codehaus.groovy.",          href: "http://groovy.codehaus.org/api/"],
    [packages: 'griffon.,org.codehaus.griffon.',        href: 'http://dist.codehaus.org/griffon/guide/api/']
]

docsDisabled = { argsMap.nodoc == true }
pdfEnabled = { argsMap.pdf == true }

createdManual = false
createdPdf = false

ant.taskdef(name: "groovydoc", classname: "org.codehaus.groovy.ant.Groovydoc")

target(docs: "Produces documentation for a Griffon project") {
    parseArguments()
    if(argsMap.init) {
        ant.mkdir(dir:"${basedir}/src/docs/img")
        ant.copy(todir:"${basedir}/src/docs/img") {
            fileset(dir:"${griffonHome}/media/", includes: "*.png")
        }
        ant.mkdir(dir:"${basedir}/src/docs/guide")
        ant.mkdir(dir:"${basedir}/src/docs/ref/Items")
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

target(docsInternal:"Actual documentation task") {
    depends(compile, /*javadoc,*/ groovydoc, refdocs, pdf, createIndex)
}

target(setupDoc:"Sets up the doc directories") {
    ant.mkdir(dir:griffonSettings.docsOutputDir)
    ant.mkdir(dir:groovydocDir)
    // ant.mkdir(dir:javadocDir)
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    dependencyManager.loadDependencies('docs')
}

target(groovydoc:"Produces groovydoc documentation") {
    depends(parseArguments, setupDoc)

    if (docsDisabled()) {
        event("DocSkip", ['groovydoc'])
        return
    }

    ant.taskdef(name:"groovydoc", classname:"org.codehaus.groovy.ant.Groovydoc")
    event("DocStart", ['groovydoc'])
    try {
        invokeGroovydoc(
        destdir: groovydocDir,
            windowtitle: "${griffonAppName} ${griffonAppVersion}",
            doctitle: "${griffonAppName} ${griffonAppVersion}",
            sourcepath: "src/main, griffon-app/controllers, griffon-app/models, griffon-app/services"
        )
    }
    catch(Exception e) {
       event("StatusError", ["Error generating groovydoc: ${e.message}"])
    }
    event("DocEnd", ['groovydoc'])
}

target(javadoc:"Produces javadoc documentation") {
    depends(parseArguments, setupDoc)

    if (docsDisabled()) {
        event("DocSkip", ['javadoc'])
        return
    }

    event("DocStart", ['javadoc'])
    File javaDir = new File("${griffonSettings.sourceDir}/main")
    if (javaDir.listFiles().find{ !it.name.startsWith(".")}) {
       try {
           invokeJavadoc(access:"protected",
                        destdir:javadocDir,
                        encoding:docEncoding,
                        classpathref:"griffon.compile.classpath",
                        use:"yes",
                        windowtitle:griffonAppName,
                        docencoding:docEncoding,
                        charset:docEncoding,
                        source:docSourceLevel,
                        useexternalfile:"yes",
                        breakiterator:"true",
                        linksource:"yes",
                        maxmemory:"128m",
                        failonerror:false,
                        sourcepath:javaDir.absolutePath) {
           }
       }
       catch(Exception e) {
          event("StatusError", ["Error generating javadoc: ${e.message}"])
          // ignore, empty src/java directory
       }
   }
    event("DocEnd", ['javadoc'])
}

target(refdocs:"Generates Griffon style reference documentation") {
    depends(parseArguments, createConfig, loadPlugins, setupDoc)

    if (docsDisabled()) return

    def srcDocs = new File("${basedir}/src/docs")
    def srcDocsGuide = new File("${basedir}/src/docs/guide")
    def srcDocsRef = new File("${basedir}/src/docs/ref")
    def srcDocsImg = new File("${basedir}/src/docs/img")

    def context = DocumentationContext.getInstance()
    if (context?.hasMetadata()) {
        for(DocumentedMethod m in context.methods) {
            if (m.artefact && m.artefact != 'Unknown') {
                String refDir = "${srcDocs}/ref/${GriffonUtil.getNaturalName(m.artefact)}"
                ant.mkdir(dir:refDir)
                def refFile = new File("${refDir}/${m.name}.gdoc")
                if (!refFile.exists()) {
                    println "Generating documentation ${refFile}"
                    refFile.write """
h1. ${m.name}

h2. Purpose

${m.text ?: ''}

h2. Examples

{code:java}
foo.${m.name}(${m.arguments?.collect {GriffonUtil.getPropertyName(it)}.join(',')})
{code}

h2. Description

${m.text ?: ''}

Arguments:

${m.arguments?.collect { '* @'+GriffonUtil.getPropertyName(it)+'@\n' }}
"""
                }
            }
        }
    }

    if (srcDocsGuide.exists() || srcDocsRef.exists()) {
        File refDocsDir = new File("${griffonSettings.docsOutputDir}/manual")
        ant.mkdir(dir:"${refDocsDir}/img")
        if(srcDocsImg.exists()) {
            ant.copy(todir:"${refDocsDir}/img") {
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
   depends(parseArguments)

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
        if (basePlugin.hasProperty("title"))
            publisher.title = basePlugin.title
        if (basePlugin.hasProperty("description"))
            publisher.subtitle = basePlugin.description
        if (basePlugin.hasProperty("version"))
            publisher.version = basePlugin.version
        if (basePlugin.hasProperty("license"))
            publisher.license = basePlugin.license
        if (basePlugin.hasProperty("author"))
            publisher.authors = basePlugin.author
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


private readIfSet(DocPublisher publisher,String prop) {
    if (buildConfig.griffon.doc."$prop") {
        publisher[prop] = buildConfig.griffon.doc."$prop"
    }
}

private loadBasePlugin() {
    pluginManager?.allPlugins?.find { it.basePlugin }
}

// target(docs: "Produces documentation for a Griffon project") {
//    depends(compile, parseArguments)
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
    if(!(sources instanceof List)) sources = sources.toString().split(',')
    def groovydocSourcePaths = ant.path {
        sources.each { pathElement(location: it.toString().trim()) }
    }

    ant.groovydoc([*:groovydocProps, *:args, sourcepath: groovydocSourcePaths]) {
        links.each { l -> link(packages: l.packages, href: l.href) }
        additionalLinks.each { l -> link(packages: l.packages, href: l.href) }
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
    if(!(sources instanceof List)) sources = sources.toString().split(',')

    ant.mkdir(dir: args.destdir)
    ant.javadoc([*:javadocProps, *:args]) {
        sources.each { srcdir -> fileset(dir: srcdir.toString().trim()) }
        links.each { l -> link(href: l) }
        additionalLinks.each { l -> link(href: l) }
    }
}
