/*
 * Copyright 2004-2008 the original author or authors.
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
 * Gant script that loads the Griffon console
 * 
 * @author Graeme Rocher
 *
 * @since 0.4
 */

//import org.codehaus.groovy.griffon.commons.GriffonClassUtils as GCU
//import groovy.text.SimpleTemplateEngine  
//import org.codehaus.groovy.griffon.support.*
import groovy.ui.Console

Ant.property(environment:"env")                             
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"    

includeTargets << new File ( "${griffonHome}/scripts/Bootstrap.groovy" )

target ('default': "Load the Griffon interactive Swing console") {
	depends( checkVersion, configureProxy, packageApp, classpath)
	console()
}            

target(console:"The console implementation target") {

    classLoader = new URLClassLoader([classesDir.toURL()] as URL[], rootLoader)
    Thread.currentThread().setContextClassLoader(classLoader)
    loadApp()
    configureApp()
    createConsole()
    try {
        consoleui.run()
//        monitorCallback = {
//            println "Exiting console"
//            consoleui.exit()
//            createConsole()
//            println "Restarting console"
//            consoleui.run()
//        }
//        monitorApp()
        while(Console.consoleControllers) { sleep(3500) }
    } catch (Exception e) {
        event("StatusFinal", ["Error starting console: ${e.message}"])
    }
}

target(createConsole:"Creates a new console") {
    def b = new Binding()
//    b.ctx = appCtx
//    b.griffonApplication = griffonApp
    classLoader = new URLClassLoader([classesDir.toURL()] as URL[], rootLoader)
    consoleui = new Console(/*griffonApp.*/classLoader?.getRootLoader(), b)
//    consoleui.beforeExecution = {
//        appCtx.getBeansOfType(PersistenceContextInterceptor).each { k,v ->
//            v.init()
//        }
//    }
//    consoleui.afterExecution = {
//        appCtx.getBeansOfType(PersistenceContextInterceptor).each { k,v ->
//            v.flush()
//            v.destroy()
//        }
//    }

}
