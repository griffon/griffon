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
 * <p/>
 * <p>When annotating a class it indicates that it will be able to resolve
 * messages using Griffon's {@code MessageSource} mechanism.</p>
 * <p/>
 * The following methods will be added to classes annotated with &#064;MessageSourceAware
 * <ul>
 * <li><code>public Object resolveMessageValue(java.lang.String, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.lang.Object[])</code></li>
 * <li><code>public String getMessage(java.lang.String, java.lang.Object[], java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.List)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.List, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.Map)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.Map, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.lang.String)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.lang.String, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.lang.Object[], java.lang.String)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.lang.Object[], java.lang.String, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.List, java.lang.String)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.List, java.lang.String, java.util.Locale)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.Map, java.lang.String)</code></li>
 * <li><code>public String getMessage(java.lang.String, java.util.Map, java.lang.String, java.util.Locale)</code></li>
 * <p/>
 * </ul>
 *
 * @author Andres Almiray
 * @see org.codehaus.griffon.ast.MessageSourceAwareASTTransformation
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass("org.codehaus.griffon.ast.MessageSourceAwareASTTransformation")
public @interface MessageSourceAware {
}
