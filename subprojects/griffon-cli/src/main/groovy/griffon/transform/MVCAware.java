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

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a class.</p>
 * <p/>
 * <p>When annotating a class it indicates that it will be able to
 * handle MVC groups, that is, instantiate them. The class will have
 * the ability to create new instances of MVC groups, including short-lived
 * ones.</p>
 * <p/>
 * The following methods will be added to classes annotated with &#064;MVCAware
 * <ul>
 * <p/>
 * <li><code>public MVCGroup buildMVCGroup(String mvcType)</code></li>
 * <li><code>public MVCGroup buildMVCGroup(String mvcType, String mvcName)</code></li>
 * <li><code>public MVCGroup buildMVCGroup(Map&lt;String, Object&gt; args, String mvcType)</code></li>
 * <li><code>public MVCGroup buildMVCGroup(String mvcType, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public MVCGroup buildMVCGroup(Map&lt;String, Object&gt; args, String mvcType, String mvcName)</code></li>
 * <li><code>public MVCGroup buildMVCGroup(String mvcType, String mvcName, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVCGroup(String mvcType)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVCGroup(Map&lt;String, Object&gt; args, String mvcType)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVCGroup(String mvcType, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVCGroup(String mvcType, String mvcName)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVCGroup(Map&lt;String, Object&gt; args, String mvcType, String mvcName)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVCGroup(String mvcType, String mvcName, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public void destroyMVCGroup(String mvcName)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, Closure handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, String mvcName, Closure handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, String mvcName, Map&lt;String, Object&gt; args, Closure handler)</code></li>
 * <li><code>public void withMVCGroup(Map&lt;String, Object&gt; args, String mvcType, String mvcName, Closure handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, Map&lt;String, Object&gt; args, Closure handler)</code></li>
 * <li><code>public void withMVCGroup( Object&gt; args, String mvcType, Map&lt;String,Closure handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVCGroup(String mvcType, MVCClosure&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVCGroup(String mvcType, String mvcName, MVCClosure&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVCGroup(String mvcType, String mvcName, Map&lt;String, Object&gt; args, MVCClosure&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVCGroup(Map&lt;String, Object&gt; args, String mvcType, String mvcName, MVCClosure&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVCGroup(String mvcType, Map&lt;String, Object&gt; args, MVCClosure&lt;M, V, C&gt; handler)</code></li>
 * <p/>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVCGroup(Map&lt;String, Object&gt; args, String mvcType, MVCClosure&lt;M, V, C&gt; handler)</code></li>
 * <p/>
 * </ul>
 *
 * @author Andres Almiray
 * @see griffon.core.MVCHandler
 * @see org.codehaus.griffon.ast.MVCAwareASTTransformation
 * @since 0.9.3
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass("org.codehaus.griffon.ast.MVCAwareASTTransformation")
public @interface MVCAware {
}
