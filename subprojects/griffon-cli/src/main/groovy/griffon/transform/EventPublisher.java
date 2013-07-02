/*
 * Copyright 2009-2013 the original author or authors.
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
 * <p>Annotates a class.</p>
 *
 * <p>When annotating a class it indicates that it will become an
 * event publishing one. The class will have the ability to send
 * arbitrary events to any listeners that may have been registered
 * with it. These events are similar to the ones published by
 * GriffonApplication.</p>
 *
 * The following methods will be added to classes annotated with &#064;EventPublisher
 * <ul>
 * <li><code>public void addEventListener(java.lang.Object)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;registers an event listener of type, Map, Script or bean</li>
 * <li><code>public void addEventListener(java.lang.String, groovy.lang.Closure)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;registers a Closure as event listener</li>
 * <li><code>public void addEventListener(java.lang.String, griffon.util.RunnableWithArgs)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;registers a Runnable as event listener</li>
 * <li><code>public void addEventListener(Class&lt;? extends griffon.core.Event&gt;, groovy.lang.Closure)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;registers a Closure as event listener</li>
 * <li><code>public void addEventListener(Class&lt;? extends griffon.core.Event&gt;, griffon.util.RunnableWithArgs)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;registers a Runnable as event listener</li>
 * <li><code>public void removeEventListener(java.lang.Object)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;unregisters an event listener of type, Map, Script or bean</li>
 * <li><code>public void removeEventListener(java.lang.String, groovy.lang.Closure)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;unregisters a Closure as event listener</li>
 * <li><code>public void removeEventListener(java.lang.String, griffon.util.RunnableWithArgs)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;unregisters an event listener of type, Map, Script or bean</li>
 * <li><code>public void removeEventListener(Class&lt;? extends griffon.core.Event&gt;, groovy.lang.Closure)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;unregisters a Closure as event listener</li>
 * <li><code>public void removeEventListener(Class&lt;? extends griffon.core.Event&gt;, griffon.util.RunnableWithArgs)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;unregisters a Runnable as event listener</li>
 * <li><code>public void publishEvent(java.lang.String)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event in the current thread</li>
 * <li><code>public void publishEvent(java.lang.String, java.util.List)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event in the current thread</li>
 * <li><code>public void publishEvent(griffon.core.Event)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event in the current thread</li>
 * <li><code>public void publishEventOutsideUI(java.lang.String)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event outside of the UI thread</li>
 * <li><code>public void publishEventOutsideUI(java.lang.String, java.util.List)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event outside of the UI thread</li>
 * <li><code>public void publishEventOutsideUI(griffon.core.Event)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event outside of the UI thread</li>
 * <li><code>public void publishEventAsync(java.lang.String)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event outside of the publisher's thread</li>
 * <li><code>public void publishEventAsync(java.lang.String, java.util.List)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event outside of the publisher's thread</li>
 * <li><code>public void publishEventAsync(griffon.core.Event)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;publishes an event outside of the publisher's thread</li>
 * <li><code>public boolean isEventPublishingEnabled()</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;if events will be published or not</li>
 * <li><code>public void setEventPublishingEnabled(boolean enabled)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;if events will be published or not</li>
 * </ul>
 * <p/>
 *
 * @author Andres Almiray
 * @see org.codehaus.griffon.ast.EventPublisherASTTransformation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass("org.codehaus.griffon.ast.EventPublisherASTTransformation")
public @interface EventPublisher {
}
