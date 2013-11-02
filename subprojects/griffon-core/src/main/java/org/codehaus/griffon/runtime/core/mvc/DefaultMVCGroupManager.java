/*
 * Copyright 2009-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core.mvc;

import griffon.core.ApplicationClassLoader;
import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.exceptions.MVCGroupInstantiationException;
import griffon.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.GriffonClassUtils.setPropertiesNoException;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

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
    private static final String CONFIG_KEY_EVENTS_DESTRUCTION = "events.destruction";
    private static final String CONFIG_KEY_EVENTS_LISTENER = "events.listener";

    private final ApplicationClassLoader applicationClassLoader;

    @Inject
    public DefaultMVCGroupManager(@Nonnull GriffonApplication application, @Nonnull ApplicationClassLoader applicationClassLoader) {
        super(application);
        this.applicationClassLoader = requireNonNull(applicationClassLoader, "Argument 'applicationClassLoader' cannot be null");
    }

    @Nonnull
    public MVCGroupConfiguration newMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config) {
        return new DefaultMVCGroupConfiguration(this, mvcType, members, config);
    }

    @Nonnull
    public MVCGroup newMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, GriffonArtifact> members) {
        return new DefaultMVCGroup(this, configuration, mvcId, members);
    }

    protected void doInitialize(@Nonnull Map<String, MVCGroupConfiguration> configurations) {
        requireNonNull(configurations, "Argument 'configurations' cannot be null");
        for (MVCGroupConfiguration configuration : configurations.values()) {
            addConfiguration(configuration);
        }
    }

    @Nonnull
    protected MVCGroup buildMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args) {
        requireNonNull(configuration, ERROR_CONFIGURATION_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);

        boolean component = getConfigValueAsBoolean(configuration.getConfig(), CONFIG_KEY_COMPONENT, false);

        if (isBlank(mvcId)) {
            if (component) {
                mvcId = configuration.getMvcType() + "-" + System.nanoTime();
            } else {
                mvcId = configuration.getMvcType();
            }
        }

        //noinspection ConstantConditions
        checkIdIsUnique(mvcId, configuration);

        if (LOG.isInfoEnabled()) {
            LOG.info("Building MVC group '" + configuration.getMvcType() + "' with name '" + mvcId + "'");
        }
        Map<String, Object> argsCopy = copyAndConfigureArguments(args, configuration, mvcId);

        // figure out what the classes are
        Map<String, Class<? extends GriffonArtifact>> classMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> memberEntry : configuration.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            String memberClassName = memberEntry.getValue();
            selectClassesPerMember(memberType, memberClassName, classMap);
        }

        boolean isEventPublishingEnabled = getApplication().getEventRouter().isEnabled();
        getApplication().getEventRouter().setEnabled(isConfigFlagEnabled(configuration, CONFIG_KEY_EVENTS_INSTANTIATION));
        Map<String, GriffonArtifact> instances = null;
        try {
            instances = instantiateMembers(classMap, argsCopy);
        } finally {
            getApplication().getEventRouter().setEnabled(isEventPublishingEnabled);
        }

        MVCGroup group = newMVCGroup(configuration, mvcId, instances);
        // must set it again because mvcId might have been initialized internally
        argsCopy.put("mvcId", group.getMvcId());
        argsCopy.put("mvcGroup", group);

        boolean fireEvents = isConfigFlagEnabled(configuration, CONFIG_KEY_EVENTS_LIFECYCLE);
        if (fireEvents) {
            getApplication().getEventRouter().publish(ApplicationEvent.INITIALIZE_MVC_GROUP.getName(), asList(configuration, group));
        }

        // special case -- controllers are added as application listeners
        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LISTENER)) {
            GriffonController controller = group.getController();
            if (controller != null) {
                getApplication().getEventRouter().addEventListener(controller);
            }
        }

        // mutually set each other to the available fields and inject args
        fillReferencedProperties(group, argsCopy);

        doAddGroup(group);

        initializeMembers(group, argsCopy);

        if (fireEvents)
            getApplication().getEventRouter().publish(ApplicationEvent.CREATE_MVC_GROUP.getName(), asList(group));

        return group;
    }

    @SuppressWarnings("unchecked")
    protected void selectClassesPerMember(@Nonnull String memberType, @Nonnull String memberClassName, @Nonnull Map<String, Class<? extends GriffonArtifact>> classMap) {
        GriffonClass griffonClass = getApplication().getArtifactManager().findGriffonClass(memberClassName);
        Class<? extends GriffonArtifact> klass = griffonClass != null ? griffonClass.getClazz() : loadClass(memberClassName);
        classMap.put(memberType, klass);
    }

    @Nonnull
    protected Map<String, Object> copyAndConfigureArguments(@Nonnull Map<String, Object> args, @Nonnull MVCGroupConfiguration configuration, @Nonnull String mvcId) {
        Map<String, Object> argsCopy = CollectionUtils.<String, Object>map()
            .e("app", getApplication())
            .e("mvcType", configuration.getMvcType())
            .e("mvcId", mvcId)
            .e("configuration", configuration);
        argsCopy.putAll(args);
        return argsCopy;
    }

    protected void checkIdIsUnique(@Nonnull String mvcId, @Nonnull MVCGroupConfiguration configuration) {
        if (findGroup(mvcId) != null) {
            String action = getApplication().getApplicationConfiguration().getAsString("griffon.mvcid.collision", "exception");
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

    @Nonnull
    protected Map<String, GriffonArtifact> instantiateMembers(Map<String, Class<? extends GriffonArtifact>> classMap, Map<String, Object> args) {
        // instantiate the parts
        Map<String, GriffonArtifact> instanceMap = new LinkedHashMap<>();
        for (Map.Entry<String, Class<? extends GriffonArtifact>> classEntry : classMap.entrySet()) {
            String memberType = classEntry.getKey();
            Class<? extends GriffonArtifact> memberClass = classEntry.getValue();
            if (args.containsKey(memberType)) {
                // use provided value, even if null
                instanceMap.put(memberType, (GriffonArtifact) args.get(memberType));
            } else {
                // otherwise create a new value
                GriffonArtifact instance = getApplication().getArtifactManager().newInstance(memberClass, memberType);
                instanceMap.put(memberType, instance);
                args.put(memberType, instance);
            }
        }
        return instanceMap;
    }

    protected void initializeMembers(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing each MVC member of group '" + group.getMvcId() + "'");
        }
        for (Map.Entry<String, GriffonArtifact> memberEntry : group.getMembers().entrySet()) {
            GriffonArtifact member = memberEntry.getValue();
            initializeMember(member, args);
        }
    }

    protected void initializeMember(@Nonnull GriffonArtifact member, @Nonnull Map<String, Object> args) {
        if (member instanceof GriffonMvcArtifact) {
            ((GriffonMvcArtifact) member).mvcGroupInit(args);
        }
    }

    protected void fillReferencedProperties(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        for (GriffonArtifact member : group.getMembers().values()) {
            // set the args and instances
            setPropertiesNoException(member, args);
        }
    }

    protected void doAddGroup(@Nonnull MVCGroup group) {
        addGroup(group);
    }

    public void destroyMVCGroup(@Nonnull String mvcId) {
        MVCGroup group = findGroup(mvcId);
        if (LOG.isDebugEnabled()) {
            LOG.trace("Group '" + mvcId + "' points to " + group);
        }

        if (group == null) return;

        if (LOG.isInfoEnabled()) {
            LOG.info("Destroying MVC group identified by '" + mvcId + "'");
        }

        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LISTENER)) {
            GriffonController controller = group.getController();
            if (controller != null) {
                getApplication().getEventRouter().removeEventListener(controller);
            }
        }

        boolean fireDestructionEvents = isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_DESTRUCTION);

        for (Map.Entry<String, GriffonArtifact> memberEntry : group.getMembers().entrySet()) {
            GriffonArtifact member = memberEntry.getValue();
            destroyMember(member, fireDestructionEvents);
        }

        doRemoveGroup(group);
        group.destroy();

        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LIFECYCLE)) {
            getApplication().getEventRouter().publish(ApplicationEvent.DESTROY_MVC_GROUP.getName(), asList(group));
        }
    }

    protected void destroyMember(@Nonnull GriffonArtifact member, boolean fireDestructionEvents) {
        if (member instanceof GriffonMvcArtifact) {
            GriffonMvcArtifact artifact = (GriffonMvcArtifact) member;
            if (fireDestructionEvents) {
                getApplication().getEventRouter().publish(ApplicationEvent.DESTROY_INSTANCE.getName(), asList(member.getClass(), artifact.getGriffonClass().getArtifactType(), artifact));
            }
            artifact.mvcGroupDestroy();
        }
    }

    protected void doRemoveGroup(@Nonnull MVCGroup group) {
        removeGroup(group);
    }

    protected boolean isConfigFlagEnabled(@Nonnull MVCGroupConfiguration configuration, @Nonnull String key) {
        return getConfigValueAsBoolean(configuration.getConfig(), key, true);
    }

    @Nullable
    protected Class<?> loadClass(@Nonnull String className) {
        try {
            return applicationClassLoader.get().loadClass(className);
        } catch (ClassNotFoundException e) {
            // ignored
        }
        return null;
    }
}
