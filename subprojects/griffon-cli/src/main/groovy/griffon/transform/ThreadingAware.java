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
 * <p>When annotating a class it indicates that it will be able to
 * execute code using the Application's threading facilities.</p>
 * <p/>
 * The following methods will be added to classes annotated with &#064;ThreadingAware
 * <ul>
 * <li><code>public boolean isUIThread()</code></li>
 * <li><code>public void execInsideUIAsync(Runnable runnable)</code></li>
 * <li><code>public void execInsideUISync(Runnable runnable)</code></li>
 * <li><code>public void execOutsideUI(Runnable runnable)</code></li>
 * <li><code>public Future execFuture(ExecutorService executorService, Closure closure)</code></li>
 * <li><code>public Future execFuture(Closure closure)</code></li>
 * <li><code>public Future execFuture(ExecutorService executorService, Callable callable)</code></li>
 * <li><code>public Future execFuture(Callable callable)</code></li>
 * </ul>
 *
 * @author Andres Almiray
 * @see griffon.core.ThreadingHandler
 * @see org.codehaus.griffon.ast.ThreadingAwareASTTransformation
 * @since 0.9.3
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass("org.codehaus.griffon.ast.ThreadingAwareASTTransformation")
public @interface ThreadingAware {
}
