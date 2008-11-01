package org.codehaus.griffon

import org.codehaus.groovy.tools.*


def ant = new AntBuilder()
ant.property(environment:"env")
griffonHome = ant.antProject.properties."env.GRIFFON_HOME"

if(!griffonHome) {
	println "Environment variable GRIFFON_HOME must be set to the location of your Griffon install"
	return
}
System.setProperty("groovy.starter.conf", "${griffonHome}/conf/groovy-starter.conf")
System.setProperty("griffon.home", griffonHome)
GroovyStarter.rootLoader(["--main", "org.codehaus.griffon.cli.GriffonScriptRunner", "run-app"] as String[])	