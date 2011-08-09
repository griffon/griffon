/*
 * Copyright 2004-2011 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.GriffonClass;
import griffon.exceptions.NewInstanceCreationException;
import griffon.util.GriffonClassUtils;
import griffon.util.GriffonExceptionHandler;
import griffon.util.GriffonNameUtils;
import groovy.lang.*;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract base class for Griffon types that provides common functionality for
 * evaluating conventions within classes
 *
 * @author Steven Devijver (Grails 0.1)
 * @author Graeme Rocher (Grails 0.1)
 * @author Andres Almiray
 * @since 0.9.1
 */
public abstract class AbstractGriffonClass implements GriffonClass {
    private final Class<?> clazz;
    private final String type;
    private final GriffonApplication app;
    private final String fullName;
    private final String name;
    private final String packageName;
    private final String naturalName;
    private final String shortName;
    private final String propertyName;
    private final String logicalPropertyName;
    private final ClassPropertyFetcher classPropertyFetcher;

    protected final Set<String> eventsCache = new TreeSet<String>();
    protected final Logger log;

    /**
     * <p>Contructor to be used by all child classes to create a
     * new instance and get the name right.
     *
     * @param app
     * @param clazz
     * @param type
     * @param trailingName
     */
    public AbstractGriffonClass(GriffonApplication app, Class<?> clazz, String type, String trailingName) {
        this.app = app;
        this.clazz = clazz;
        this.type = type;
        fullName = clazz.getName();
        log = LoggerFactory.getLogger(getClass().getSimpleName() + "[" + fullName + "]");
        packageName = GriffonClassUtils.getPackageName(clazz);
        naturalName = GriffonNameUtils.getNaturalName(clazz.getName());
        shortName = GriffonClassUtils.getShortClassName(clazz);
        name = GriffonNameUtils.getLogicalName(clazz, trailingName);
        propertyName = GriffonNameUtils.getPropertyNameRepresentation(shortName);
        if (GriffonNameUtils.isBlank(name)) {
            logicalPropertyName = propertyName;
        } else {
            logicalPropertyName = GriffonNameUtils.getPropertyNameRepresentation(name);
        }
        classPropertyFetcher = ClassPropertyFetcher.forClass(clazz);
    }

