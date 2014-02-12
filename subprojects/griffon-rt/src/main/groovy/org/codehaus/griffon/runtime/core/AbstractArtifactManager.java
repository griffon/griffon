/*
 * Copyright 2009-2014 the original author or authors.
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
import griffon.util.CallableWithArgs;
import griffon.util.CallableWithArgsClosure;
import groovy.lang.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.uncapitalize;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.toList;

/**
 * Base implementation of the {@code ArtifactManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public abstract class AbstractArtifactManager implements ArtifactManager {
    private final GriffonApplication app;

    private final Map<String, ArtifactInfo[]> artifacts = new LinkedHashMap<String, ArtifactInfo[]>();
    private final Map<String, ArtifactHandler> artifactHandlers = new LinkedHashMap<String, ArtifactHandler>();
    private final Object lock = new Object();

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArtifactManager.class);
    private static final Pattern GET_METHOD_PATTERN = Pattern.compile("^get(\\w+)Classes$");
    private static final Pattern IS_METHOD_PATTERN = Pattern.compile("^is(\\w+)Class$");
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(\\w+)Classes$");

    public AbstractArtifactManager(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    protected Map<String, ArtifactInfo[]> getArtifacts() {
        return artifacts;
    }

    protected Map<String, ArtifactHandler> getArtifactHandlers() {
        return artifactHandlers;
    }

    public final void loadArtifactMetadata() {
        Map<String, List<ArtifactInfo>> loadedArtifacts = doLoadArtifactMetadata();

        synchronized (lock) {
            for (Map.Entry<String, List<ArtifactInfo>> artifactsEntry : loadedArtifacts.entrySet()) {
                String type = artifactsEntry.getKey();
                List<ArtifactInfo> list = artifactsEntry.getValue();
                artifacts.put(type, list.toArray(new ArtifactInfo[list.size()]));
                ArtifactHandler handler = artifactHandlers.get(type);
                if (handler != null) handler.initialize(artifacts.get(type));
            }
        }
    }

    // commented out generics because it fails with groovy 1.8.1
    protected abstract Map<String, List<ArtifactInfo>> doLoadArtifactMetadata();

    public void registerArtifactHandler(ArtifactHandler handler) {
        if (handler == null) return;
        if (LOG.isInfoEnabled()) {
            LOG.info("Registering artifact handler for type '" + handler.getType() + "': " + handler);
        }
        synchronized (lock) {
            artifactHandlers.put(handler.getType(), handler);
            if (artifacts.get(handler.getType()) != null) handler.initialize(artifacts.get(handler.getType()));
        }
    }

    public void unregisterArtifactHandler(ArtifactHandler handler) {
        if (handler == null) return;
        if (LOG.isInfoEnabled()) {
            LOG.info("Removing artifact handler for type '" + handler.getType() + "': " + handler);
        }
        synchronized (lock) {
            artifactHandlers.remove(handler.getType());
        }
    }

    public GriffonClass findGriffonClass(String name, String type) {
        if (isBlank(name) || isBlank(type)) return null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + type + ":" + name);
        }
        synchronized (lock) {
            ArtifactHandler handler = artifactHandlers.get(type);
            return handler != null ? handler.findClassFor(name) : null;
        }
    }

    public GriffonClass findGriffonClass(Class clazz, String type) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + type + ":" + clazz.getName());
        }
        synchronized (lock) {
            ArtifactHandler handler = artifactHandlers.get(type);
            return handler != null ? handler.getClassFor(clazz) : null;
        }
    }

    public GriffonClass findGriffonClass(Object obj) {
        if (obj == null) return null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + obj);
        }
        synchronized (lock) {
            return findGriffonClass(obj.getClass());
        }
    }

    public GriffonClass findGriffonClass(Class clazz) {
        if (clazz == null) return null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + clazz.getName());
        }
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                GriffonClass griffonClass = handler.getClassFor(clazz);
                if (griffonClass != null) return griffonClass;
            }
        }
        return null;
    }

    public GriffonClass findGriffonClass(String fqnClassName) {
        if (isBlank(fqnClassName)) return null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + fqnClassName);
        }
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                GriffonClass griffonClass = handler.getClassFor(fqnClassName);
                if (griffonClass != null) return griffonClass;
            }
        }
        return null;
    }

    public List<GriffonClass> getClassesOfType(String type) {
        synchronized (lock) {
            if (artifacts.containsKey(type)) {
                return toList(artifactHandlers.get(type).getClasses());
            }
        }
        return EMPTY_GRIFFON_CLASS_LIST;
    }

    public List<GriffonClass> getAllClasses() {
        List<GriffonClass> all = new ArrayList<GriffonClass>();
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                all.addAll(toList(handler.getClasses()));
            }
        }
        return Collections.unmodifiableList(all);
    }

    /**
     * Adds dynamic handlers for querying artifact classes.<p>
     * The following patterns are recognized<ul>
     * <li>getXXXClasses</li>
     * <li>isXXXClass</li>
     * </ul>
     * where {@code XXX} stands for the name of an artifact, like
     * "Controller" or "Service".
     */
    public Object methodMissing(final String methodName, Object args) {
        Object[] arguments = new Object[0];
        if (args != null && args.getClass().isArray()) {
            arguments = (Object[]) args;
        } else {
            arguments = new Object[]{args};
        }

        Matcher matcher = GET_METHOD_PATTERN.matcher(methodName);
        if (matcher.matches()) {
            final String artifactType = uncapitalize(matcher.group(1));
            if (arguments.length == 0 && artifacts.containsKey(artifactType)) {
                MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(ArtifactManager.class);
                if (mc instanceof ExpandoMetaClass) {
                    ExpandoMetaClass emc = (ExpandoMetaClass) mc;
                    CallableWithArgs<List<GriffonClass>> callable = new CallableWithArgs<List<GriffonClass>>() {
                        public List<GriffonClass> call(Object[] params) {
                            if (params != null) {
                                throw new MissingMethodException(methodName, ArtifactManager.class, params);
                            }
                            return getClassesOfType(artifactType);
                        }
                    };
                    emc.registerInstanceMethod(methodName, new CallableWithArgsClosure(this, callable));
                }
                return getClassesOfType(artifactType);
            }
            return EMPTY_GRIFFON_CLASS_ARRAY;
        }

        matcher = IS_METHOD_PATTERN.matcher(methodName);
        if (matcher.matches()) {
            final String artifactType = uncapitalize(matcher.group(1));
            if (arguments.length == 1 && artifacts.containsKey(artifactType)) {
                MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(ArtifactManager.class);
                if (mc instanceof ExpandoMetaClass) {
                    ExpandoMetaClass emc = (ExpandoMetaClass) mc;
                    CallableWithArgs<Boolean> callable = new CallableWithArgs<Boolean>() {
                        public Boolean call(Object[] params) {
                            if (params == null || params.length != 1 || !(params[0] instanceof Class)) {
                                throw new MissingMethodException(methodName, ArtifactManager.class, params);
                            }
                            Class klass = (Class) params[0];
                            return isClassOfType(artifactType, klass);
                        }
                    };
                    emc.registerInstanceMethod(methodName, new CallableWithArgsClosure(this, callable));
                }
                return isClassOfType(artifactType, (Class) arguments[0]);
            }
            return false;
        }

        throw new MissingMethodException(methodName, ArtifactManager.class, arguments);
    }

    /**
     * Adds dynamic handlers for querying artifact classes.<p>
     * The following patterns are recognized<ul>
     * <li>xXXClasses</li>
     * </ul>
     * where {@code xXX} stands for the name of an artifact, like
     * "controller" or "service".
     */
    public Object propertyMissing(String propertyName) {
        Matcher matcher = PROPERTY_PATTERN.matcher(propertyName);
        if (matcher.matches()) {
            final String artifactType = uncapitalize(matcher.group(1));
            if (artifacts.containsKey(artifactType)) {
                List<GriffonClass> griffonClasses = getClassesOfType(artifactType);
                MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(ArtifactManager.class);
                if (mc instanceof ExpandoMetaClass) {
                    ExpandoMetaClass emc = (ExpandoMetaClass) mc;
                    emc.registerBeanProperty(propertyName, griffonClasses);
                }
                return griffonClasses;
            }
            return EMPTY_GRIFFON_CLASS_ARRAY;
        }

        throw new MissingPropertyException(propertyName, Object.class);
    }

    protected boolean isClassOfType(String type, Class clazz) {
        for (ArtifactInfo artifactInfo : artifacts.get(type)) {
            if (artifactInfo.getClazz().getName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
