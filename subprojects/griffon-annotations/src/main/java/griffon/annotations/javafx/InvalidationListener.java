/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.annotations.javafx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a property.</p>
 * <p>This transformation provides a convenient way to register InvalidationListeners
 * on an observable bean by leveraging Groovy's closures and the Groovy cast operator.</p>
 * <p/>
 * <p>The following code exemplifies what must be written by hand in order to register an InvalidationListener.
 * <pre>
 * import griffon.annotations.javafx.FXObservable
 * import javafx.beans.InvalidationListener
 *
 * class MyModel {
 *     &#064;FXObservable String name
 *     &#064;FXObservable String lastname
 *
 *     private def snoopAll = { ... }
 *
 *     MyModel() {
 *         nameProperty().addListener(snoopAll as InvalidationListener)
 *         lastnameProperty().addListener({
 *             controller.someAction(it)
 *         } as InvalidationListener)
 *     }
 * }
 * </pre>
 * <p/>
 * <p>Applying &#064;InvalidationListener to the previous snippet results in the following code</p>
 * <pre>
 * import griffon.annotations.javafx.FXObservable
 * import griffon.annotations.javafx.InvalidationListener
 *
 * class MyModel {
 *     &#064;FXObservable
 *     &#064;InvalidationListener(snoopAll)
 *     String name
 *
 *     &#064;FXObservable
 *     &#064;InvalidationListener({ controller.someAction(it)})
 *     String lastname
 *
 *     private def snoopAll = { ... }
 * }
 * </pre>
 * <p>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement InvalidationListener (when the value
 * is a closure defined in place) or be casted as a proxy of InvalidationListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface InvalidationListener {
    String value();

    /**
     * If the {@code InvalidationListener} should be wrapped with a {@code WeakInvalidationListener} or not
     *
     * @since 2.4.0
     */
    boolean weak() default false;
}