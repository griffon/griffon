/*
 * Copyright 2008-2015 the original author or authors.
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
 * <p>This transformation provides a convenient way to register MapChangeListeners
 * on an ObservableMap by leveraging Groovy's closures and the Groovy cast operator.</p>
 * <p/>
 * <p>The following code exemplifies what must be written by hand in order to register a ChangeListener.
 * <pre>
 * import griffon.transform.MapChangeListener
 * import griffon.transform.FXObservable
 * import javafx.collections.FXCollections
 * import javafx.collections.ObservableMap
 * import griffon.core.artifact.GriffonModel
 *
 * &#064;griffon.metadata.ArtifactProviderFor(GriffonModel)
 * class SampleModel {
 *     def controller
 *
 *     &#064;FXObservable
 *     &#064;MapChangeListener(snoop)
 *     ObservableMap map = FXCollections.observableHashMap()
 *
 *     def snoop = { change -> ... }
 * }
 * </pre>
 * <p/>
 * <p>Applying &#064;ChangeListener to the previous snippet results in the following code</p>
 * <pre>
 * import javafx.collections.MapChangeListener
 * import javafx.collections.FXCollections
 * import javafx.collections.ObservableMap
 * import griffon.core.artifact.GriffonModel
 *
 * &#064;griffon.metadata.ArtifactProviderFor(GriffonModel)
 * class SampleModel {
 *    def controller
 *
 *     &#064;FXObservable ObservableMap map = FXCollections.observableHashMap()
 *
 *     def snoop = { change -> ... }
 *
 *     SampleModel() {
 *         mapProperty().addListener(snoopAll as MapChangeListener)
 *     }
 * }
 * </pre>
 * <p>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement MapChangeListener (when the value
 * is a closure defined in place) or be casted as a proxy of MapChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 * @since 2.4.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
@GroovyASTTransformationClass("org.codehaus.griffon.compile.core.ast.transform.MapChangeListenerASTTransformation")
public @interface MapChangeListener {
    String value();

    /**
     * If the {@code MapChangeListener} should be wrapped with a {@code WeakMapChangeListener} or not
     */
    boolean weak() default false;
}