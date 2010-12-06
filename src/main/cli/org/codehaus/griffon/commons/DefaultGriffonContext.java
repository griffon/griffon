/*
* Copyright 2004-2010 the original author or authors.
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
package org.codehaus.griffon.commons;

import griffon.util.Environment;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObjectSupport;
import groovy.util.ConfigObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.griffon.commons.cfg.ConfigurationHelper;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;

/**
 * Default implementation of the GriffonApplication interface that manages application loading,
 * state, and artefact instances.
 * <p/>
 * Upon loading this GriffonApplication will inspect each class using its registered ArtefactHandler instances. Each
 * ArtefactHandler provides knowledge about the conventions used to establish its artefact type. For example
 * controllers use the ControllerArtefactHandler to establish this knowledge.
 * <p/>
 * New ArtefactHandler instances can be registered with the GriffonApplication thus allowing application extensibility.
 *
 * @author Marc Palmer (Grails 0.1)
 * @author Steven Devijver (Grails 0.1)
 * @author Graeme Rocher (Grails 0.1)
 */
public class DefaultGriffonContext extends GroovyObjectSupport implements GriffonContext {
    private GroovyClassLoader cl;
    private Class[] allClasses = new Class[0];
    private static Log log = LogFactory.getLog(DefaultGriffonContext.class);
    private ApplicationContext parentContext;
    private ApplicationContext mainContext;

    private Set loadedClasses = new HashSet();
    private Map<Object, Object> applicationMeta;
    private boolean initialised;

    /**
     * Creates a new empty Griffon application
     */
    public DefaultGriffonContext() {
        this.cl = new GroovyClassLoader();
        this.applicationMeta = loadMetadata();
    }

    /**
     * Creates a new GriffonContext instance using the given classes and GroovyClassLoader
     *
     * @param classes     The classes that make up the GriffonContext
     * @param classLoader The GroovyClassLoader to use
     */
    public DefaultGriffonContext(final Class[] classes, GroovyClassLoader classLoader) {
        if (classes == null) {
            throw new IllegalArgumentException("Constructor argument 'classes' cannot be null");
        }

        for (int i = 0; i < classes.length; i++) {
            Class aClass = classes[i];
            loadedClasses.add(aClass);
        }
        this.allClasses = classes;
        this.cl = classLoader;
        this.applicationMeta = loadMetadata();
    }

    protected Map<Object, Object> loadMetadata() {
        final Properties meta = new Properties();
        Resource r = new ClassPathResource(PROJECT_META_FILE, getClassLoader());
        try {
            meta.load(r.getInputStream());
        }
        catch (IOException e) {
            StackTraceUtils.deepSanitize(e);
            log.warn("No application metadata file found at " + r);
        }
        if (System.getProperty(Environment.KEY) != null) {
            meta.setProperty(Environment.KEY, System.getProperty(Environment.KEY));
        }
        return Collections.unmodifiableMap(meta);
    }

    public GroovyClassLoader getClassLoader() {
        return this.cl;
    }

    public ConfigObject getConfig() {
        ConfigObject c = ConfigurationHolder.getConfig();
        if (c == null) {
            c = ConfigurationHelper.loadConfigFromClasspath(this);
        }
        return c;
    }

    public Map getFlatConfig() {
        return ConfigurationHolder.getFlatConfig();
    }

    /**
     * Retrieves all classes loaded by the GriffonContext
     *
     * @return All classes loaded by the GriffonContext
     */
    public Class[] getAllClasses() {
        return this.allClasses;
    }

    /**
     * Retrieves a class from the GriffonContext for the given name
     *
     * @param className The class name
     * @return Either the java.lang.Class instance or null if it doesn't exist
     */
    public Class getClassForName(String className) {
        if (StringUtils.isBlank(className)) {
            return null;
        }
        for (int i = 0; i < allClasses.length; i++) {
            Class c = allClasses[i];
            if (c.getName().equals(className)) {
                return c;
            }
        }
        return null;
    }

    public void initialise() {
        // get all the classes that were loaded
        if (log.isDebugEnabled()) {
            log.debug("loaded classes: [" + loadedClasses + "]");
        }
        this.initialised = true;
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    public Map getMetadata() {
        return applicationMeta;
    }
}