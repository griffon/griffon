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
package griffon.core.mvc;

import griffon.annotations.core.Nonnull;

/**
 * An specialized function for working with typed MVC groups.
 *
 * @author Andres Almiray
 * @since 2.11.0
 */
public interface TypedMVCGroupFunction<MVC extends TypedMVCGroup> {
    /**
     * Applies this function<p>
     *
     * @param group the MVC group
     */
    void apply(@Nonnull MVC group);
}
