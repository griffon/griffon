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
package griffon.transform.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a property.</p>
 * <p>This transformation provides a convenient way to register PropertyChangeListeners
 * on an observable bean by leveraging Groovy's closures and the Groovy cast operator.</p>
 * <p/>
 * <p>The following code exemplifies what must be written by hand in order to register a pair
 * of PropertyChangeListeners. One of them is a catch-all handler while the second is property specific.
 * <pre>
 * import griffon.transform.Observable
 * import java.beans.PropertyChangeListener
 *
 * class MyModel {
 *     &#064;Observable String name
 *     &#064;Observable String lastname
 *
 *     def snoopAll = { evt -> ... }
 *
 *     MyModel() {
 *         addPropertyChangeListener(snoopAll as PropertyChangeListener)
 *         addPropertyChangeListener('lastname', {
 *             controller.someAction(it)
 *         } as PropertyChangeListener)
 *     }
 * }
 * </pre>
 * <p/>
 * <p>Applying &#064;PropertyListener to the previous snippet results in the following code</p>
 * <pre>
 * import griffon.transform.beans.PropertyListener
 * import griffon.transform.Observable
 *
 * &#064;PropertyListener(snoopAll)
 * class MyModel {
 *     &#064;Observable String name
 *
 *     &#064;Observable
 *     &#064;PropertyListener({controller.someAction(it)})
 *     String lastname
 *
 *     def snoopAll = { evt -> ... }
 * }
 * </pre>
 * <p/>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement PropertyChangeListener (when the value
 * is a closure defined in place) or be casted as a proxy of PropertyChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface PropertyListener {
    String value();
}