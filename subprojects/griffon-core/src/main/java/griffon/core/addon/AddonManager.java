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
package griffon.core.addon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Helper class capable of dealing with addons.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface AddonManager {
    void initialize();

    /**
     * Returns a read-only view of all available addons
     *
     * @return a non-null Map of addons keyed by name
     */
    @Nonnull
    Map<String, GriffonAddon> getAddons();

    /**
     * Finds an addon by name.<p>
     * Example: findAddon("miglayout") will return a GriffonAddon that
     * represents the contributions of the MigLayout plugin.
     *
     * @param name the name of the addon to search for
     * @return an addon instance if there's a match, null otherwise
     */
    @Nullable
    GriffonAddon findAddon(@Nonnull String name);
}
