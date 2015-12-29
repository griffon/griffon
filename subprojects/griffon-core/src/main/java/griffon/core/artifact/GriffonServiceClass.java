/*
 * Copyright 2008-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core.artifact;

import javax.annotation.Nonnull;

/**
 * Represents a Service class in Griffon.<p>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface GriffonServiceClass extends GriffonClass {
    /**
     * "service"
     */
    String TYPE = "service";
    /**
     * "Service"
     */
    String TRAILING = "Service";

    /**
     * Matches all public methods and all properties that
     * have a Closure as value.<p>
     *
     * @return an array containing the names of all service methods.
     */
    @Nonnull
    String[] getServiceNames();

    /**
     * Matches all public methods and closure properties whose name
     * matches the event handler convention, i.e, starts with "on" and
     * is followed by at least one uppercase character.<p>
     *
     * @return an array containing the names of all event handlers.
     */
    @Nonnull
    String[] getEventNames();
}
