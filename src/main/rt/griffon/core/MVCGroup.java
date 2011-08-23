/*
 * Copyright 2009-2011 the original author or authors.
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

/**
 * Defines an MVC group and its contents
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public interface MVCGroup extends ApplicationHandler {
    /**
     * Returns the configuration of this group.
     *
     * @return the configuration used to instantiate this group.
     */
    MVCGroupConfiguration getConfiguration();

    /**
     * Returns the type of this group as set in the application's configuration.
     *
     * @return the type of the group
     */
    String getMvcType();

    /**
     * Returns the id of the group.
     * Ids are used to uniquely identify a group instance.
     *
     * @return the id of this group.
     */
    String getMvcId();

    /**
     * Returns the Model portion of this group.
     *
     * @return a GriffonModel instance if the group has a model member, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    GriffonModel getModel();

    /**
     * Returns the View portion of this group.
     *
     * @return a GriffonView instance if the group has a view member, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    GriffonView getView();

    /**
     * Returns the Controller portion of this group.
     *
     * @return a GriffonController instance if the group has a controller member, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    GriffonController getController();

    /**
     * Returns the builder portion of this group.
     *
     * @return a FactoryBuilderSupport instance (typically an UberBuilder)
     * @throws IllegalStateException if the group has been destroyed already
     */
    FactoryBuilderSupport getBuilder();

    /**
     * Returns the specified member type.
     *
     * @param name the type of member to retrieve
     * @return the selected MVC member if a match is found, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    Object getMember(String name);

    /**
     * Destroys the current group.
     * <strong>Should only be called once.</strong>
     *
     * @throws IllegalStateException if the group has been destroyed already
     */
    void destroy();

    /**
     * Returns whether this group has been destroyed or not.
     *
     * @return false if the group has not been destroyed yet, false otherwise
     */
    boolean isAlive();
}
