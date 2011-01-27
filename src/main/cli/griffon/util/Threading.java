/*
 * Copyright 2009-2011 the original author or authors.
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

package griffon.util;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method or property.<p>
 *
 * Annotated elements must follow these rules
 * <ul>
 *  <li>must be a public method or property</li>
 *  <li>name does not match an event handler</li>
 *  <li>must pass {@code griffon.util.GriffonClassUtils.isPlainMethod()} if it's a method</li>
 *  <li>its value must be a closure (including curried method pointers) if it's a property</li>
 * </ul>
 *
 * This annotation takes {@code griffon.util.ThreadingPolicy} as value, with {@code ThreadingPolicy.OUTSIDE_UITHREAD} being
 * the default value.
 *
 * @see griffon.util.ThreadingPolicy
 * @see org.codehaus.griffon.ast.ThreadingASTTransformation
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD})
@GroovyASTTransformationClass("org.codehaus.griffon.ast.ThreadingASTTransformation")
public @interface Threading {
    ThreadingPolicy value() default ThreadingPolicy.OUTSIDE_UITHREAD;
}
