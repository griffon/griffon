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
package griffon.core.artifact;

import griffon.annotations.core.Nonnull;
import griffon.core.mvc.MVCGroup;

import java.util.Map;

/**
 * Identifies an artifact that belongs to an MVC group.<p>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface GriffonMvcArtifact extends GriffonArtifact {
    /**
     * Post initialization callback.<p>
     * This callback is called for all artifacts that belong to the
     * same MVC group right after each instance has been created.
     * Each entry on the <tt>args</tt> Map points either to an MVC
     * member or a variable that was defined using any of the {@code createMVCGroup}
     * and/or {@code createMVC} methods that can take a Map as parameter.
     *
     * @param args a Map of MVC instances or variables keyed by type.
     */
    void mvcGroupInit(@Nonnull Map<String, Object> args);

    /**
     * Callback for when the group is destroyed and disposed from the application.<p>
     * Once an artifact has been "destroyed" it should not be used anymore. The application
     * will remove any references to the group on its cache.
     */
    void mvcGroupDestroy();

    @Nonnull
    MVCGroup getMvcGroup();
}