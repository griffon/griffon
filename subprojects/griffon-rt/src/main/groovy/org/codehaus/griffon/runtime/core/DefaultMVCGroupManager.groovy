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

import griffon.core.GriffonApplication
import griffon.core.GriffonClass
import griffon.core.MVCGroup
import griffon.core.MVCGroupConfiguration
import griffon.exceptions.MVCGroupInstantiationException
import griffon.util.GriffonExceptionHandler
import org.codehaus.griffon.runtime.builder.UberBuilder
import org.codehaus.griffon.runtime.util.CompositeBuilderHelper
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static griffon.util.GriffonNameUtils.isBlank

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
class DefaultMVCGroupManager extends AbstractMVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMVCGroupManager)
    private static final String CONFIG_KEY_COMPONENT = 'component'

    DefaultMVCGroupManager(GriffonApplication app) {
        super(app)
    }

    MVCGroupConfiguration newMVCGroupConfiguration(String mvcType, Map<String, String> members, Map<String, Object> config) {
        new DefaultMVCGroupConfiguration(app, mvcType, members, config);
    }

    MVCGroup newMVCGroup(MVCGroupConfiguration configuration, String mvcId, Map<String, Object> members) {
        new DefaultMVCGroup(app, configuration, mvcId, members);
    }

    protected void doInitialize(Map<String, MVCGroupConfiguration> configurations) {
        configurations.each { mvcType, configuration ->
            addConfiguration(configuration)
        }
    }

    protected MVCGroup buildMVCGroup(MVCGroupConfiguration configuration, String mvcId, Map<String, Object> args) {
        if (args == null) args = Collections.EMPTY_MAP

        boolean component = configuration.config[CONFIG_KEY_COMPONENT]
        boolean checkId = true

        if(isBlank(mvcId)) {
            if(component) {
                checkId = false
            } else {
                mvcId = configuration.mvcType
            }
        }

        if (checkId) checkIdIsUnique(mvcId, configuration)

        if (LOG.infoEnabled) LOG.info("Building MVC group '${configuration.mvcType}' with name '${mvcId}'")
        Map<String, Object> argsCopy = [app: app, mvcType: configuration.mvcType, mvcName: mvcId, configuration: configuration]
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

        Map<String, Object> instances = instantiateMembers(klassMap, argsCopy, griffonClassMap, builder)
        instances.builder = builder
        argsCopy.builder = builder

        MVCGroup group = newMVCGroup(configuration, mvcId, instances)
        // must set it again because mvcId might have been initialized internally
        argsCopy.mvcName = group.mvcId
        argsCopy.group = group

        app.event(GriffonApplication.Event.INITIALIZE_MVC_GROUP.name, [configuration, group])

        // special case --
        // controllers are added as application listeners
        // addApplicationListener method is null safe
        app.addApplicationEventListener(instances.controller)

        // mutually set each other to the available fields and inject args
        fillReferencedProperties(instances, argsCopy)

        if (checkId) doAddGroup(group)

        initializeMembers(mvcId, instances, argsCopy, group)

        app.event(GriffonApplication.Event.CREATE_MVC_GROUP.name, [group])
        return group
    }

    protected checkIdIsUnique(String mvcId, MVCGroupConfiguration configuration) {
        if (findGroup(mvcId)) {
            String action = app.config.griffon.mvcid.collision ?: 'exception'
            switch (action) {
                case 'warning':
                    if (LOG.warnEnabled) {
                        LOG.warn("A previous instance of MVC group '${configuration.mvcType}' with name '$mvcId' exists. Destroying the old instance first.")
                        destroyMVCGroup(mvcId)
                    }
                    break
                case 'exception':
                default:
                    throw new MVCGroupInstantiationException("Can not instantiate MVC group '${configuration.mvcType}' with name '${mvcId}' because a previous instance with that name exists and was not disposed off properly.", configuration.mvcType, mvcId)
            }
        }
    }

    protected Map<String, Object> instantiateMembers(Map<String, Class> klassMap, Map<String, Object> args, Map<String, GriffonClass> griffonClassMap, UberBuilder builder) {
        // instantiate the parts
        Map<String, Object> instanceMap = [:]
        klassMap.each {memberType, memberClass ->
            if (args.containsKey(memberType)) {
                // use provided value, even if null
                instanceMap[memberType] = args[memberType]
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
                args[memberType] = instance

                // all scripts get the builder as their binding
                if (instance instanceof Script) {
                    builder.variables.putAll(instance.binding.variables)
                    instance.binding = builder
                }
            }
        }
        return instanceMap
    }

    protected initializeMembers(String mvcId, Map<String, Object> instances, Map<String, Object> args, MVCGroup group) {
        // initialize the classes and call scripts
        if (LOG.debugEnabled) LOG.debug("Initializing each MVC member of group '${mvcId}'")
        instances.each {String memberType, member ->
            if (member instanceof Script) {
                group.buildScriptMember(memberType)
            } else if (memberType != 'builder') {
                try {
                    member.mvcGroupInit(args)
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupInit') {
                        throw mme
                    }
                    // MME on mvcGroupInit means they didn't define
                    // an init method.  This is not an error.
                }
            }
        }
    }

    protected fillReferencedProperties(Map<String, Object> instances, Map<String, Object> args) {
        instances.each {k, v ->
            // loop on the instance map to get just the instances
            if (v instanceof Script) {
                v.binding.variables.putAll(args)
            } else {
                // set the args and instances
                InvokerHelper.setProperties(v, args)
            }
        }
    }

    protected doAddGroup(MVCGroup group) {
        addGroup(group)
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

        doRemoveGroup(group)

        app.event(GriffonApplication.Event.DESTROY_MVC_GROUP.name, [group])
    }

    protected doRemoveGroup(MVCGroup group) {
        removeGroup(group)
    }
}
