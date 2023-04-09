/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
public interface ActionParameter {
    /**
     * Returns the index of this parameter in the array of parameters for an action.
     *
     * @return a number equal or greater than {@code 0}.
     */
    int getIndex();

    /**
     * Returns the set of annotations attached to parameter.
     *
     * @return a non-null array of annotations.
     */
    @Nonnull
    Annotation[] getAnnotations();

    /**
     * Returns the type of this parameter.
     *
     * @return a non-null type.
     */
    @Nonnull
    Class<?> getType();

    /**
     * Returns the name of this parameter.
     *
     * @return a non-null name.
     */
    @Nonnull
    String getName();

    /**
     * Finds out if this parameter is a contextual one, ie., it's annotated with {@code Contextual}.
     *
     * @return {@code true} if annotated with {@code Contextual}, {@code false} otherwise.
     */
    boolean isContextual();

    /**
     * Finds out if this parameter accepts null values.
     *
     * @return {@code true} if annotated with an implementation specific qualifier that identifies this parameter as nullable,
     * {@code false} otherwise.
     */
    boolean isNullable();
}
