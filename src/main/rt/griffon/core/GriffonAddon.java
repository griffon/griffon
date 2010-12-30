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
package griffon.core;

import java.util.Map;
import java.util.List;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import org.slf4j.Logger;

/**
 * Identifies an Addon artifact.
 *
 * @author Andres Almiray
 *
 * @since 0.9.2
 */
public interface GriffonAddon {
    /**
     * Returns the reference to the current application.
     */
    GriffonApplication getApp();

    /**
     * Returns a Logger instance suitable for this addon.<p>
     *
     * @return a Logger instance associated with this artifact.
     */
    Logger getLog();

    void addonInit(GriffonApplication app);
    void addonPostInit(GriffonApplication app);
    void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder);
    void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder);

    Map<String, ?> getFactories();
    Map<String, Closure> getMethods();
    Map<String, Map<String, Closure>> getProps();
    Map<String, Closure> getEvents();
    Map<String, Map<String, String>> getMvcGroups();

    List<Closure> getAttributeDelegates();
    List<Closure> getPreInstantiateDelegates();
    List<Closure> getPostInstantiateDelegates();
    List<Closure> getPostNodeCompletionDelegates();
}
