/*
 * Copyright 2008 the original author or authors.
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
package griffon.util;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.ConfigObject;

import java.util.Map;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: May 21, 2008
 * Time: 3:21:38 PM
 */
public interface IGriffonApplication {

    public Class getConfigClass();
    public ConfigObject getConfig();
    public void setConfig(ConfigObject config);

    public Class getBuilderClass();
    public ConfigObject getBuilderConfig();
    public void setBuilderConfig(ConfigObject builderConfig);

    public Class getEventsClass();
    public Object getEventsConfig();
    public void setEventsConfig(Object eventsConfig);

    public Binding getBindings();
    public void setBindings(Binding bindings);

    public Map<String, ?> getModels();
    public Map<String, ?> getViews();
    public Map<String, ?> getControllers();
    public Map<String, ?> getBuilders();
    public Map<String, Map<String, ?>> getGroups();

    public Object createApplicationContainer();

    public void initialize();
    public void startup();
    public void ready();
    public void shutdown();

    public void addApplicationEventListener(Object listener );
    public void addApplicationEventListener(String eventName, Closure listener);
    public void removeApplicationEventListener(Object listener );
    public void removeApplicationEventListener(String eventName, Closure listener);
    public void event(String eventName);
    public void event(String eventName, List params);

}
