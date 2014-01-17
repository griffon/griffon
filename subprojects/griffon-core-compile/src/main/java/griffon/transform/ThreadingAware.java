/*
 * Copyright 2009-2014 the original author or authors.
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

import java.lang.annotation.*;

/**
 * <p>Annotates a class.</p>
 * <p/>
 * <p>When annotating a class it indicates that it will be able to
 * execute code using the Application's threading facilities.</p>
 * <p/>
 * The following methods will be added to classes annotated with &#064;ThreadingAware
 * <ul>
 * <li><code>public boolean isUIThread()</code></li>
 * <li><code>public void runInsideUIAsync(Runnable runnable)</code></li>
 * <li><code>public void runInsideUISync(Runnable runnable)</code></li>
 * <li><code>public void runOutsideUI(Runnable runnable)</code></li>
 * <li><code>public Future runFuture(ExecutorService executorService, Callable callable)</code></li>
 * <li><code>public Future runFuture(Callable callable)</code></li>
 * </ul>
 *
 * @author Andres Almiray
 * @see griffon.core.threading.ThreadingHandler
 * @since 0.9.3
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface ThreadingAware {
}
