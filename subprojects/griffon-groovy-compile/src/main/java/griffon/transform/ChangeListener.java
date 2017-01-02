/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.transform;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a property.</p>
 * <p>This transformation provides a convenient way to register ChangeListeners
 * on an observable bean by leveraging Groovy's closures and the Groovy cast operator.</p>
 * <p/>
 * <p>The following code exemplifies what must be written by hand in order to register a ChangeListener.
 * <pre>
 * import griffon.transform.FXObservable
 * import javafx.beans.value.ChangeListener
 *
 * class MyModel {
 *     &#064;FXObservable String name
 *     &#064;FXObservable String lastname
 *
 *     private def snoopAll = { ob, ov, nv -> ... }
 *
 *     MyModel() {
 *         nameProperty().addListener(snoopAll as ChangeListener)
 *         lastnameProperty().addListener({ ob, ov, nv ->
 *             controller.someAction(nv)
 *         } as ChangeListener)
 *     }
 * }
 * </pre>
 * <p/>
 * <p>Applying &#064;ChangeListener to the previous snippet results in the following code</p>
 * <pre>
 * import griffon.transform.FXObservable
 * import griffon.transform.ChangeListener
 *
 * class MyModel {
 *     &#064;FXObservable
 *     &#064;ChangeListener(snoopAll)
 *     String name
 *
 *     &#064;FXObservable
 *     &#064;ChangeListener({ ob, ov, nv -> controller.someAction(nv)})
 *     String lastname
 *
 *     private def snoopAll = { ob, ov, nv -> ... }
 * }
 * </pre>
 * <p>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement ChangeListener (when the value
 * is a closure defined in place) or be casted as a proxy of ChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
@GroovyASTTransformationClass("org.codehaus.griffon.compile.core.ast.transform.ChangeListenerASTTransformation")
public @interface ChangeListener {
    String value();

    /**
     * If the {@code ChangeListener} should be wrapped with a {@code WeakChangeListener} or not
     *
     * @since 2.4.0
     */
    boolean weak() default true;
}