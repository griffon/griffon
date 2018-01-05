/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
 * <p>This transformation provides a convenient way to register ListChangeListeners
 * on an ObservableList by leveraging Groovy's closures and the Groovy cast operator.</p>
 * <p/>
 * <p>The following code exemplifies what must be written by hand in order to register a ChangeListener.
 * <pre>
 * import griffon.transform.ListChangeListener
 * import griffon.transform.FXObservable
 * import javafx.collections.FXCollections
 * import javafx.collections.ObservableList
 * import griffon.core.artifact.GriffonModel
 *
 * &#064;griffon.metadata.ArtifactProviderFor(GriffonModel)
 * class SampleModel {
 *     def controller
 *
 *     &#064;FXObservable
 *     &#064;ListChangeListener(snoop)
 *     ObservableList list = FXCollections.observableArrayList()
 *
 *     def snoop = { change -> ... }
 * }
 * </pre>
 * <p/>
 * <p>Applying &#064;ChangeListener to the previous snippet results in the following code</p>
 * <pre>
 * import javafx.collections.ListChangeListener
 * import javafx.collections.FXCollections
 * import javafx.collections.ObservableList
 * import griffon.core.artifact.GriffonModel
 *
 * &#064;griffon.metadata.ArtifactProviderFor(GriffonModel)
 * class SampleModel {
 *    def controller
 *
 *     &#064;FXObservable ObservableList list = FXCollections.observableArrayList()
 *
 *     def snoop = { change -> ... }
 *
 *     SampleModel() {
 *         listProperty().addListener(snoopAll as ListChangeListener)
 *     }
 * }
 * </pre>
 * <p>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement ListChangeListener (when the value
 * is a closure defined in place) or be casted as a proxy of ListChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 * @since 2.4.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
@GroovyASTTransformationClass("org.codehaus.griffon.compile.core.ast.transform.ListChangeListenerASTTransformation")
public @interface ListChangeListener {
    String value();

    /**
     * If the {@code ListChangeListener} should be wrapped with a {@code WeakListChangeListener} or not
     */
    boolean weak() default false;
}