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

import java.util.Map;

/**
 * Helper class capable of dealing with services.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public interface ServiceManager extends ApplicationHandler {
    /**
     * Returns a read-only view of all available services
     *
     * @return a non-null Map of services keyed by name
     */
    Map<String, GriffonService> getServices();

    /**
     * Finds an service by name.<p>
     * Never returns null for a matching service name.
     * If a service instance is not yet available then this method will create it.
     *
     * @param name the name of the service to search for
     * @return a service instance if there's a match, null otherwise.
     */
    GriffonService findService(String name);
}
