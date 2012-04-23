/* 
 * Copyright 2004-2012 the original author or authors.
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
package org.codehaus.griffon.cli.support;

import griffon.build.GriffonBuildListener;
import griffon.util.BuildSettings;
import griffon.util.GriffonUtil;
import griffon.util.Metadata;
import groovy.lang.*;
import org.apache.tools.ant.BuildEvent;
import org.codehaus.griffon.plugins.PluginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.getPropertyNameForLowerCaseHyphenSeparatedName;
import static org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation.castToBoolean;

/**
 * @author Graeme Rocher (Grails 1.1)
 */
public class GriffonBuildEventListener extends BuildListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonBuildEventListener.class);
    private static final Pattern EVENT_NAME_PATTERN = Pattern.compile("event([A-Z]\\w*)");
    private GroovyClassLoader classLoader;
    private Binding binding;
    protected Map<String, List<Closure>> globalEventHooks = new HashMap<String, List<Closure>>();
    private BuildSettings buildSettings;
    private final Map<String, Long> timings = new LinkedHashMap<String, Long>();

    /**
     * The objects that are listening for build events
     */
    private List<GriffonBuildListener> buildListeners = new LinkedList<GriffonBuildListener>();

    public GriffonBuildEventListener(GroovyClassLoader scriptClassLoader, Binding binding, BuildSettings buildSettings) {
        super();
        this.classLoader = scriptClassLoader;
        this.binding = binding;
        this.buildSettings = buildSettings;
    }

    public void initialize() {
        loadEventHooks(buildSettings);
        loadGriffonBuildListeners();
    }

    public void setClassLoader(GroovyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setGlobalEventHooks(Map<String, List<Closure>> globalEventHooks) {
        this.globalEventHooks = globalEventHooks;
    }

    protected void loadEventHooks(BuildSettings buildSettings) {
        if (buildSettings != null) {
            loadEventsScript(findEventsScript(new File(buildSettings.getUserHome(), ".griffon/scripts")));

            if (buildSettings.isPluginProject() != null) {
                String pluginName = getPropertyNameForLowerCaseHyphenSeparatedName(Metadata.getCurrent().getApplicationName());
                binding.setVariable(pluginName + "PluginDir", buildSettings.getBaseDir());
            }
            loadEventsScript(findEventsScript(new File(buildSettings.getBaseDir(), "scripts")));

            loadEventsFromPlugins(buildSettings.pluginSettings.getSortedProjectPluginDirectories());
            loadEventsFromPlugins(buildSettings.pluginSettings.getSortedFrameworkPluginDirectories());
        }
    }

    private void loadEventsFromPlugins(Map<String, PluginInfo> projectPlugins) {
        for (Map.Entry<String, PluginInfo> plugin : projectPlugins.entrySet()) {
            try {
                if (!castToBoolean(binding.getVariables().get("events_loaded_" + plugin.getKey()))) {
                    String pluginName = getPropertyNameForLowerCaseHyphenSeparatedName(plugin.getKey());
                    binding.setVariable(pluginName + "PluginDir", plugin.getValue().getDirectory().getFile());
                    loadEventsScript(findEventsScript(new File(plugin.getValue().getDirectory().getFile(), "scripts")));
                    binding.setVariable("events_loaded_" + plugin.getKey(), true);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected void loadGriffonBuildListeners() {
        for (Object listener : buildSettings.getBuildListeners()) {
            if (listener instanceof String) {
                addGriffonBuildListener((String) listener);
            } else if (listener instanceof Class) {
                addGriffonBuildListener((Class) listener);
            } else {
                throw new IllegalStateException("buildSettings.getBuildListeners() returned a " + listener.getClass().getName());
            }
        }
    }

    public void loadEventsScript(File eventScript) {
        if (eventScript != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loading event handlers from " + eventScript.getAbsolutePath());
            }
            try {
                Class scriptClass = classLoader.parseClass(eventScript);
                if (scriptClass != null) {
                    Script script = (Script) scriptClass.newInstance();
                    script.setBinding(new Binding(this.binding.getVariables()) {
                        @Override
                        public void setVariable(String var, Object o) {
                            final Matcher matcher = EVENT_NAME_PATTERN.matcher(var);
                            if (matcher.matches() && (o instanceof Closure)) {
                                String eventName = matcher.group(1);
                                List<Closure> hooks = globalEventHooks.get(eventName);
                                if (hooks == null) {
                                    hooks = new ArrayList<Closure>();
                                    globalEventHooks.put(eventName, hooks);
                                }
                                if (!hooks.contains(o)) hooks.add((Closure) o);
                            }
                            super.setVariable(var, o);
                        }
                    });
                    script.run();
                } else {
                    System.err.println("Could not load event script (script may be empty): " + eventScript);
                }
            } catch (Throwable e) {
                sanitize(e);
                e.printStackTrace();
                System.out.println("Error loading event script from file [" + eventScript + "] " + e.getMessage());
            }
        }
    }

    protected File findEventsScript(File dir) {
        File f = new File(dir, "_Events.groovy");
        if (!f.exists()) {
            f = new File(dir, "Events.groovy");
            if (f.exists()) {
                GriffonUtil.deprecated("Use of 'Events.groovy' is DEPRECATED. Please rename to '_Events.groovy'.");
            }
        }

        return f.exists() ? f : null;
    }

    public void targetStarted(BuildEvent buildEvent) {
        String targetName = buildEvent.getTarget().getName();
        String eventName = GriffonUtil.getClassNameRepresentation(targetName) + "Start";

        buildSettings.debug(">>>> " + targetName);
        timings.put(targetName, System.currentTimeMillis());
        triggerEvent(eventName, binding);
    }

    /**
     * Triggers and event for the given name and binding
     *
     * @param eventName The name of the event
     */
    public void triggerEvent(String eventName) {
        triggerEvent(eventName, binding);
    }

    /**
     * Triggers an event for the given name and arguments
     *
     * @param eventName The name of the event
     * @param arguments The arguments
     */
    public void triggerEvent(String eventName, Object... arguments) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Triggering event " + eventName);
        }
        List<Closure> handlers = globalEventHooks.get(eventName);
        if (handlers != null) {
            List<Closure> eventHandlers = new ArrayList<Closure>();
            eventHandlers.addAll(handlers);
            for (Closure handler : eventHandlers) {
                handler.setDelegate(binding);
                try {
                    handler.call(arguments);
                } catch (MissingPropertyException mpe) {
                    // ignore
                }
            }
        }

        for (GriffonBuildListener buildListener : buildListeners) {
            buildListener.receiveGriffonBuildEvent(eventName, arguments);
        }
    }

    public void targetFinished(BuildEvent buildEvent) {
        String targetName = buildEvent.getTarget().getName();
        String eventName = GriffonUtil.getClassNameRepresentation(targetName) + "End";

        triggerEvent(eventName, binding);
        Long timing = System.currentTimeMillis() - timings.get(targetName);
        buildSettings.debug("<<<< " + targetName + " [" + timing + "ms]");
    }

    protected void addGriffonBuildListener(String listenerClassName) {
        Class listenerClass;
        try {
            listenerClass = classLoader.loadClass(listenerClassName);
            addGriffonBuildListener(listenerClass);
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load griffon build listener class. " + e);
        }
    }

    protected void addGriffonBuildListener(Class listenerClass) {
        if (!GriffonBuildListener.class.isAssignableFrom(listenerClass)) {
            throw new RuntimeException("Intended griffon build listener class of " + listenerClass.getName() + " does not implement " + GriffonBuildListener.class.getName());
        }
        GriffonBuildListener listener;
        try {
            listener = (GriffonBuildListener) listenerClass.newInstance();
            addGriffonBuildListener(listener);
        } catch (Exception e) {
            System.err.println("Could not instantiate " + listenerClass.getName() + ". " + e);
        }
    }

    public void addGriffonBuildListener(GriffonBuildListener listener) {
        buildListeners.add(listener);
    }
}
