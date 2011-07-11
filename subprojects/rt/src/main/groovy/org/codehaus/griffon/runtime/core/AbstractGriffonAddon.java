/*
 * Copyright 2010-2011 the original author or authors.
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

import griffon.core.GriffonAddon;
import griffon.core.GriffonApplication;
import griffon.util.GriffonNameUtils;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.util.FactoryBuilderSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of the GriffonAddon interface.
 *
 * @author Andres Almiray
 *
 * @since 0.9.2
 */
public abstract class AbstractGriffonAddon extends GroovyObjectSupport implements GriffonAddon {
    private final GriffonApplication app;
    private final Logger log;

    protected final Map<String, ?> factories = new LinkedHashMap();
    protected final Map<String, Closure> methods = new LinkedHashMap<String, Closure>();
    protected final Map<String, Map<String, Closure>> props = new LinkedHashMap<String, Map<String, Closure>>();
    protected final Map<String, Closure> events = new LinkedHashMap<String, Closure>();
    protected final Map<String, Map<String, String>> mvcGroups = new LinkedHashMap<String, Map<String, String>>();
    protected final List<Closure> attributeDelegates = new ArrayList<Closure>();
    protected final List<Closure> preInstantiateDelegates = new ArrayList<Closure>();
    protected final List<Closure> postInstantiateDelegates = new ArrayList<Closure>();
    protected final List<Closure> postNodeCompletionDelegates = new ArrayList<Closure>();
    
    public AbstractGriffonAddon(GriffonApplication app) {
        this(app, null);
    }

    protected AbstractGriffonAddon(GriffonApplication app, String loggingCategory) {
        this.app = app;
        if(GriffonNameUtils.isBlank(loggingCategory)) loggingCategory = "griffon.addon." + getClass().getName();
        log = LoggerFactory.getLogger(loggingCategory);
    }

    public GriffonApplication getApp() {
        return app;
    }

    public Logger getLog() {
        return log;
    }

    public void addonInit(GriffonApplication app) {}
    public void addonPostInit(GriffonApplication app) {}
    public void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder) {}
    public void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder) {}

    public Map<String, ?> getFactories() {
        return factories;
    }

    public Map<String, Closure> getMethods() {
        return methods;
    }

    public Map<String, Map<String, Closure>> getProps() {
        return props;
    }

    public Map<String, Closure> getEvents() {
        return events;
    }

    public Map<String, Map<String, String>> getMvcGroups() {
        return mvcGroups;
    }

    public List<Closure> getAttributeDelegates() {
        return attributeDelegates;
    }

    public List<Closure> getPreInstantiateDelegates() {
        return preInstantiateDelegates;
    }

    public List<Closure> getPostInstantiateDelegates() {
        return postInstantiateDelegates;
    }

    public List<Closure> getPostNodeCompletionDelegates() {
        return postNodeCompletionDelegates;
    }
}
