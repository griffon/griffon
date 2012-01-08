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

package org.codehaus.griffon.runtime.core

import griffon.exceptions.MVCGroupInstantiationException
import griffon.util.GriffonExceptionHandler
import org.codehaus.griffon.runtime.builder.UberBuilder
import org.codehaus.griffon.runtime.util.CompositeBuilderHelper
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import griffon.core.*
import static griffon.util.GriffonNameUtils.isBlank

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
class DefaultMVCGroupManager extends AbstractMVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMVCGroupManager)

    DefaultMVCGroupManager(GriffonApplication app) {
        super(app)
    }

    MVCGroupConfiguration newMVCGroupConfiguration(GriffonApplication app, String mvcType, Map<String, String> members) {
        new DefaultMVCGroupConfiguration(app, mvcType, members);
    }

    MVCGroup newMVCGroup(GriffonApplication app, MVCGroupConfiguration configuration, String mvcId, Map<String, Object> members) {
        new DefaultMVCGroup(app, configuration, mvcId, members);
    }

    protected void doInitialize(Map<String, MVCGroupConfiguration> configurations) {
        configurations.each { mvcType, configuration ->
            addConfiguration(configuration)
        }
    }

    protected MVCGroup buildMVCGroup(MVCGroupConfiguration configuration, String mvcName, Map<String, Object> args) {
        if (isBlank(mvcName)) mvcName = configuration.mvcType
        if (args == null) args = Collections.EMPTY_MAP

        if (findGroup(mvcName)) {
            String action = app.config.griffon.mvcid.collision ?: 'exception'
            switch (action) {
                case 'warning':
                    if (LOG.warnEnabled) {
                        LOG.warn("A previous instance of MVC group '${configuration.mvcType}' with name '$mvcName' exists. Destroying the old instance first.")
                        destroyMVCGroup(mvcName)
                    }
                    break
                case 'exception':
                default:
                    throw new MVCGroupInstantiationException("Can not instantiate MVC group '${configuration.mvcType}' with name '${mvcName}' because a previous instance with that name exists and was not disposed off properly.", configuration.mvcType, mvcName)
            }
        }

        if (LOG.infoEnabled) LOG.info("Building MVC group '${configuration.mvcType}' with name '${mvcName}'")
        def argsCopy = [app: app, mvcType: configuration.mvcType, mvcName: mvcName]
        argsCopy.putAll(app.bindings.variables)
        argsCopy.putAll(args)

        // figure out what the classes are and prep the metaclass
        Map<String, MetaClass> metaClassMap = [:]
        Map<String, Class> klassMap = [:]
        Map<String, GriffonClass> griffonClassMap = [:]
        configuration.members.each {String memberType, String memberClassName ->
            GriffonClass griffonClass = app.artifactManager.findGriffonClass(memberClassName)
            Class klass = griffonClass?.clazz ?: Thread.currentThread().contextClassLoader.loadClass(memberClassName)
            MetaClass metaClass = griffonClass?.getMetaClass() ?: klass.getMetaClass()

            metaClassMap[memberType] = metaClass
            klassMap[memberType] = klass
            griffonClassMap[memberType] = griffonClass
        }

        // create the builder
        UberBuilder builder = CompositeBuilderHelper.createBuilder(app, metaClassMap)
        argsCopy.each {k, v -> builder.setVariable k, v }

        // instantiate the parts
        Map<String, Object> instanceMap = [:]
        klassMap.each {memberType, memberClass ->
            if (argsCopy.containsKey(memberType)) {
                // use provided value, even if null
                instanceMap[memberType] = argsCopy[memberType]
            } else {
                // otherwise create a new value
                GriffonClass griffonClass = griffonClassMap[memberType]
                def instance = null
                if (griffonClass) {
                    instance = griffonClass.newInstance()
                } else {
                    instance = app.newInstance(memberClass, memberType)
                }
                instanceMap[memberType] = instance
                argsCopy[memberType] = instance

                // all scripts get the builder as their binding
                if (instance instanceof Script) {
                    builder.variables.putAll(instance.binding.variables)
                    instance.binding = builder
                }
            }
        }
        instanceMap.builder = builder
        argsCopy.builder = builder

        MVCGroup group = newMVCGroup(app, configuration, mvcName, instanceMap)

        app.event(GriffonApplication.Event.INITIALIZE_MVC_GROUP.name, [configuration, group])

        // special case --
        // controllers are added as application listeners
        // addApplicationListener method is null safe
        app.addApplicationEventListener(instanceMap.controller)

        // mutually set each other to the available fields and inject args
        instanceMap.each {k, v ->
            // loop on the instance map to get just the instances
            if (v instanceof Script) {
                v.binding.variables.putAll(argsCopy)
            } else {
                // set the args and instances
                InvokerHelper.setProperties(v, argsCopy)
            }
        }

        addGroup(group)

        // initialize the classes and call scripts
        if (LOG.debugEnabled) LOG.debug("Initializing each MVC member of group '${mvcName}'")
        instanceMap.each {String memberType, member ->
            if (member instanceof Script) {
                // special case: view gets executed in the UI thread always
                if (memberType == 'view') {
                    UIThreadManager.instance.executeSync { builder.build(member) }
                } else {
                    // non-view gets built in the builder
                    // they can switch into the UI thread as desired
                    builder.build(member)
                }
            } else if (memberType != 'builder') {
                try {
                    member.mvcGroupInit(argsCopy)
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupInit') {
                        throw mme
                    }
                    // MME on mvcGroupInit means they didn't define
                    // an init method.  This is not an error.
                }
            }
        }

        app.event(GriffonApplication.Event.CREATE_MVC_GROUP.name, [group])
        return group
    }

    void destroyMVCGroup(String mvcName) {
        MVCGroup group = findGroup(mvcName)
        if (group == null) return
        if (LOG.infoEnabled) LOG.info("Destroying MVC group identified by '$mvcName'")
        app.removeApplicationEventListener(group.controller)
        [group.model, group.view, group.controller].each { member ->
            if ((member != null) && !(member instanceof Script)) {
                try {
                    member.mvcGroupDestroy()
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupDestroy') {
                        throw mme
                    }
                    // MME on mvcGroupDestroy means they didn't define
                    // a destroy method.  This is not an error.
                }
            }
        }

        try {
            group.builder?.dispose()
        } catch (MissingMethodException mme) {
            // TODO find out why this call breaks applet mode on shutdown
            if (LOG.errorEnabled) LOG.error("Application encountered an error while destroying group '$mvcName'", GriffonExceptionHandler.sanitize(mme))
        }

        removeGroup(group)

        app.event(GriffonApplication.Event.DESTROY_MVC_GROUP.name, [group])
    }
}
