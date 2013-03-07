/*
 * Copyright 2010-2013 the original author or authors.
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
package griffon.core;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Identifies an Addon artifact.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public interface GriffonAddon extends ApplicationHandler, ThreadingHandler, ResourceHandler {
    /**
     * Returns a Logger instance suitable for this addon.<p>
     *
     * @return a Logger instance associated with this artifact.
     */
    Logger getLog();

    Object newInstance(Class klass, String type);

    void addonInit(GriffonApplication app);

    void addonPostInit(GriffonApplication app);

    void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder);

    void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder);

    Map<String, Object> getFactories();

    Map<String, Closure> getMethods();

    Map<String, Map<String, Closure>> getProps();

    Map<String, Closure> getEvents();

    Map<String, Map<String, Object>> getMvcGroups();

    List<Closure> getAttributeDelegates();

    List<Closure> getPreInstantiateDelegates();

    List<Closure> getPostInstantiateDelegates();

    List<Closure> getPostNodeCompletionDelegates();

    Map<String,Map<String, Object>> getActionInterceptors();
}
