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

package org.codehaus.griffon.runtime.core;

import griffon.core.*;
import griffon.exceptions.MVCGroupInstantiationException;
import griffon.util.CollectionUtils;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.Script;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.ConfigUtils.getConfigValueAsString;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;
import static org.codehaus.griffon.runtime.builder.CompositeBuilderHelper.createBuilder;
import static org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation.castToBoolean;

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public class DefaultMVCGroupManager extends AbstractMVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMVCGroupManager.class);
    private static final String CONFIG_KEY_COMPONENT = "component";
    private static final String CONFIG_KEY_EVENTS_LIFECYCLE = "events.lifecycle";
    private static final String CONFIG_KEY_EVENTS_INSTANTIATION = "events.instantiation";
    private static final String CONFIG_KEY_EVENTS_LISTENER = "events.listener";

    public DefaultMVCGroupManager(GriffonApplication app) {
        super(app);
    }

    public MVCGroupConfiguration newMVCGroupConfiguration(String mvcType, Map<String, String> members, Map<String, Object> config) {
        return new DefaultMVCGroupConfiguration(getApp(), mvcType, members, config);
    }

    public MVCGroup newMVCGroup(MVCGroupConfiguration configuration, String mvcId, Map<String, Object> members) {
        return new DefaultMVCGroup(getApp(), configuration, mvcId, members);
    }

    protected void doInitialize(Map<String, MVCGroupConfiguration> configurations) {
        for (MVCGroupConfiguration configuration : configurations.values()) {
            addConfiguration(configuration);
        }
    }

    protected MVCGroup buildMVCGroup(MVCGroupConfiguration configuration, String mvcId, Map<String, Object> args) {
        if (args == null) args = Collections.EMPTY_MAP;

        boolean component = castToBoolean(configuration.getConfig().get(CONFIG_KEY_COMPONENT));
        boolean checkId = true;

        if (isBlank(mvcId)) {
            if (component) {
                checkId = false;
            } else {
                mvcId = configuration.getMvcType();
            }
        }

        if (checkId) checkIdIsUnique(mvcId, configuration);

        if (LOG.isInfoEnabled())
            LOG.info("Building MVC group '" + configuration.getMvcType() + "' with name '" + mvcId + "'");
        Map<String, Object> argsCopy = copyAndConfigureArguments(args, configuration, mvcId);

        // figure out what the classes are and prep the metaclass
        Map<String, MetaClass> metaClassMap = new LinkedHashMap<String, MetaClass>();
        Map<String, Class> klassMap = new LinkedHashMap<String, Class>();
        Map<String, GriffonClass> griffonClassMap = new LinkedHashMap<String, GriffonClass>();
        for (Map.Entry<String, String> memberEntry : configuration.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            String memberClassName = memberEntry.getValue();
            selectClassesPerMember(memberType, memberClassName, klassMap, metaClassMap, griffonClassMap);
        }

        // create the builder
        FactoryBuilderSupport builder = createBuilder(getApp(), metaClassMap);

        boolean isEventPublishingEnabled = getApp().isEventPublishingEnabled();
        getApp().setEventPublishingEnabled(isConfigFlagEnabled(configuration, CONFIG_KEY_EVENTS_INSTANTIATION));
        Map<String, Object> instances = null;
        try {
            instances = instantiateMembers(klassMap, argsCopy, griffonClassMap, builder);
        } finally {
            getApp().setEventPublishingEnabled(isEventPublishingEnabled);
        }

        instances.put("builder", builder);
        argsCopy.put("builder", builder);

        MVCGroup group = newMVCGroup(configuration, mvcId, instances);
        // must set it again because mvcId might have been initialized internally
        argsCopy.put("mvcName", group.getMvcId());
        argsCopy.put("mvcId", group.getMvcId());
        argsCopy.put("mvcGroup", group);

        for (Map.Entry<String, Object> variable : argsCopy.entrySet()) {
            builder.setVariable(variable.getKey(), variable.getValue());
        }

        boolean fireEvents = isConfigFlagEnabled(configuration, CONFIG_KEY_EVENTS_LIFECYCLE);
        if (fireEvents) {
            getApp().event(GriffonApplication.Event.INITIALIZE_MVC_GROUP.getName(), asList(configuration, group));
        }

        // special case --
        // controllers are added as application listeners
        // addApplicationListener method is null safe
        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LISTENER)) {
            getApp().addApplicationEventListener(group.getController());
        }

        // mutually set each other to the available fields and inject args
        fillReferencedProperties(group, argsCopy);

        if (checkId) doAddGroup(group);

        initializeMembers(group, argsCopy);

        if (fireEvents) getApp().event(GriffonApplication.Event.CREATE_MVC_GROUP.getName(), asList(group));

        return group;
    }

    protected void selectClassesPerMember(String memberType, String memberClassName, Map<String, Class> klassMap, Map<String, MetaClass> metaClassMap, Map<String, GriffonClass> griffonClassMap) {
        GriffonClass griffonClass = getApp().getArtifactManager().findGriffonClass(memberClassName);
        Class klass = griffonClass != null ? griffonClass.getClazz() : loadClass(memberClassName);
        MetaClass metaClass = griffonClass != null ? griffonClass.getMetaClass() : GroovySystem.getMetaClassRegistry().getMetaClass(klass);
        klassMap.put(memberType, klass);
        metaClassMap.put(memberType, metaClass);
        griffonClassMap.put(memberType, griffonClass);
    }

    protected Map<String, Object> copyAndConfigureArguments(Map<String, Object> args, MVCGroupConfiguration configuration, String mvcId) {
        Map<String, Object> argsCopy = CollectionUtils.<String, Object>map()
                .e("app", getApp())
                .e("mvcType", configuration.getMvcType())
                .e("mvcName", mvcId)
                .e("mvcId", mvcId)
                .e("configuration", configuration);

        argsCopy.putAll(getApp().getBindings().getVariables());
        argsCopy.putAll(args);
        for (String methodName : UIThreadManager.THREADING_METHOD_NAMES) {
            argsCopy.remove(methodName);
        }
        return argsCopy;
    }

    protected void checkIdIsUnique(String mvcId, MVCGroupConfiguration configuration) {
        if (findGroup(mvcId) != null) {
            String action = getConfigValueAsString(getApp().getConfig(), "griffon.mvcid.collision", "exception");
            if ("warning".equalsIgnoreCase(action)) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("A previous instance of MVC group '" + configuration.getMvcType() + "' with name '" + mvcId + "' exists. Destroying the old instance first.");
                    destroyMVCGroup(mvcId);
                }
            } else {
                throw new MVCGroupInstantiationException("Can not instantiate MVC group '" + configuration.getMvcType() + "' with name '" + mvcId + "' because a previous instance with that name exists and was not disposed off properly.", configuration.getMvcType(), mvcId);
            }
        }
    }

    protected Map<String, Object> instantiateMembers(Map<String, Class> klassMap, Map<String, Object> args, Map<String, GriffonClass> griffonClassMap, FactoryBuilderSupport builder) {
        // instantiate the parts
        Map<String, Object> instanceMap = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Class> classEntry : klassMap.entrySet()) {
            String memberType = classEntry.getKey();
            Class memberClass = classEntry.getValue();
            if (args.containsKey(memberType)) {
                // use provided value, even if null
                instanceMap.put(memberType, args.get(memberType));
            } else {
                // otherwise create a new value
                GriffonClass griffonClass = griffonClassMap.get(memberType);
                Object instance = null;
                if (griffonClass != null) {
                    instance = griffonClass.newInstance();
                } else {
                    instance = getApp().newInstance(memberClass, memberType);
                }
                instanceMap.put(memberType, instance);
                args.put(memberType, instance);

                // all scripts get the builder as their binding
                if (instance instanceof Script) {
                    builder.getVariables().putAll(((Script) instance).getBinding().getVariables());
                    ((Script) instance).setBinding(builder);
                }
            }
        }
        return instanceMap;
    }

    protected void initializeMembers(MVCGroup group, Map<String, Object> args) {
        // initialize the classes and call scripts
        if (LOG.isDebugEnabled()) LOG.debug("Initializing each MVC member of group '" + group.getMvcId() + "'");
        for (Map.Entry<String, Object> memberEntry : group.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            Object member = memberEntry.getValue();
            if (member instanceof Script) {
                group.buildScriptMember(memberType);
            } else if (!"builder".equalsIgnoreCase(memberType)) {
                try {
                    InvokerHelper.invokeMethod(member, "mvcGroupInit", new Object[]{args});
                } catch (MissingMethodException mme) {
                    if ("mvcGroupInit".equals(mme.getMethod())) {
                        throw mme;
                    }
                    // MME on mvcGroupInit means they didn't define
                    // an init method.  This is not an error.
                }
            }
        }
    }

    protected void fillReferencedProperties(MVCGroup group, Map<String, Object> args) {
        for (Object member : group.getMembers().values()) {
            // loop on the instance map to get just the instances
            if (member instanceof Script) {
                ((Script) member).getBinding().getVariables().putAll(args);
            } else {
                // set the args and instances
                InvokerHelper.setProperties(member, args);
            }
        }
    }

    protected void doAddGroup(MVCGroup group) {
        addGroup(group);
    }

    public void destroyMVCGroup(String mvcId) {
        MVCGroup group = findGroup(mvcId);
        if (LOG.isDebugEnabled()) LOG.trace("Group '" + mvcId + "' points to " + group);
        if (group == null) return;
        if (LOG.isInfoEnabled()) LOG.info("Destroying MVC group identified by '" + mvcId + "'");

        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LISTENER)) {
            getApp().removeApplicationEventListener(group.getController());
        }

        for (Map.Entry<String, Object> memberEntry : group.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            Object member = memberEntry.getValue();
            if (!"builder".equalsIgnoreCase(memberType) && (member != null) && !(member instanceof Script)) {
                try {
                    InvokerHelper.invokeMethod(member, "mvcGroupDestroy", new Object[0]);
                } catch (MissingMethodException mme) {
                    if ("mvcGroupDestroy".equals(mme.getMethod())) {
                        throw mme;
                    }
                    // MME on mvcGroupDestroy means they didn't define
                    // a destroy method.  This is not an error.
                }
            }
        }

        try {
            if (group.getBuilder() != null) {
                group.getBuilder().dispose();
                group.getBuilder().getVariables().clear();
            }
        } catch (MissingMethodException mme) {
            // TODO find out why this call breaks applet mode on shutdown
            if (LOG.isErrorEnabled())
                LOG.error("Application encountered an error while destroying group '" + mvcId + "'", sanitize(mme));
        }

        doRemoveGroup(group);
        group.destroy();

        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LIFECYCLE)) {
            getApp().event(GriffonApplication.Event.DESTROY_MVC_GROUP.getName(), asList(group));
        }
    }

    protected void doRemoveGroup(MVCGroup group) {
        removeGroup(group);
    }

    protected Class loadClass(String className) {
        try {
            return getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // ignored
        }
        return null;
    }

    protected boolean isConfigFlagEnabled(MVCGroupConfiguration configuration, String key) {
        return getConfigValueAsBoolean(configuration.getConfig(), key, true);
    }
}
