/*
 * Copyright 2010-2012 the original author or authors.
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
import griffon.core.UIThreadManager;
import griffon.util.GriffonNameUtils;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base implementation of the GriffonAddon interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public abstract class AbstractGriffonAddon extends GroovyObjectSupport implements GriffonAddon {
    private final GriffonApplication app;
    private final Logger log;
    private final ResourceLocator resourceLocator = new ResourceLocator();

    protected final Map<String, Object> factories = new LinkedHashMap<String, Object>();
    protected final Map<String, Closure> methods = new LinkedHashMap<String, Closure>();
    protected final Map<String, Map<String, Closure>> props = new LinkedHashMap<String, Map<String, Closure>>();
    protected final Map<String, Closure> events = new LinkedHashMap<String, Closure>();
    protected final Map<String, Map<String, Object>> mvcGroups = new LinkedHashMap<String, Map<String, Object>>();
    protected final List<Closure> attributeDelegates = new ArrayList<Closure>();
    protected final List<Closure> preInstantiateDelegates = new ArrayList<Closure>();
    protected final List<Closure> postInstantiateDelegates = new ArrayList<Closure>();
    protected final List<Closure> postNodeCompletionDelegates = new ArrayList<Closure>();

    public AbstractGriffonAddon(GriffonApplication app) {
        this(app, null);
    }

    protected AbstractGriffonAddon(GriffonApplication app, String loggingCategory) {
        this.app = app;
        if (GriffonNameUtils.isBlank(loggingCategory)) loggingCategory = "griffon.addon." + getClass().getName();
        log = LoggerFactory.getLogger(loggingCategory);
    }

    public GriffonApplication getApp() {
        return app;
    }

    public Logger getLog() {
        return log;
    }

    /**
     * Creates a new instance of the specified class and type.<br/>
     * Triggers the Event.NEW_INSTANCE with the following parameters
     * <ul>
     * <li>clazz - the Class of the object</li>
     * <li>type - the symbolical type of the object</li>
     * <li>instance -> the object that was created</li>
     * </ul>
     *
     * @param clazz the Class for which an instance must be created
     * @param type  a symbolical type, for example 'controller' or 'service'. May be null.
     * @return a newly instantiated object of type <tt>clazz</tt>. Implementations must be sure
     *         to trigger an event of type Event.NEW_INSTANCE.
     */
    public Object newInstance(Class clazz, String type) {
        return GriffonApplicationHelper.newInstance(getApp(), clazz, type);
    }

    public void addonInit(GriffonApplication app) {
    }

    public void addonPostInit(GriffonApplication app) {
    }

    public void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder) {
    }

    public void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder) {
    }

    public Map<String, Object> getFactories() {
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

    public Map<String, Map<String, Object>> getMvcGroups() {
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

    public boolean isUIThread() {
        return UIThreadManager.getInstance().isUIThread();
    }

    public void execInsideUIAsync(Runnable runnable) {
        UIThreadManager.getInstance().executeAsync(runnable);
    }

    public void execInsideUISync(Runnable runnable) {
        UIThreadManager.getInstance().executeSync(runnable);
    }

    public void execOutsideUI(Runnable runnable) {
        UIThreadManager.getInstance().executeOutside(runnable);
    }

    public Future execFuture(ExecutorService executorService, Closure closure) {
        return UIThreadManager.getInstance().executeFuture(executorService, closure);
    }

    public Future execFuture(Closure closure) {
        return UIThreadManager.getInstance().executeFuture(closure);
    }

    public Future execFuture(ExecutorService executorService, Callable callable) {
        return UIThreadManager.getInstance().executeFuture(executorService, callable);
    }

    public Future execFuture(Callable callable) {
        return UIThreadManager.getInstance().executeFuture(callable);
    }

    protected Map<String, Object> groupDef(String[][] parts) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < parts.length; i++) {
            map.put(parts[i][0], parts[i][1]);
        }
        return map;
    }

    public InputStream getResourceAsStream(String name) {
        return resourceLocator.getResourceAsStream(name);
    }

    public URL getResourceAsURL(String name) {
        return resourceLocator.getResourceAsURL(name);
    }

    public List<URL> getResources(String name) {
        return resourceLocator.getResources(name);
    }
}
