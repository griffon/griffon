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
package org.codehaus.griffon.cli.support;

import griffon.build.GriffonBuildListener;
import griffon.util.BuildSettings;
import griffon.util.GriffonUtil;
import griffon.util.PluginBuildSettings;
import groovy.lang.*;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Graeme Rocher (Grails 1.1)
 */
public class GriffonBuildEventListener implements BuildListener {
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
            loadEventsScript(findEventsScript(new File(buildSettings.getBaseDir(), "scripts")));

            PluginBuildSettings pluginSettings = (PluginBuildSettings) binding.getVariable("pluginSettings");
            for (Resource pluginBase : pluginSettings.getSortedPluginDirectories()) {
                try {
                    loadEventsScript(findEventsScript(new File(pluginBase.getFile(), "scripts")));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
                GriffonUtil.deepSanitize(e);
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
                GriffonUtil.deprecated("Use of 'Events.groovy' is DEPRECATED.  Please rename to '_Events.groovy'.");
            }
        }

        return f.exists() ? f : null;
    }

    public void buildStarted(BuildEvent buildEvent) {
        // do nothing
    }

    public void buildFinished(BuildEvent buildEvent) {
        // do nothing
    }

    public void targetStarted(BuildEvent buildEvent) {
        String targetName = buildEvent.getTarget().getName();
        String eventName = GriffonUtil.getClassNameRepresentation(targetName) + "Start";

        buildSettings.debug(">> " + targetName);
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
        List<Closure> handlers = globalEventHooks.get(eventName);
        if (handlers != null) {
            for (Closure handler : handlers) {
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
        buildSettings.debug("<< " + targetName + " [" + timing + "ms]");
    }

    public void taskStarted(BuildEvent buildEvent) {
        // do nothing
    }

    public void taskFinished(BuildEvent buildEvent) {
        // do nothing
    }

    public void messageLogged(BuildEvent buildEvent) {
        // do nothing
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
