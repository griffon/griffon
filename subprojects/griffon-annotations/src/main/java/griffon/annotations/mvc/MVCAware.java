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
package griffon.annotations.mvc;

import java.lang.annotation.Documented;
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
 * <li><code>public MVCGroup createMVCGroup(String mvcType)</code></li>
 * <li><code>public MVCGroup createMVCGroup(String mvcType, String mvcId)</code></li>
 * <li><code>public MVCGroup createMVCGroup(Map&lt;String, Object&gt; args, String mvcType)</code></li>
 * <li><code>public MVCGroup createMVCGroup(String mvcType, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public MVCGroup createMVCGroup(Map&lt;String, Object&gt; args, String mvcType, String mvcId)</code></li>
 * <li><code>public MVCGroup createMVCGroup(String mvcType, String mvcId, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; createMVCGroup(Class&lt;? extends MVC&gt; mvcType)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; createMVCGroup(Class&lt;? extends MVC&gt; mvcType, String mvcId)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; createMVCGroup(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; createMVCGroup(Class&lt;? extends MVC&gt; mvcType, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; createMVCGroup(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType, String mvcId)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; createMVCGroup(Class&lt;? extends MVC&gt; mvcType, String mvcId, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVC(String mvcType)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVC(Map&lt;String, Object&gt; args, String mvcType)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVC(String mvcType, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVC(String mvcType, String mvcId)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVC(Map&lt;String, Object&gt; args, String mvcType, String mvcId)</code></li>
 * <li><code>public List&lt;? extends GriffonMvcArtifact&gt; createMVC(String mvcType, String mvcId, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; List&lt;? extends GriffonMvcArtifact&gt; createMVC(Class&lt;? extends MVC&gt; mvcType)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; List&lt;? extends GriffonMvcArtifact&gt; createMVC(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; List&lt;? extends GriffonMvcArtifact&gt; createMVC(Class&lt;? extends MVC&gt; mvcType, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; List&lt;? extends GriffonMvcArtifact&gt; createMVC(Class&lt;? extends MVC&gt; mvcType, String mvcId)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; List&lt;? extends GriffonMvcArtifact&gt; createMVC(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType, String mvcId)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; List&lt;? extends GriffonMvcArtifact&gt; createMVC(Class&lt;? extends MVC&gt; mvcType, String mvcId, Map&lt;String, Object&gt; args)</code></li>
 * <li><code>public void destroyMVCGroup(String mvcId)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(String mvcType, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(String mvcType, String mvcId, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(String mvcType, String mvcId, Map&lt;String, Object&gt; args, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Map&lt;String, Object&gt; args, String mvcType, String mvcId, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(String mvcType, Map&lt;String, Object&gt; args, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Map&lt;String, Object&gt; args, String mvcType, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Class&lt;? extends MVC&gt; mvcType, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Class&lt;? extends MVC&gt; mvcType, String mvcId, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Class&lt;? extends MVC&gt; mvcType, String mvcId, Map&lt;String, Object&gt; args, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType, String mvcId, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Class&lt;? extends MVC&gt; mvcType, Map&lt;String, Object&gt; args, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController&gt; void withMVC(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType, MVCConsumer&lt;M, V, C&gt; handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, MVCGroupFunction handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, String mvcId, MVCGroupFunction handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, String mvcId, Map&lt;String, Object&gt; args, MVCGroupFunction handler)</code></li>
 * <li><code>public void withMVCGroup(Map&lt;String, Object&gt; args, String mvcType, String mvcId, MVCGroupFunction handler)</code></li>
 * <li><code>public void withMVCGroup(String mvcType, Map&lt;String, Object&gt; args, MVCGroupFunction handler)</code></li>
 * <li><code>public void withMVCGroup(Map&lt;String, Object&gt; args, String mvcType, MVCGroupFunction handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; void withMVCGroup(Class&lt;? extends MVC&gt; mvcType, TypedMVCGroupConsumer<MVC> handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; void withMVCGroup(Class&lt;? extends MVC&gt; mvcType, String mvcId, TypedMVCGroupConsumer<MVC> handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; void withMVCGroup(Class&lt;? extends MVC&gt; mvcType, String mvcId, Map&lt;String, Object&gt; args, TypedMVCGroupConsumer<MVC> handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; void withMVCGroup(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType, String mvcId, TypedMVCGroupConsumer<MVC> handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; void withMVCGroup(Class&lt;? extends MVC&gt; mvcType, Map&lt;String, Object&gt; args, TypedMVCGroupConsumer<MVC> handler)</code></li>
 * <li><code>public &lt;MVC extends TypedMVCGroup&gt; void withMVCGroup(Map&lt;String, Object&gt; args, Class&lt;? extends MVC&gt; mvcType, TypedMVCGroupConsumer<MVC> handler)</code></li>
 * </ul>
 *
 * @author Andres Almiray
 * @see griffon.core.mvc.MVCHandler
 * @since 2.0.0
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface MVCAware {
}
