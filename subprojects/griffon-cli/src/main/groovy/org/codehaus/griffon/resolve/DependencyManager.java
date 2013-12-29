/* Copyright 2013-2014 the original author or authors.
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
package org.codehaus.griffon.resolve;

import java.util.Collection;

/**
 * General interface for all dependency manager implementations to implement with common utility methods not tied to Ivy or Aether or any
 * dependency resolution engine
 *
 * @author Graeme Rocher (Grails 2.3)
 * @since 1.3.0
 */
public interface DependencyManager {
    /**
     * Outputs the dependency graph to System.out
     */
    void produceReport();

    /**
     * Outputs the dependency graph to System.out
     *
     * @param scope The scope of the report
     */
    void produceReport(String scope);

    /**
     * Resolve dependencies for the given scope
     *
     * @param scope The scope
     * @return The {@link DependencyReport} instance
     */
    DependencyReport resolve(String scope);

    /**
     * Resolve dependencies for the default scope
     *
     * @return The {@link DependencyReport} instance
     */
    DependencyReport resolve();

    /**
     * The direct dependencies of the application, not including framework or dependencies inherited from plugins
     *
     * @return Direct application dependencies
     */
    Collection<Dependency> getApplicationDependencies();

    /**
     * @return The plugin dependencies of the application
     */
    Collection<Dependency> getPluginDependencies();

    /**
     * All dependencies of the current application
     *
     * @return All application dependencies
     */
    Collection<Dependency> getAllDependencies();

    /**
     * The direct dependencies of the application, not including framework or dependencies inherited from plugins
     *
     * @param scope The scope of the dependencies
     * @return Direct application dependencies
     */
    Collection<Dependency> getApplicationDependencies(String scope);

    /**
     * The direct plugin dependencies of the application, not including framework or dependencies inherited from plugins
     *
     * @param scope The scope of the dependencies
     * @return Direct application dependencies
     */
    Collection<Dependency> getPluginDependencies(String scope);

    /**
     * All dependencies of the current application
     *
     * @param scope The scope of the dependencies
     * @return All application dependencies
     */
    Collection<Dependency> getAllDependencies(String scope);
}
