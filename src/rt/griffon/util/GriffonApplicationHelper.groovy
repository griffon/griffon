/*
 * Copyright 2008 the original author or authors.
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
package griffon.util

import griffon.builder.UberBuilder
import java.awt.Toolkit
import javax.swing.JFrame
import javax.swing.SwingUtilities
import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: May 17, 2008
 * Time: 3:28:46 PM
 */
class GriffonApplicationHelper {

    static void prepare(IGriffonApplication app) {
        app.bindings.app = app

        def startDir = System.getProperty("griffon.start.dir")
        if( startDir && startDir.size() > 1 &&
            startDir.startsWith('"') && startDir.endsWith('"') ) {
            System.setProperty("griffon.start.dir", startDir[1..-2])
        }

        app.initialize();

        app.config = new ConfigSlurper().parse(app.configClass)
        app.builderConfig = new ConfigSlurper().parse(app.builderClass)
        def eventsClass = app.eventsClass
        if( eventsClass ) {
            app.eventsConfig = eventsClass.newInstance()
            app.addApplicationEventListener(app.eventsConfig)
        }
    }

    static void startup(IGriffonApplication app) {
        // init the builders
        // this is where a composite gets made and composites are added
        // for now we punt and make a SwingBuilder

        app.config.application.startupGroups.each {group ->
            createMVCGroup(app, group) 
        }

        app.startup();
    }

    /**
     * Calls the ready lifecycle mehtod after the EDT calms down
     */
    public static void callReady(IGriffonApplication app) {
        // wait for EDT to empty out.... somehow
        boolean empty = false
        while (true) {
            SwingUtilities.invokeAndWait {empty = Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null}
            if (empty) break
            sleep(100)
        }

        app.ready();
    }


    static void safeSet(reciever, property, value) {
        try {
            reciever."$property" = value
        } catch (MissingPropertyException mpe) {
            if (mpe.property != property) {
                throw mpe
            }
            /* else ignore*/
        }
    }


