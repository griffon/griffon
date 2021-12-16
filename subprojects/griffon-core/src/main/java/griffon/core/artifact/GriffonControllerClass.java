/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
 * Represents a Controller class in Griffon.<p>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface GriffonControllerClass extends GriffonClass {
    /**
     * "controller"
     */
    String TYPE = "controller";
    /**
     * "Controller"
     */
    String TRAILING = "Controller";

    /**
     * Matches all public methods and all properties that
     * have a Closure as value.<p>
     * Actions are subject to the following rules in order to be considered as such:<ul>
     * <li>must have public (Java) or default (Groovy) visibility modifier. </li>
     * <li>name does not match an event handler, i.e, it does not begin with <tt>on</tt>.</li>
     * <li>must pass {code GriffonClassUtils.isPlainMethod()} if it's a method.</li>
     * <li>must have <tt>void</tt> as return type if it's a method.</li>
     * <li>value must be a closure (including curried method pointers) if it's a property.</li>
     * </ul>
     *
     * @return an array containing the names of all action names.
     */
    @Nonnull
    String[] getActionNames();

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
