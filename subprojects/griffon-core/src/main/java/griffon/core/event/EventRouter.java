/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.core.event;

/**
 * An event handling helper.<p>
 * Listeners may be of type<ul>
 * <li>a <tt>Map</tt></li>
 * <li>a <tt>CallableWithArgs</tt></li>
 * <li>a <tt>Object</tt> (a Java bean)</li>
 * </ul>
 * <p/>
 * With the exception of Map keys, the naming convention for an eventHandler is
 * "on" + eventName, Maps keys require handlers to be named as eventName only.<p>
 * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
 * Event names must follow the camelCase naming convention.<p>
 *
 * @author Andres Almiray
 */
public interface EventRouter extends EventPublisher {

}
