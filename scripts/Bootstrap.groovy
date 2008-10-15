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
//import org.codehaus.griffon.commons.GriffonClassUtils as GCU
//import org.codehaus.griffon.commons.ApplicationAttributes;
//import org.codehaus.griffon.commons.GriffonApplication;
//import org.codehaus.griffon.commons.ApplicationHolder;
//import org.codehaus.griffon.commons.spring.GriffonRuntimeConfigurator;
//import org.springframework.context.ApplicationContext;
//import org.codehaus.griffon.plugins.*
//import org.springframework.core.io.*
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.mock.web.MockServletContext
//import org.codehaus.griffon.cli.support.CommandLineResourceLoader;
//import griffon.spring.*
//import org.springframework.web.context.WebApplicationContext
//import org.codehaus.groovy.tools.LoaderConfiguration
//import org.codehaus.groovy.tools.RootLoader
//import org.codehaus.griffon.cli.support.GriffonRootLoader

Ant.property(environment:"env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

includeTargets << new File ( "${griffonHome}/scripts/Package.groovy" )

target ('default': "This target will load the Griffon application context into the command window with a variable named 'ctx'") {
    depends(packageApp)
    bootstrap()
}

parentContext = null // default parent context is null

target(loadApp:"Loads the Griffon application object") {
    event("AppLoadStart", ["Loading Griffon Application"])
//    profile("Loading parent ApplicationContext") {
//        def builder = parentContext ? new WebBeanBuilder(parentContext) :  new WebBeanBuilder()
//        beanDefinitions = builder.beans {
//            resourceHolder(org.codehaus.griffon.commons.spring.GriffonResourceHolder) {
//                this.resources = "file:${basedir}/**/griffon-app/**/*.groovy"
//            }
//            griffonResourceLoader(org.codehaus.griffon.commons.GriffonResourceLoaderFactoryBean) {
//                griffonResourceHolder = resourceHolder
//            }
//            griffonApplication(org.codehaus.griffon.commons.DefaultGriffonApplication.class, ref("griffonResourceLoader"))
//            pluginMetaManager(DefaultPluginMetaManager, resolveResources("file:${basedir}/plugins/*/plugin.xml"))
//        }
//    }
//
//    appCtx = beanDefinitions.createApplicationContext()
//    def ctx = appCtx
//
    // The mock servlet context needs to resolve resources relative to the 'web-app'
    // directory. We also need to use a FileSystemResourceLoader, otherwise paths are
    // evaluated against the classpath - not what we want!
//    servletContext = new MockServletContext('web-app', new FileSystemResourceLoader())
//    ctx.servletContext = servletContext
//    griffonApp = ctx.griffonApplication 
//    ApplicationHolder.application = griffonApp
//    classLoader = griffonApp.classLoader
//      packageApp()
//    PluginManagerHolder.pluginManager = null
//    loadPlugins()
//    pluginManager = PluginManagerHolder.pluginManager
//    pluginManager.application = griffonApp
//    pluginManager.doArtefactConfiguration()
//    griffonApp.initialise()

    File jardir = new File(Ant.antProject.replaceProperties(config.griffon.jars.destDir))
    rootLoader.addURL(new File("${jardir}/${config.griffon.jars.jarName}").toURI().toURL())

    griffonApp = rootLoader.loadClass("griffon.application.SingleFrameApplication", false).newInstance()
    griffonApp.bootstrap()

    event("AppLoadEnd", ["Loading Griffon Application"])
}
target(configureApp:"Configures the Griffon application and builds an ApplicationContext") {
//    event("ConfigureAppStart", [griffonApp, appCtx])
//    appCtx.resourceLoader = new  CommandLineResourceLoader()
//    profile("Performing runtime Spring configuration") {
//        def config = new org.codehaus.griffon.commons.spring.GriffonRuntimeConfigurator(griffonApp,appCtx)
//       appCtx = config.configure(servletContext)
//        servletContext.setAttribute(ApplicationAttributes.APPLICATION_CONTEXT,appCtx );
//        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx);
//    }
//    event("ConfigureAppEnd", [griffonApp, appCtx])
}

monitorCallback = {}

target(monitorApp:"Monitors an application for changes using the PluginManager and reloads changes") {
//    long lastModified = classesDir.lastModified()
//    while(true) {
//        sleep(3500)
//        try {
//            pluginManager.checkForChanges()
//
//            lastModified = recompileCheck(lastModified) {
//                compile()
//                ClassLoader contextLoader = Thread.currentThread().getContextClassLoader()
//                classLoader = new URLClassLoader([classesDir.toURL()] as URL[], contextLoader.rootLoader)
//                Thread.currentThread().setContextClassLoader(classLoader)
//                // reload plugins
//                loadPlugins()
//                loadApp()
//                configureApp()
//                monitorCallback()
//            }
//
//        } catch (Exception e) {
//            println e.message
//        }
//    }
}

target(bootstrap: "The implementation target") {
    depends(loadApp, configureApp)
}
