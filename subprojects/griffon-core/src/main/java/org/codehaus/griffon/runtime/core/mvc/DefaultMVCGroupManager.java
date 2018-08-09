/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.core.mvc;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.ApplicationClassLoader;
import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.artifact.ArtifactManager;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.exceptions.FieldException;
import griffon.exceptions.GriffonException;
import griffon.exceptions.GriffonViewInitializationException;
import griffon.exceptions.MVCGroupInstantiationException;
import griffon.exceptions.NewInstanceException;
import griffon.inject.Contextual;
import griffon.inject.MVCMember;
import griffon.util.CollectionUtils;
import griffon.util.Instantiator;
import org.codehaus.griffon.runtime.core.injection.InjectionUnitOfWork;
import org.kordamp.jsr377.converter.FormattingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.application.converter.Converter;
import javax.application.converter.ConverterRegistry;
import javax.application.converter.NoopConverter;
import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.AnnotationUtils.annotationsOfMethodParameter;
import static griffon.util.AnnotationUtils.findAnnotation;
import static griffon.util.AnnotationUtils.namesFor;
import static griffon.util.AnnotationUtils.parameterTypeAt;
import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.GriffonClassUtils.getAllDeclaredFields;
import static griffon.util.GriffonClassUtils.getPropertyDescriptors;
import static griffon.util.GriffonClassUtils.setFieldValue;
import static griffon.util.GriffonClassUtils.setPropertiesOrFieldsNoException;
import static griffon.util.GriffonClassUtils.setPropertyOrFieldValueNoException;
import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultMVCGroupManager extends AbstractMVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMVCGroupManager.class);

    protected static final String ERROR_VALUE_NULL = "Argument 'value' must not be null";
    protected static final String CONFIG_KEY_COMPONENT = "component";
    protected static final String CONFIG_KEY_EVENTS_LIFECYCLE = "events.lifecycle";
    protected static final String CONFIG_KEY_EVENTS_INSTANTIATION = "events.instantiation";
    protected static final String CONFIG_KEY_EVENTS_DESTRUCTION = "events.destruction";
    protected static final String CONFIG_KEY_EVENTS_LISTENER = "events.listener";
    protected static final String KEY_PARENT_GROUP = "parentGroup";

    protected final ApplicationClassLoader applicationClassLoader;
    protected final Instantiator instantiator;

    @Inject
    public DefaultMVCGroupManager(@Nonnull GriffonApplication application,
                                  @Nonnull ApplicationClassLoader applicationClassLoader,
                                  @Nonnull Instantiator instantiator) {
        super(application);
        this.applicationClassLoader = requireNonNull(applicationClassLoader, "Argument 'applicationClassLoader' must not be null");
        this.instantiator = requireNonNull(instantiator, "Argument 'instantiator' must not be null");
    }

    protected void doInitialize(@Nonnull Map<String, MVCGroupConfiguration> configurations) {
        requireNonNull(configurations, "Argument 'configurations' must not be null");
        for (MVCGroupConfiguration configuration : configurations.values()) {
            addConfiguration(configuration);
        }
    }

    @Nonnull
    protected MVCGroup createMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args) {
        requireNonNull(configuration, ERROR_CONFIGURATION_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);

        mvcId = resolveMvcId(configuration, mvcId);
        checkIdIsUnique(mvcId, configuration);

        LOG.debug("Building MVC group '{}' with name '{}'", configuration.getMvcType(), mvcId);
        Map<String, Object> argsCopy = copyAndConfigureArguments(args, configuration, mvcId);

        // figure out what the classes are
        Map<String, ClassHolder> classMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> memberEntry : configuration.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            String memberClassName = memberEntry.getValue();
            selectClassesPerMember(memberType, memberClassName, classMap);
        }

        boolean isEventPublishingEnabled = getApplication().getEventRouter().isEventPublishingEnabled();
        getApplication().getEventRouter().setEventPublishingEnabled(isConfigFlagEnabled(configuration, CONFIG_KEY_EVENTS_INSTANTIATION));
        Map<String, Object> instances = new LinkedHashMap<>();
        List<Object> injectedInstances = new ArrayList<>();

        try {
            InjectionUnitOfWork.start();
        } catch (IllegalStateException ise) {
            throw new MVCGroupInstantiationException("Can not instantiate MVC group '" + configuration.getMvcType() + "' with id '" + mvcId + "'", configuration.getMvcType(), mvcId, ise);
        }

        try {
            instances.putAll(instantiateMembers(classMap, argsCopy));
        } finally {
            getApplication().getEventRouter().setEventPublishingEnabled(isEventPublishingEnabled);
            try {
                injectedInstances.addAll(InjectionUnitOfWork.finish());
            } catch (IllegalStateException ise) {
                throw new MVCGroupInstantiationException("Can not instantiate MVC group '" + configuration.getMvcType() + "' with id '" + mvcId + "'", configuration.getMvcType(), mvcId, ise);
            }
        }

        MVCGroup group = newMVCGroup(configuration, mvcId, instances, (MVCGroup) args.get(KEY_PARENT_GROUP));
        adjustMvcArguments(group, argsCopy);

        boolean fireEvents = isConfigFlagEnabled(configuration, CONFIG_KEY_EVENTS_LIFECYCLE);
        if (fireEvents) {
            getApplication().getEventRouter().publishEvent(ApplicationEvent.INITIALIZE_MVC_GROUP.getName(), asList(configuration, group));
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
        if (group instanceof AbstractMVCGroup) {
            ((AbstractMVCGroup) group).getInjectedInstances().addAll(injectedInstances);
        }

        if (fireEvents) {
            getApplication().getEventRouter().publishEvent(ApplicationEvent.CREATE_MVC_GROUP.getName(), singletonList(group));
        }

        return group;
    }

    protected void adjustMvcArguments(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        // must set it again because mvcId might have been initialized internally
        args.put("mvcId", group.getMvcId());
        args.put("mvcGroup", group);
        args.put("application", getApplication());
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    protected String resolveMvcId(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId) {
        boolean component = getConfigValueAsBoolean(configuration.getConfig(), CONFIG_KEY_COMPONENT, false);

        if (isBlank(mvcId)) {
            if (component) {
                mvcId = configuration.getMvcType() + "-" + System.nanoTime();
            } else {
                mvcId = configuration.getMvcType();
            }
        }
        return mvcId;
    }

    @SuppressWarnings("unchecked")
    protected void selectClassesPerMember(@Nonnull String memberType, @Nonnull String memberClassName, @Nonnull Map<String, ClassHolder> classMap) {
        GriffonClass griffonClass = getApplication().getArtifactManager().findGriffonClass(memberClassName);
        ClassHolder classHolder = new ClassHolder();
        if (griffonClass != null) {
            classHolder.artifactClass = (Class<? extends GriffonArtifact>) griffonClass.getClazz();
        } else {
            classHolder.regularClass = loadClass(memberClassName);
        }
        classMap.put(memberType, classHolder);
    }

    @Nonnull
    protected Map<String, Object> copyAndConfigureArguments(@Nonnull Map<String, Object> args, @Nonnull MVCGroupConfiguration configuration, @Nonnull String mvcId) {
        Map<String, Object> argsCopy = CollectionUtils.<String, Object>map()
            .e("application", getApplication())
            .e("mvcType", configuration.getMvcType())
            .e("mvcId", mvcId)
            .e("configuration", configuration);

        if (args.containsKey(KEY_PARENT_GROUP) && args.get(KEY_PARENT_GROUP) instanceof MVCGroup) {
            MVCGroup parentGroup = (MVCGroup) args.get(KEY_PARENT_GROUP);
            for (Map.Entry<String, Object> e : parentGroup.getMembers().entrySet()) {
                args.put("parent" + capitalize(e.getKey()), e.getValue());
            }
        }

        argsCopy.putAll(args);
        return argsCopy;
    }

    protected void checkIdIsUnique(@Nonnull String mvcId, @Nonnull MVCGroupConfiguration configuration) {
        if (findGroup(mvcId) != null) {
            String action = getApplication().getConfiguration().getAsString("griffon.mvcid.collision", "exception");
            if ("warning".equalsIgnoreCase(action)) {
                LOG.warn("A previous instance of MVC group '{}' with id '{}' exists. Destroying the old instance first.", configuration.getMvcType(), mvcId);
                destroyMVCGroup(mvcId);
            } else {
                throw new MVCGroupInstantiationException("Can not instantiate MVC group '" + configuration.getMvcType() + "' with id '" + mvcId + "' because a previous instance with that name exists and was not disposed off properly.", configuration.getMvcType(), mvcId);
            }
        }
    }

    @Nonnull
    protected Map<String, Object> instantiateMembers(@Nonnull Map<String, ClassHolder> classMap, @Nonnull Map<String, Object> args) {
        // instantiate the parts
        Map<String, Object> instanceMap = new LinkedHashMap<>();
        for (Map.Entry<String, ClassHolder> classEntry : classMap.entrySet()) {
            String memberType = classEntry.getKey();
            if (args.containsKey(memberType)) {
                // use provided value, even if null
                instanceMap.put(memberType, args.get(memberType));
            } else {
                // otherwise create a new value
                ClassHolder classHolder = classEntry.getValue();
                if (classHolder.artifactClass != null) {
                    Class<? extends GriffonArtifact> memberClass = classHolder.artifactClass;
                    ArtifactManager artifactManager = getApplication().getArtifactManager();
                    GriffonClass griffonClass = artifactManager.findGriffonClass(memberClass);
                    GriffonArtifact instance = artifactManager.newInstance(griffonClass);
                    instanceMap.put(memberType, instance);
                    args.put(memberType, instance);
                } else {
                    Class<?> memberClass = classHolder.regularClass;
                    try {
                        Object instance = instantiator.instantiate(memberClass);
                        instanceMap.put(memberType, instance);
                        args.put(memberType, instance);
                    } catch (RuntimeException e) {
                        LOG.error("Can't create member {} with {}", memberType, memberClass);
                        throw new NewInstanceException(memberClass, e);
                    }
                }
            }
        }
        return instanceMap;
    }

    protected void initializeMembers(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        LOG.debug("Initializing each MVC member of group '{}'", group.getMvcId());
        for (Map.Entry<String, Object> memberEntry : group.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            Object member = memberEntry.getValue();
            if (member instanceof GriffonArtifact) {
                initializeArtifactMember(group, memberType, (GriffonArtifact) member, args);
            } else {
                initializeNonArtifactMember(group, memberType, member, args);
            }
        }
    }

    protected void initializeArtifactMember(@Nonnull final MVCGroup group, @Nonnull String type, @Nonnull final GriffonArtifact member, @Nonnull final Map<String, Object> args) {
        if (member instanceof GriffonView) {
            getApplication().getUIThreadManager().executeInsideUISync(() -> {
                try {
                    GriffonView view = (GriffonView) member;
                    view.initUI();
                } catch (RuntimeException e) {
                    throw (RuntimeException) sanitize(new GriffonViewInitializationException(group.getMvcType(), group.getMvcId(), member.getClass().getName(), e));
                }
                ((GriffonMvcArtifact) member).mvcGroupInit(args);
            });
        } else if (member instanceof GriffonMvcArtifact) {
            ((GriffonMvcArtifact) member).mvcGroupInit(args);
        }
    }

    protected void initializeNonArtifactMember(@Nonnull MVCGroup group, @Nonnull String type, @Nonnull Object member, @Nonnull Map<String, Object> args) {
        // empty
    }

    protected abstract static class InjectionPoint {
        protected final ConverterRegistry converterRegistry;
        protected final String name;
        protected final boolean nullable;
        protected final Kind kind;
        protected final Class<?> type;
        protected final String format;
        protected final Class<? extends Converter> converter;

        protected InjectionPoint(ConverterRegistry converterRegistry, String name, boolean nullable, Kind kind, Class<?> type, String format, Class<? extends Converter> converter) {
            this.converterRegistry = converterRegistry;
            this.name = name;
            this.nullable = nullable;
            this.kind = kind;
            this.type = type; this.format = format;
            this.converter = converter;
        }

        protected enum Kind {
            MEMBER,
            CONTEXTUAL,
            OTHER
        }

        protected abstract void apply(@Nonnull MVCGroup group, @Nonnull String memberType, @Nonnull Object instance, @Nonnull Map<String, Object> args);

        @Nonnull
        protected Object convertValue(@Nonnull Class<?> type, @Nonnull Object value, @Nullable String format, @Nonnull Class<? extends Converter> converter) {
            requireNonNull(type, ERROR_TYPE_NULL);
            requireNonNull(value, ERROR_VALUE_NULL);

            Converter resolvedConverter = resolveConverter(type, format, converter);
            if (isNoopConverter(resolvedConverter.getClass())) { return value; }
            if (value instanceof CharSequence) {
                return resolvedConverter.fromObject(String.valueOf(value));
            } else {
                return resolvedConverter.fromObject(value);
            }
        }

        @Nonnull
        protected Converter resolveConverter(@Nonnull Class<?> type, @Nullable String format, @Nonnull Class<? extends Converter> converter) {
            requireNonNull(type, ERROR_TYPE_NULL);

            Converter resolvedConverter = null;
            if (isNoopConverter(converter)) {
                resolvedConverter = converterRegistry.findConverter(type);
            } else {
                try {
                    resolvedConverter = converter.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new GriffonException("Could not instantiate converter with " + converter, e);
                }
            }

            if (resolvedConverter instanceof FormattingConverter) {
                ((FormattingConverter) resolvedConverter).setFormat(format);
            }
            return resolvedConverter;
        }

        protected boolean isNoopConverter(@Nonnull Class<? extends Converter> converter) {
            return NoopConverter.class.isAssignableFrom(converter);
        }
    }

    protected static class FieldInjectionPoint extends InjectionPoint {
        protected final Field field;

        protected FieldInjectionPoint(ConverterRegistry converterRegistry, String name, boolean nullable, Kind kind, Class<?> type, Field field, String format, Class<? extends Converter> converter) {
            super(converterRegistry, name, nullable, kind, type, format, converter);
            this.field = field;
        }

        @Override
        protected void apply(@Nonnull MVCGroup group, @Nonnull String memberType, @Nonnull Object instance, @Nonnull Map<String, Object> args) {
            String[] keys = namesFor(field);
            Object argValue = args.get(name);

            if (kind == Kind.CONTEXTUAL) {
                for (String key : keys) {
                    if (group.getContext().containsKey(key)) {
                        argValue = group.getContext().get(key);
                        break;
                    }
                }
            }

            try {
                if (argValue == null) {
                    if (!nullable) {
                        if (kind == Kind.CONTEXTUAL) {
                            throw new IllegalStateException("Could not find an instance of type " +
                                field.getType().getName() + " under keys '" + Arrays.toString(keys) +
                                "' in the context of MVCGroup[" + group.getMvcType() + ":" + group.getMvcId() +
                                "] to be injected on field '" + field.getName() +
                                "' in " + kind + " (" + resolveMemberClass(instance).getName() + "). Field does not accept null values.");
                        } else if (kind == Kind.MEMBER) {
                            throw new IllegalStateException("Could not inject argument on field '"
                                + name + "' in " + memberType + " (" + resolveMemberClass(instance).getName() +
                                "). Field does not accept null values.");
                        }
                    }
                    return;
                } else if (kind == Kind.MEMBER && (!isNoopConverter(converter) || !type.isAssignableFrom(argValue.getClass()))) {
                    argValue = convertValue(type, argValue, format, converter);
                }

                setFieldValue(instance, name, argValue);
                if (kind == Kind.OTHER) {
                    LOG.warn("Field '" + name + "' in " + memberType + " (" + resolveMemberClass(instance).getName() +
                        ") must be annotated with @" + MVCMember.class.getName() + ".");
                }
            } catch (Exception e) {
                throw new MVCGroupInstantiationException("Unexpected error when setting value for " + resolveMemberClass(instance).getName() + "." + field.getName(), group.getMvcType(), group.getMvcId(), e);
            }
        }
    }

    protected static class MethodInjectionPoint extends InjectionPoint {
        protected final Method method;

        protected MethodInjectionPoint(ConverterRegistry converterRegistry, String name, boolean nullable, Kind kind, Class<?> type, Method method, String format, Class<? extends Converter> converter) {
            super(converterRegistry, name, nullable, kind, type, format, converter);
            this.method = method;
        }

        @Override
        protected void apply(@Nonnull MVCGroup group, @Nonnull String memberType, @Nonnull Object instance, @Nonnull Map<String, Object> args) {
            if (kind == Kind.CONTEXTUAL) {
                String[] keys = namesFor(method);
                Object argValue = args.get(name);

                for (String key : keys) {
                    if (group.getContext().containsKey(key)) {
                        argValue = group.getContext().get(key);
                        break;
                    }
                }

                try {
                    if (argValue == null && !nullable) {
                        throw new IllegalStateException("Could not find an instance of type " +
                            method.getParameterTypes()[0].getName() + " under keys '" + Arrays.toString(keys) +
                            "' in the context of MVCGroup[" + group.getMvcType() + ":" + group.getMvcId() +
                            "] to be injected on property '" + name +
                            "' in " + kind + " (" + resolveMemberClass(instance).getName() + "). Property does not accept null values.");
                    }

                    method.invoke(instance, argValue);
                } catch (Exception e) {
                    throw new MVCGroupInstantiationException(group.getMvcType(), group.getMvcId(), e);
                }
            } else {
                try {
                    Object argValue = args.get(name);
                    if (argValue == null) {
                        if (!nullable) {
                            if (kind == Kind.MEMBER) {
                                throw new IllegalStateException("Could not inject argument on property '" +
                                    name + "' in " + memberType + " (" + resolveMemberClass(instance).getName() +
                                    "). Property does not accept null values.");
                            }
                        }
                        return;
                    } else if (kind == Kind.MEMBER && (!isNoopConverter(converter) || !type.isAssignableFrom(argValue.getClass()))) {
                        argValue = convertValue(type, argValue, format, converter);
                    }

                    method.invoke(instance, argValue);
                    if (kind == Kind.OTHER) {
                        LOG.warn("Property '" + name + "' in " + memberType + " (" + resolveMemberClass(instance).getName() +
                            ") must be annotated with @" + MVCMember.class.getName() + ".");
                    }
                } catch (Exception e) {
                    throw new MVCGroupInstantiationException("Unexpected error when invoking " + resolveMemberClass(instance).getName() + "." + method.getName(), group.getMvcType(), group.getMvcId(), e);
                }
            }
        }
    }

    protected void fillReferencedProperties(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        for (Map.Entry<String, Object> memberEntry : group.getMembers().entrySet()) {
            String memberType = memberEntry.getKey();
            Object member = memberEntry.getValue();

            Map<String, Object> argsCopy = new LinkedHashMap<>(args);

            Map<String, Field> fields = new LinkedHashMap<>();
            for (Field field : getAllDeclaredFields(resolveMemberClass(member))) {
                fields.put(field.getName(), field);
            }
            Map<String, InjectionPoint> injectionPoints = new LinkedHashMap<>();
            for (PropertyDescriptor descriptor : getPropertyDescriptors(resolveMemberClass(member))) {
                Method method = descriptor.getWriteMethod();
                if (method == null || isInjectable(method)) { continue; }
                Class<?> type = parameterTypeAt(method, 0);
                boolean nullable = method.getAnnotation(Nonnull.class) == null && findAnnotation(annotationsOfMethodParameter(method, 0), Nonnull.class) == null;
                InjectionPoint.Kind kind = resolveKind(method);
                String format = resolveFormat(method);
                Class<? extends Converter> converter = resolveEditor(method);
                Field field = fields.get(descriptor.getName());
                if (field != null && kind == InjectionPoint.Kind.OTHER) {
                    kind = resolveKind(field);
                    nullable = field.getAnnotation(Nonnull.class) == null;
                    type = field.getType();
                    format = resolveFormat(field);
                    converter = resolveEditor(field);
                }
                injectionPoints.put(descriptor.getName(), new MethodInjectionPoint(converterRegistry, descriptor.getName(), nullable, kind, type, method, format, converter));
            }

            for (Field field : getAllDeclaredFields(resolveMemberClass(member))) {
                if (Modifier.isStatic(field.getModifiers()) || isInjectable(field)) { continue; }
                if (!injectionPoints.containsKey(field.getName())) {
                    boolean nullable = field.getAnnotation(Nonnull.class) == null;
                    InjectionPoint.Kind kind = resolveKind(field);
                    Class<?> type = field.getType();
                    String format = resolveFormat(field);
                    Class<? extends Converter> converter = resolveEditor(field);
                    injectionPoints.put(field.getName(), new FieldInjectionPoint(converterRegistry, field.getName(), nullable, kind, type, field, format, converter));
                }
            }

            for (InjectionPoint ip : injectionPoints.values()) {
                ip.apply(group, memberType, member, args);
                argsCopy.remove(ip.name);
            }

            /*
            for (Map.Entry<String, Object> e : argsCopy.entrySet()) {
                try {
                    setPropertyOrFieldValue(member, e.getKey(), e.getValue());
                    LOG.warn("Property '" + e.getKey() + "' in " + memberType + " (" + resolveMemberClass(member).getName() +
                        ") must be annotated with @" + MVCMember.class.getName() + ".");
                } catch (PropertyException ignored) {
                    // OK
                }
            }
            */
            setPropertiesOrFieldsNoException(member, argsCopy);
        }
    }

    @Nonnull
    protected InjectionPoint.Kind resolveKind(@Nonnull AnnotatedElement element) {
        if (isContextual(element)) {
            return InjectionPoint.Kind.CONTEXTUAL;
        } else if (isMvcMember(element)) {
            return InjectionPoint.Kind.MEMBER;
        }
        return InjectionPoint.Kind.OTHER;
    }

    @Nonnull
    protected String resolveFormat(@Nonnull AnnotatedElement element) {
        if (isMvcMember(element)) {
            return element.getAnnotation(MVCMember.class).format();
        }
        return "";
    }

    @Nonnull
    protected Class<? extends Converter> resolveEditor(@Nonnull AnnotatedElement element) {
        if (isMvcMember(element)) {
            return element.getAnnotation(MVCMember.class).converter();
        }
        return NoopConverter.class;
    }

    protected boolean isContextual(AnnotatedElement element) {
        return element != null && element.getAnnotation(Contextual.class) != null;
    }

    protected boolean isInjectable(AnnotatedElement element) {
        return element != null && element.getAnnotation(Inject.class) != null;
    }

    protected boolean isMvcMember(AnnotatedElement element) {
        return element != null && element.getAnnotation(MVCMember.class) != null;
    }

    protected void doAddGroup(@Nonnull MVCGroup group) {
        addGroup(group);
    }

    public void destroyMVCGroup(@Nonnull String mvcId) {
        MVCGroup group = findGroup(mvcId);
        LOG.debug("Group '{}' points to {}", mvcId, group);

        if (group == null) { return; }

        LOG.debug("Destroying MVC group identified by '{}'", mvcId);

        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LISTENER)) {
            GriffonController controller = group.getController();
            if (controller != null) {
                getApplication().getEventRouter().removeEventListener(controller);
            }
        }

        boolean fireDestructionEvents = isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_DESTRUCTION);

        destroyMembers(group, fireDestructionEvents);

        doRemoveGroup(group);
        group.destroy();

        if (isConfigFlagEnabled(group.getConfiguration(), CONFIG_KEY_EVENTS_LIFECYCLE)) {
            getApplication().getEventRouter().publishEvent(ApplicationEvent.DESTROY_MVC_GROUP.getName(), singletonList(group));
        }
    }

    protected void destroyMembers(@Nonnull MVCGroup group, boolean fireDestructionEvents) {
        for (Map.Entry<String, Object> memberEntry : group.getMembers().entrySet()) {
            Object member = memberEntry.getValue();
            if (member instanceof GriffonArtifact) {
                destroyArtifactMember(memberEntry.getKey(), (GriffonArtifact) member, fireDestructionEvents);
            } else {
                destroyNonArtifactMember(memberEntry.getKey(), member, fireDestructionEvents);
            }

        }

        if (group instanceof AbstractMVCGroup) {
            List<Object> injectedInstances = ((AbstractMVCGroup) group).getInjectedInstances();
            for (Object instance : injectedInstances) {
                getApplication().getInjector().release(instance);
            }
            injectedInstances.clear();
        }
    }

    protected void destroyArtifactMember(@Nonnull String type, @Nonnull GriffonArtifact member, boolean fireDestructionEvents) {
        if (member instanceof GriffonMvcArtifact) {
            final GriffonMvcArtifact artifact = (GriffonMvcArtifact) member;
            if (fireDestructionEvents) {
                getApplication().getEventRouter().publishEvent(ApplicationEvent.DESTROY_INSTANCE.getName(), asList(artifact.getTypeClass(), artifact));
            }

            if (artifact instanceof GriffonView) {
                getApplication().getUIThreadManager().executeInsideUISync(() -> {
                    try {
                        artifact.mvcGroupDestroy();
                    } catch (RuntimeException e) {
                        throw (RuntimeException) sanitize(e);
                    }
                });
            } else {
                artifact.mvcGroupDestroy();
            }

            // clear all parent* references
            for (String parentMemberName : new String[]{"parentModel", "parentView", "parentController", "parentGroup"}) {
                setPropertyOrFieldValueNoException(member, parentMemberName, null);
            }
        }

        destroyContextualMemberProperties(type, member);
    }

    protected void destroyContextualMemberProperties(@Nonnull String type, @Nonnull GriffonArtifact member) {
        for (Field field : getAllDeclaredFields(member.getTypeClass())) {
            if (isContextual(field) && !field.getType().isPrimitive()) {
                try {
                    setFieldValue(member, field.getName(), null);
                } catch (FieldException e) {
                    throw new IllegalStateException("Could not nullify field '" +
                        field.getName() + "' in " + type + " (" + member.getTypeClass().getName() + ")", e);
                }
            }
        }
    }

    protected void destroyNonArtifactMember(@Nonnull String type, @Nonnull Object member, boolean fireDestructionEvents) {
        // empty
    }

    protected void doRemoveGroup(@Nonnull MVCGroup group) {
        removeGroup(group);
    }

    protected boolean isConfigFlagEnabled(@Nonnull MVCGroupConfiguration configuration, @Nonnull String key) {
        return getConfigValueAsBoolean(configuration.getConfig(), key, true);
    }

    @Nonnull
    private static Class<?> resolveMemberClass(@Nonnull Object member) {
        if (member instanceof GriffonArtifact) {
            return ((GriffonArtifact) member).getTypeClass();
        }
        return member.getClass();
    }

    @Nullable
    protected Class<?> loadClass(@Nonnull String className) {
        try {
            return applicationClassLoader.get().loadClass(className);
        } catch (ClassNotFoundException e) {
            // #39 do not ignore this CNFE
            throw new GriffonException(e.toString(), e);
        }
    }

    protected static final class ClassHolder {
        protected Class<?> regularClass;
        protected Class<? extends GriffonArtifact> artifactClass;
    }
}
