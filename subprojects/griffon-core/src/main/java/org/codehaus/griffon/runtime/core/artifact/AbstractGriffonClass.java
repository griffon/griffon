/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import griffon.util.GriffonClassUtils;
import griffon.util.GriffonNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import static griffon.util.GriffonClassUtils.isEventHandler;
import static griffon.util.GriffonClassUtils.isPlainMethod;
import static griffon.util.GriffonNameUtils.getPropertyNameRepresentation;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.runtime.core.artifact.ClassPropertyFetcher.forClass;

/**
 * Abstract base class for Griffon types that provides common functionality for
 * evaluating conventions within classes
 *
 * @author Steven Devijver (Grails 0.1)
 * @author Graeme Rocher (Grails 0.1)
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonClass implements GriffonClass {
    private static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";
    private static final String ERROR_ARTIFACT_TYPE_BLANK = "Argument 'artifactType' must not be blank";
    private static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    private static final String ERROR_APPLICATION_NULL = "Argument 'application' must not be null";

    private final GriffonApplication application;
    private final Class<?> clazz;
    private final String artifactType;
    private final String fullName;
    private final String name;
    private final String packageName;
    private final String naturalName;
    private final String shortName;
    private final String propertyName;
    private final String logicalPropertyName;
    private final ClassPropertyFetcher classPropertyFetcher;

    protected final Set<String> eventsCache = new TreeSet<>();
    protected final Logger log;

    public AbstractGriffonClass(@Nonnull GriffonApplication application, @Nonnull Class<?> type, @Nonnull String artifactType, @Nonnull String trailingName) {
        this.application = requireNonNull(application, ERROR_APPLICATION_NULL);
        this.clazz = requireNonNull(type, ERROR_TYPE_NULL);
        this.artifactType = requireNonBlank(artifactType, ERROR_ARTIFACT_TYPE_BLANK).trim();
        trailingName = isBlank(trailingName) ? "" : trailingName.trim();
        fullName = type.getName();
        log = LoggerFactory.getLogger(getClass().getSimpleName() + "[" + fullName + "]");
        packageName = GriffonClassUtils.getPackageName(type);
        naturalName = GriffonNameUtils.getNaturalName(type.getName());
        shortName = GriffonClassUtils.getShortClassName(type);
        name = GriffonNameUtils.getLogicalName(type, trailingName);
        propertyName = getPropertyNameRepresentation(shortName);
        if (isBlank(name)) {
            logicalPropertyName = propertyName;
        } else {
            logicalPropertyName = getPropertyNameRepresentation(name);
        }
        classPropertyFetcher = forClass(type);
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nullable
    @Override
    public Object getPropertyValue(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        return getPropertyOrStaticPropertyOrFieldValue(name, Object.class);
    }

    @Override
    public boolean hasProperty(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        return classPropertyFetcher.isReadableProperty(name);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getShortName() {
        return shortName;
    }

    @Nonnull
    @Override
    public String getFullName() {
        return fullName;
    }

    @Nonnull
    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Nonnull
    @Override
    public String getLogicalPropertyName() {
        return logicalPropertyName;
    }

    @Nonnull
    @Override
    public String getNaturalName() {
        return naturalName;
    }

    @Nonnull
    @Override
    public String getPackageName() {
        return packageName;
    }

    @Nonnull
    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Nonnull
    @Override
    public String getArtifactType() {
        return artifactType;
    }

    @Nullable
    @Override
    public <T> T getPropertyValue(@Nonnull String name, @Nonnull Class<T> type) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(type, ERROR_TYPE_NULL);
        return null;
    }

    public String toString() {
        return "Artifact[" + artifactType + "] > " + getName();
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!obj.getClass().getName().equals(getClass().getName()))
            return false;

        GriffonClass gc = (GriffonClass) obj;
        return clazz.getName().equals(gc.getClazz().getName());
    }

    public int hashCode() {
        return clazz.hashCode() + artifactType.hashCode();
    }

    public void resetCaches() {
        eventsCache.clear();
    }

    @Nonnull
    public String[] getEventNames() {
        if (eventsCache.isEmpty()) {
            for (Method method : getClazz().getMethods()) {
                String methodName = method.getName();
                if (!eventsCache.contains(methodName) &&
                    isPlainMethod(method) &&
                    isEventHandler(methodName)) {
                    eventsCache.add(methodName.substring(2));
                }
            }
        }

        return eventsCache.toArray(new String[eventsCache.size()]);
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
    protected Object getPropertyOrStaticPropertyOrFieldValue(@SuppressWarnings("hiding") @Nonnull String name, @Nonnull Class<?> type) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(type, ERROR_TYPE_NULL);
        Object value = classPropertyFetcher.getPropertyValue(name);
        return classPropertyFetcher.returnOnlyIfInstanceOf(value, type);
    }
}
