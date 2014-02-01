/*
 * Copyright 2008-2014 the original author or authors.
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
 * <p>When annotating a class it indicates that it will be able to locate
 * resources using Griffon's standard resource loading mechanism.</p>
 * <p/>
 * The following methods will be added to classes annotated with &#064;ResourcesAware
 * <ul>
 * <li><code>public URL getResourceAsURL(java.lang.String)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;returns the resource URL if found</li>
 * <li><code>public URL getResourceAsStream(java.lang.String)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;opens a stream to the resource if found</li>
 * <li><code>public List&gt;URL&lt; getResources(java.lang.String)</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;returns all matching resources for the specified name</li>
 * <li><code>public ClassLoader classloader()</code><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;returns the classloader used to locate resources</li>
 * <p/>
 * </ul>
 * <p/>
 *
 * @author Andres Almiray
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ResourcesAware {
}
