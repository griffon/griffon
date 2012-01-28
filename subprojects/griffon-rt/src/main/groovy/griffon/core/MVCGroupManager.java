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
package griffon.core;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * Manages the configuration and instantiation of MVC groups.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public interface MVCGroupManager extends MVCHandler, ApplicationHandler {
    MVCGroupConfiguration newMVCGroupConfiguration(GriffonApplication app, String mvcType, Map<String, String> members, Map<String, Object> config);

    MVCGroup newMVCGroup(GriffonApplication app, MVCGroupConfiguration configuration, String mvcId, Map<String, Object> members);

    void initialize(Map<String, MVCGroupConfiguration> configurations);

    void addConfiguration(MVCGroupConfiguration configuration);

    Map<String, MVCGroupConfiguration> getConfigurations();

    Map<String, MVCGroup> getGroups();

    MVCGroupConfiguration findConfiguration(String mvcType);

    MVCGroup findGroup(String mvcId);

    MVCGroup getAt(String mvcId);

    /**
     * Returns all currently available model instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated models.
     */
    Map<String, ? extends GriffonModel> getModels();

    /**
     * Returns all currently available view instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated views.
     */
    Map<String, ? extends GriffonView> getViews();

    /**
     * Returns all currently available controller instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated controllers.
     */
    Map<String, ? extends GriffonController> getControllers();

    /**
     * Returns all currently available builder instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated builders.
     */
    Map<String, ? extends FactoryBuilderSupport> getBuilders();
}
