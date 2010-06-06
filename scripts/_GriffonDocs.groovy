/*
 * Copyright 2004-2010 the original author or authors.
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
 * @author Graeme Rocher (Grails 1.0)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_docs_called")) return
_griffon_docs_called = true

includeTargets << griffonScript("_GriffonCompile")

javadocDir = "${basedir}/docs/api"
groovydocDir = "${basedir}/docs/gapi"
docEncoding = "UTF-8"
docSourceLevel = "1.5"

ant.taskdef(name: "groovydoc", classname: "org.codehaus.groovy.ant.Groovydoc")

target(docs: "Produces documentation for a Griffon project") {
    depends(compile, parseArguments)


    if(!argsMap.nodoc) {
        invokeGroovydoc(
            destdir: javadocDir,
            windowtitle: "${griffonAppName} ${griffonAppVersion}",
            doctitle: "${griffonAppName} ${griffonAppVersion}",
            sourcepath: "src/main, griffon-app/controllers, griffon-app/models, griffon-app/services"
        )
    }
}

invokeGroovydoc = { Map args ->
    ant.mkdir(dir: args.destdir)

    Map groovydocProps = [
        use: 'true',
        'private': 'true',
        packagenames: '**.*'
    ]

    def links = [
        [packages: "java.,org.xml.,javax.,org.xml.",        href: "http://java.sun.com/j2se/1.5.0/docs/api"],
        [packages: "org.apache.ant.,org.apache.tools.ant.", href: "http://www.dpml.net/api/ant/1.7.0"],
        [packages: "org.junit.,junit.framework.",           href: "http://junit.sourceforge.net/junit3.8.1/javadoc/"],
        [packages: "groovy.,org.codehaus.groovy.",          href: "http://groovy.codehaus.org/api/"],
        [packages: 'griffon.,org.codehaus.griffon.',        href: 'http://dist.codehaus.org/griffon/guide/api/']
    ]
    def additionalLinks = args.remove('links') ?: []
    def sources = args.remove('sourcepath') ?: ["${basedir}/src/main"]
    def groovydocSourcePaths = ant.path {
        sources.each { pathElement(location: it) }
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

    ant.mkdir(dir: args.destdir)
    ant.javadoc([*:javadocProps, *:args]) {
        sources.each { srcdir -> fileset(dir: srcdir) }
        links.each { l -> link(href: l) }
        additionalLinks.each { l -> link(href: l) }
    }
}
