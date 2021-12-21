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
package griffon.annotations.threading;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a class.</p>
 * <p/>
 * <p>When annotating a class it indicates that it will be able to
 * execute code using the Application's threading facilities.</p>
 * <p/>
 * The following methods will be added to classes annotated with &#064;ThreadingAware
 * <ul>
 * <li><code>public boolean isUIThread()</code></li>
 * <li><code>public void executeInsideUIAsync(Runnable runnable)</code></li>
 * <li><code>public void executeInsideUISync(Runnable runnable)</code></li>
 * <li><code>public void executeOutsideUI(Runnable runnable)</code></li>
 * <li><code>public void executeOutsideUIAsync(Runnable runnable)</code></li>
 * <li><code>public &lt;R&gt; Future&lt;R&gt; executeFuture(ExecutorService executorService, Callable&lt;R&gt; callable)</code></li>
 * <li><code>public &lt;R&gt; Future&lt;R&gt; executeFuture(Callable&lt;R&gt; callable)</code></li>
 * <li><code>public &lt;R&gt; R executeInsideUISync(Callable&lt;R&gt; callable)</code></li>
 * <li><code>public &lt;R&gt; R executeOutsideUI(Callable&lt;R&gt; callable)</code></li>
 * </ul>
 *
 * @author Andres Almiray
 * @see griffon.core.threading.ThreadingHandler
 * @since 2.0.0
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface ThreadingAware {
}
