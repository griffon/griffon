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

//import griffon.spring.WebBeanBuilder
//import org.codehaus.griffon.support.CommandLineResourceLoader
//import org.codehaus.griffon.commons.ApplicationAttributes
//import org.codehaus.griffon.commons.ApplicationHolder
//import org.codehaus.griffon.commons.ConfigurationHolder
//import org.codehaus.griffon.plugins.DefaultPluginMetaManager
//import org.codehaus.griffon.plugins.GriffonPluginUtils
//import org.codehaus.griffon.plugins.PluginManagerHolder
//import org.codehaus.griffon.web.context.ServletContextHolder
//import org.springframework.core.io.FileSystemResourceLoader
//import org.springframework.mock.web.MockServletContext
//import org.springframework.web.context.WebApplicationContext
//import org.codehaus.groovy.griffon.support.CommandLineResourceLoader

import org.codehaus.griffon.plugins.*
import org.codehaus.griffon.commons.DefaultGriffonContext
import org.codehaus.griffon.commons.GriffonContextHolder



/**
 * Gant script that bootstraps a running Griffon instance without a
 * servlet container.
 *
 * @author Graeme Rocher
 */

includeTargets << griffonScript("_GriffonPackage")

parentContext = null // default parent context is null

target(loadApp:"Loads the Griffon application object") {
	event("AppLoadStart", ["Loading Griffon Application"])
//	profile("Loading parent ApplicationContext") {
//		def builder = parentContext ? new WebBeanBuilder(parentContext) :  new WebBeanBuilder()
//		beanDefinitions = builder.beans {
//			resourceHolder(org.codehaus.groovy.griffon.commons.spring.GriffonResourceHolder) {
//				resources = GriffonPluginUtils.getArtefactResources(basedir, resolveResources)
//			}
//			griffonResourceLoader(org.codehaus.groovy.griffon.commons.GriffonResourceLoaderFactoryBean) {
//				griffonResourceHolder = resourceHolder
//			}
//			griffonApplication(org.codehaus.groovy.griffon.commons.DefaultGriffonApplication, ref("griffonResourceLoader"))
//			pluginMetaManager(DefaultPluginMetaManager) {
//                griffonApplication = ref('griffonApplication')
//            }
//		}
//	}
//
//	appCtx = beanDefinitions.createApplicationContext()
//	def ctx = appCtx
//
    // The mock servlet context needs to resolve resources relative to the 'web-app'
    // directory. We also need to use a FileSystemResourceLoader, otherwise paths are
    // evaluated against the classpath - not what we want!
//    servletContext = new MockServletContext('web-app', new FileSystemResourceLoader())
//    ctx.servletContext = servletContext
//	griffonApp = ctx.griffonApplication
    griffonContext = new DefaultGriffonContext(new Class[0], new GroovyClassLoader(classLoader))
    GriffonContextHolder.griffonContext = griffonContext
    classLoader = griffonContext.classLoader
	packageApp()
    PluginManagerHolder.pluginManager = null
    loadPlugins()
    pluginManager = PluginManagerHolder.pluginManager
//    pluginManager.application = griffonApp
//    pluginManager.doArtefactConfiguration()
//    griffonApp.initialise()

    File jardir = new File(ant.antProject.replaceProperties(config.griffon.jars.destDir))
    rootLoader.addURL(new File("${jardir}/${config.griffon.jars.jarName}").toURI().toURL())

    griffonApp = rootLoader.loadClass("griffon.application.StandaloneApplication", false).newInstance()
    griffonApp.bootstrap()

	event("AppLoadEnd", ["Loading Griffon Application"])
}

target(configureApp:"Configures the Griffon application and builds an ApplicationContext") {
	event("ConfigureAppStart", [griffonApp/*, appCtx*/])
//    appCtx.resourceLoader = new  CommandLineResourceLoader()
//	profile("Performing runtime Spring configuration") {
//	    def config = new org.codehaus.griffon.commons.spring.GriffonRuntimeConfigurator(griffonApp,appCtx)
//        appCtx = config.configure(servletContext)
//        servletContext.setAttribute(ApplicationAttributes.APPLICATION_CONTEXT,appCtx );
//        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx);
//	}
	event("ConfigureAppEnd", [griffonApp/*, appCtx*/])
}

//target(shutdownApp:"Shuts down the running Griffon application") {
//    appCtx?.close()
//    ApplicationHolder.setApplication(null);
//    ServletContextHolder.setServletContext(null);
//    PluginManagerHolder.setPluginManager(null);
//    ConfigurationHolder.setConfig(null);
//}
//
//// Flag that determines whether the monitor loop should keep running.
//keepMonitoring = true
//
//// Callback invoked by the monitor each time it has checked for changes.
//monitorCheckCallback = {}
//
//// Callback invoked by the monitor each time it recompiles the app and
//// restarts it.
//monitorRecompileCallback = {}
//
target(monitorApp:"Monitors an application for changes using the PluginManager and reloads changes") {
//    depends(classpath)
//
//    long lastModified = classesDir.lastModified()
//    while(keepMonitoring) {
//        sleep(3500)
//        try {
//            pluginManager.checkForChanges()
//
//            lastModified = recompileCheck(lastModified) {
//                compile()
//                ClassLoader contextLoader = Thread.currentThread().getContextClassLoader()
//                classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], contextLoader.rootLoader)
//                Thread.currentThread().setContextClassLoader(classLoader)
//                // reload plugins
//                loadPlugins()
//                loadApp()
//                configureApp()
//                monitorRecompileCallback()
//            }
//
//        } catch (Exception e) {
//            logError("Error recompiling application",e)
//        } finally {
//            monitorCheckCallback()
//        }
//    }
}

target(bootstrap: "Loads and configures a Griffon instance") {
    //packageApp()
    loadApp()
    configureApp()
}
