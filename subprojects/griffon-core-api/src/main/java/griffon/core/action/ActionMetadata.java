/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.core.action;

import griffon.annotations.core.Nonnull;

import java.lang.annotation.Annotation;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface ActionMetadata {
    /**
     * Returns the set of annotations attached to the method related to an action.
     *
     * @return a non-null array of annotations.
     */
    @Nonnull
    Annotation[] getAnnotations();

    /**
     * Returns the type associated with the method related to an action.
     *
     * @return a non-null type.
     */
    @Nonnull
    Class<?> getReturnType();

    /**
     * Returns the set of parameters of the method related to an action.
     *
     * @return a non-null array of parameters.
     */
    @Nonnull
    ActionParameter[] getParameters();

    /**
     * Returns the id for the action.
     * This value is computed from the name of the method associated with an action.
     * Example, invoking this method returns "{@code click}" given an action defined as follows:
     * <pre>
     *     package org.example;
     *
     *     public class SampleController {
     *         &#064;ActionHandler
     *         public class click(ActionEvent event) {
     *             ...
     *         }
     *     }
     * </pre>
     *
     * @return a non-null name.
     */
    @Nonnull
    String getActionId();

    /**
     * Returns the simple name for the action.
     * This value is computed from the name of the method associated with an action.
     * Example, invoking this method returns "{@code click}" given an action defined as follows:
     * <pre>
     *     package org.example;
     *
     *     public class SampleController {
     *         &#064;ActionHandler
     *         public class click(ActionEvent event) {
     *             ...
     *         }
     *     }
     * </pre>
     *
     * The computed value may be overridden by supplying a different value to the {@code name} attribute
     * of the {@code &#064;ActionHandler} annotation, in the following example the result of invoking this method
     * is "{@code clickAndGo}"
     * <pre>
     *     package org.example;
     *
     *     public class SampleController {
     *         &#064;ActionHandler(name = "clickAndGo")
     *         public class click(ActionEvent event) {
     *             ...
     *         }
     *     }
     * </pre>
     *
     * @return a non-null name.
     */
    @Nonnull
    String getActionName();

    /**
     * Returns the name of the action with the fully qualified class name of its owner as prefix.</p>
     * Example, invoking this method returns "{@code org.example.SampleController.click}" given an action defined as follows:
     * <pre>
     *     package org.example;
     *
     *     public class SampleController {
     *         &#064;ActionHandler
     *         public class click(ActionEvent event) {
     *             ...
     *         }
     *     }
     * </pre>
     * <p>
     *
     * @return a non-null name.
     */
    @Nonnull
    String getFullyQualifiedName();

    /**
     * Finds out if there are any contextual arguments defined in the method's arguments.
     *
     * @return {@code true} if any parameter is annotated with {@code Contextual}, {@code false} otherwise.
     */
    boolean hasContextualArgs();
}
