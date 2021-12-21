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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andres Almiray
 * @since 3.0.0
 * @see Policy
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Threading {
    /**
     * Defines the threading hint to be used when executing the annotated element.
     */
    Policy value() default Policy.OUTSIDE_UITHREAD;

    /**
     * Indicates the type of threading management for a method or property.</p>
     * The following values apply
     * <ul>
     * <li>{@code SKIP} - no threading management will be performed.</li>
     * <li>{@code OUTSIDE_UITHREAD} - code should be invoked outside of the UI thread.</li>
     * <li>{@code OUTSIDE_UITHREAD_ASYNC} - code should be invoked on a background thread, always.</li>
     * <li>{@code INSIDE_UITHREAD_SYNC} - code should be invoked inside the UI thread using a synchronous call.</li>
     * <li>{@code INSIDE_UITHREAD_ASYNC} - code should be invoked inside the UI thread using an asynchronous call.</li>
     * </ul>
     *
     * @author Andres Almiray
     * @see Threading
     */
    enum Policy {
        /**
         * Skip threading injection
         */
        SKIP,
        /**
         * Inject runOutsideUI wrapper
         */
        OUTSIDE_UITHREAD,
        /**
         * Inject runOutsideUIAsync wrapper
         */
        OUTSIDE_UITHREAD_ASYNC,
        /**
         * Inject runInsideUISync wrapper
         */
        INSIDE_UITHREAD_SYNC,
        /**
         * Inject runInsideUIAsync wrapper
         */
        INSIDE_UITHREAD_ASYNC
    }
}