    public String getShortName() {
        return shortName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getArtifactType() {
        return type;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public Object newInstance() {
        try {
            Object instance = GriffonApplicationHelper.newInstance(app, clazz, type);
            if (instance instanceof AbstractGriffonArtifact) {
                ((AbstractGriffonArtifact) instance).setApp(app);
            }
            return instance;
        } catch (Exception e) {
            Throwable targetException = null;
            if (e instanceof InvocationTargetException) {
                targetException = ((InvocationTargetException) e).getTargetException();
            } else {
                targetException = e;
            }
            throw new NewInstanceCreationException("Could not create a new instance of class " + clazz.getName(), GriffonExceptionHandler.sanitize(targetException));
        }
    }

    public String getName() {
        return name;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getLogicalPropertyName() {
        return logicalPropertyName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Object getReferenceInstance() {
        Object obj = classPropertyFetcher.getReference();
        MetaClass myMetaClass = getMetaClass();
        if (obj instanceof GroovyObject) {
            MetaClass otherMetaClass = ((GroovyObject) obj).getMetaClass();
            if (myMetaClass != otherMetaClass) {
                if (log.isDebugEnabled()) {
                    log.debug("Setting MetaClass " + myMetaClass + " on GroovyObject " + obj);
                }
                ((GroovyObject) obj).setMetaClass(myMetaClass);
            }
        } else {
            MetaClass otherMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
            if (myMetaClass != otherMetaClass) {
                if (log.isDebugEnabled()) {
                    log.debug("Setting MetaClass " + myMetaClass + " on non-GroovyObject " + obj);
                }
                GroovySystem.getMetaClassRegistry().setMetaClass(clazz, myMetaClass);
            }
        }
        return obj;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return classPropertyFetcher.getPropertyDescriptors();
    }

    /**
     * Returns an array of property names that are backed by a filed with a matching
     * name.<p>
     * Fields must be private and non-static. Names will be returned in the order
     * they are declared in the class, starting from the deepest class in the
     * class hierarchy up to the topmost superclass != null
     */
    public String[] getPropertiesWithFields() {
        return classPropertyFetcher.getPropertiesWithFields();
    }

    public Class<?> getPropertyType(String name) {
        return classPropertyFetcher.getPropertyType(name);
    }

    public boolean isReadableProperty(String name) {
        return classPropertyFetcher.isReadableProperty(name);
    }

    public boolean hasMetaMethod(String name) {
        return hasMetaMethod(name, null);
    }

    public boolean hasMetaMethod(String name, Object[] args) {
        return (getMetaClass().getMetaMethod(name, args) != null);
    }

    public boolean hasMetaProperty(String name) {
        return (getMetaClass().getMetaProperty(name) != null);
    }

    public MetaProperty[] getMetaProperties() {
        List<MetaProperty> properties = new ArrayList<MetaProperty>();
        for (MetaProperty property : getMetaClass().getProperties()) {
            if (!"class".equals(property.getName()) && !"metaClass".equals(property.getName())) {
                properties.add(property);
            }
        }

        return properties.toArray(new MetaProperty[properties.size()]);
    }

    /**
     * <p>Looks for a property of the reference instance with a given name and type.</p>
     * <p>If found its value is returned. We follow the Java bean conventions with augmentation for groovy support
     * and static fields/properties. We will therefore match, in this order:
     * </p>
     * <ol>
     * <li>Public static field
     * <li>Public static property with getter method
     * <li>Standard public bean property (with getter or just public field, using normal introspection)
     * </ol>
     *
     * @return property value or null if no property or static field was found
     */
    protected Object getPropertyOrStaticPropertyOrFieldValue(@SuppressWarnings("hiding") String name, Class<?> type) {
        Object value = classPropertyFetcher.getPropertyValue(name);
        return returnOnlyIfInstanceOf(value, type);
    }

    /**
     * Get the value of the named static property.
     *
     * @param propName
     * @param type
     * @return The property value or null
     */
    public <T> T getStaticPropertyValue(String propName, Class<T> type) {
        T value = classPropertyFetcher.getStaticPropertyValue(propName, type);
        if (value == null) {
            return getGroovyProperty(propName, type, true);
        }
        return value;
    }

    /**
     * Get the value of the named property, with support for static properties in both Java and Groovy classes
     * (which as of Groovy JSR 1.0 RC 01 only have getters in the metaClass)
     *
     * @param propName
     * @param type
     * @return The property value or null
     */
    public <T> T getPropertyValue(String propName, Class<T> type) {
        T value = classPropertyFetcher.getPropertyValue(propName, type);
        if (value == null) {
            // Groovy workaround
            return getGroovyProperty(propName, type, false);
        }
        return returnOnlyIfInstanceOf(value, type);
    }

    private <T> T getGroovyProperty(String propName, Class<T> type, boolean onlyStatic) {
        Object value = null;
        if (GroovyObject.class.isAssignableFrom(getClazz())) {
            MetaProperty metaProperty = getMetaClass().getMetaProperty(propName);
            if (metaProperty != null) {
                int modifiers = metaProperty.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    value = metaProperty.getProperty(clazz);
                } else if (!onlyStatic) {
                    value = metaProperty.getProperty(getReferenceInstance());
                }
            }
        }
        return returnOnlyIfInstanceOf(value, type);
    }

    public Object getPropertyValueObject(String propertyNAme) {
        return getPropertyValue(propertyNAme, Object.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T returnOnlyIfInstanceOf(Object value, Class<T> type) {
        if ((value != null) && (type == Object.class || GriffonClassUtils.isGroovyAssignableFrom(type, value.getClass()))) {
            return (T) value;
        }

        return null;
    }

    public Object getPropertyValue(String name) {
        return getPropertyOrStaticPropertyOrFieldValue(name, Object.class);
    }

    /**
     * Finds out if the property was defined with a Closure as value.<p>
     */
    public boolean isClosureMetaProperty(MetaProperty property) {
        Object value = property.getProperty(getReferenceInstance());

        if (value != null) return Closure.class.isAssignableFrom(value.getClass());

        if (property instanceof MetaBeanProperty) {
            // Instances of MetaBeanProperty store the closure in a descendant
            // of ClosureInvokingMethod so we only need to check the type of
            // the getter
            MetaMethod getter = ((MetaBeanProperty) property).getGetter();
            return getter instanceof ClosureInvokingMethod;
        }

        return false;
    }

    public boolean hasProperty(String propName) {
        return classPropertyFetcher.isReadableProperty(propName);
    }

    /**
     * @return the metaClass
     */
    public MetaClass getMetaClass() {
        return GriffonApplicationHelper.expandoMetaClassFor(clazz);
    }

    public void setMetaClass(MetaClass metaClass) {
        GroovySystem.getMetaClassRegistry().setMetaClass(clazz, metaClass);
    }

    public String toString() {
        return "Artifact[" + type + "] > " + getName();
    }

    public void resetCaches() {
        eventsCache.clear();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!obj.getClass().getName().equals(getClass().getName())) return false;

        GriffonClass gc = (GriffonClass) obj;
        return clazz.getName().equals(gc.getClazz().getName());
    }

    public int hashCode() {
        return clazz.hashCode() + type.hashCode();
    }

    public void updateMetaClass(Closure updater) {
        if (updater == null) return;
        updater.setDelegate(getMetaClass());
        updater.setResolveStrategy(Closure.DELEGATE_FIRST);
        updater.run();
        resetCaches();
    }

    // Any artifact can become an Event listener
    public String[] getEventNames() {
        if (eventsCache.isEmpty()) {
            for (String propertyName : getPropertiesWithFields()) {
                if (!eventsCache.contains(propertyName) &&
                        GriffonClassUtils.isEventHandler(propertyName) &&
                        getPropertyValue(propertyName, Closure.class) != null) {
                    eventsCache.add(propertyName.substring(2));
                }
            }
            for (Method method : getClazz().getMethods()) {
                String methodName = method.getName();
                if (!eventsCache.contains(methodName) &&
                        GriffonClassUtils.isPlainMethod(method) &&
                        GriffonClassUtils.isEventHandler(methodName)) {
                    eventsCache.add(methodName.substring(2));
                }
            }
            for (MetaProperty p : getMetaProperties()) {
                String propertyName = p.getName();
                if (GriffonClassUtils.isGetter(p, true)) {
                    propertyName = GriffonNameUtils.uncapitalize(propertyName.substring(3));
                }
                if (!eventsCache.contains(propertyName) &&
                        GriffonClassUtils.isEventHandler(propertyName) &&
                        isClosureMetaProperty(p)) {
                    eventsCache.add(propertyName.substring(2));
                }
            }
            for (MetaMethod method : getMetaClass().getMethods()) {
                String methodName = method.getName();
                if (!eventsCache.contains(methodName) &&
                        GriffonClassUtils.isPlainMethod(method) &&
                        GriffonClassUtils.isEventHandler(methodName)) {
                    eventsCache.add(methodName.substring(2));
                }
            }
        }

        return eventsCache.toArray(new String[eventsCache.size()]);
    }
}