    public static void runScriptInsideEDT(String scriptName, IGriffonApplication app) {
        def script
        try {
            script = GriffonApplicationHelper.classLoader.loadClass(scriptName).newInstance(app.bindings)
        } catch (ClassNotFoundException cnfe) {
            if (cnfe.getMessage() == scriptName) {
                // the script must not exist, do nothing
                //LOGME - may be because of chained failures
                return
            } else {
                throw cnfe;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            script.run()
        } else {
            SwingUtilities.invokeAndWait script.&run
        }
    }


    public static Object newInstance(IGriffonApplication app, Class klass, String type) {
        def instance = klass.newInstance()
        app.event("NewInstance",[klass,type,instance])
        return instance
    }

    public static createMVCGroup(IGriffonApplication app, String mvcType) {
        createMVCGroup(app, mvcType, mvcType, [:])
    }

    public static createMVCGroup(IGriffonApplication app, String mvcType, String mvcName) {
        createMVCGroup(app, mvcType, mvcName, [:])
    }

    public static createMVCGroup(IGriffonApplication app, String mvcType, Map bindArgs) {
        createMVCGroup(app, mvcType, mvcType, bindArgs)
    }

    public static createMVCGroup(IGriffonApplication app, String mvcType, String mvcName, Map bindArgs) {
        if (!app.config.mvcGroups.containsKey(mvcType)) {
            throw new RuntimeException("Unknown MVC type \"$mvcType\".  Known types are ${app.config.mvcGroups.keySet()}")
        }

        def argsCopy = [app:app, mvcType:mvcType, mvcName:mvcName]
        argsCopy.putAll(bindArgs)


        // figure out what the classes are and prep the metaclass
        def klassMap = [:]
        ClassLoader classLoader = app.getClass().classLoader
        app.config.mvcGroups[mvcType].each {k, v ->
            Class klass = classLoader.loadClass(v);

            // inject defaults into emc
            // this also insures EMC metaclasses later
            klass.metaClass.app = app
            klass.metaClass.createMVCGroup = {Object... args ->
                GriffonApplicationHelper.createMVCGroup(app, *args)
            }
            klass.metaClass.destroyMVCGroup = GriffonApplicationHelper.&destroyMVCGroup.curry(app)
            klass.metaClass.newInstance = GriffonApplicationHelper.&newInstance.curry(app)
            klassMap[k] = klass
        }

        // create the builder
        UberBuilder builder = CompositeBuilderHelper.createBuilder(app, klassMap)
        argsCopy.each {k, v -> builder.setVariable k, v }

        // instantiate the parts
        def instanceMap = [:]
        klassMap.each {k, v ->
            if (argsCopy.containsKey(k)) {
                // use provided value, even if null
                instanceMap[k] = argsCopy[k]
            } else {
                // otherwise create a new value
                def instance = newInstance(app, v, k)
                instanceMap[k] = instance
                argsCopy[k] = instance

                // all scripts get the builder as their binding
                if (instance instanceof Script) {
                    instance.binding = builder;
                }
            }
        }
        instanceMap.builder = builder
        
        // special case --
        // controller gets applicaiton listeners
        // addApplicationListener method is null safe
        app.addApplicationEventListener(instanceMap.controller)

        // mutually set each other to the available fields
        instanceMap.each {k, v ->
            if (v instanceof Script)  {
                v.binding.variables.putAll(instanceMap)
            } else {
                InvokerHelper.setProperties(v, instanceMap)
            }
        }

        // store the refs in the app caches
        app.models[mvcName] = instanceMap.model
        app.views[mvcName] = instanceMap.view
        app.controllers[mvcName] = instanceMap.controller
        app.builders[mvcName] = instanceMap.builder
        app.groups[mvcName] = instanceMap

        // initialize the classes and call scripts
        instanceMap.each {k, v ->
            if (v instanceof Script) {
                // special case: view gets execed in the EDT always
                if (k == 'view') {
                    builder.edt({builder.build(v) })
                } else {
                    // non-view gets built in the builder
                    // they casn switch into the EDT as desired
                    builder.build(v)
                }
            } else if (k != 'builder') {
                try {
                    v.mvcGroupInit(argsCopy)
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupInit') {
                        throw mme
                    }
                    // MME on mvcGroupInit means they didn't define
                    // an init method.  This is not an error.
                }
            }
        }

        app.event("CreateMVCGroup",[mvcName, instanceMap.model, instanceMap.view, instanceMap.controller, mvcType, instanceMap])
        return new ArrayList(instanceMap.values())
    }

    public static destroyMVCGroup(IGriffonApplication app, String mvcName) {
        app.removeApplicationEventListener(app.controllers[mvcName])
        [app.models, app.views, app.controllers].each {
            def part = it.remove(mvcName)
            if ((part != null)  & !(part instanceof Script)) {
                try {
                    part.mvcGroupDestroy()
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupDestroy') {
                        throw mme
                    }
                    // MME on mvcGroupDestroy means they didn't define
                    // an init method.  This is not an error.
                }
            }
        }
        app.builders.remove(mvcName)?.dispose()
        app.event("DestroyMVCGroup",[mvcName])
    }

    public static def createJFrameApplication(IGriffonApplication app) {
        Object frame = null
        // try config specified first
        if (app.config.application?.frameClass) {
            try {
                ClassLoader cl = getClass().getClassLoader();
                if (cl) {
                    frame = cl.loadClass(app.config.application.frameClass).newInstance()
                } else {
                    frame = Class.forName(app.config.application.frameClass).newInstance()
                    frame = Class.forName(app.config.application.frameClass).newInstance()
                }
            } catch (Throwable t) {
                // ignore
            }
        }
        if (frame == null) {
            // JXFrame, it's nice.  Try it!
            try {
                ClassLoader cl = getClass().getClassLoader();
                if (cl) {
                    frame = cl.loadClass('org.jdesktop.swingx.JXFrame').newInstance()
                } else {
                    frame = Class.forName('org.jdesktop.swingx.JXFrame').newInstance()
                }
            } catch (Throwable t) {
                // ignore
            }
            // this will work for sure
            if (frame == null) {
                frame = new JFrame()
            }

            // do some standard tweaking
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        }
        return frame
    }


}
