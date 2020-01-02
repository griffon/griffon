/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.core.events;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.addon.GriffonAddon;
import griffon.core.event.Event;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LoadAddonEndEvent extends Event {
    private final GriffonApplication application;
    private final String name;
    private final GriffonAddon addon;

    @Nonnull
    public static LoadAddonEndEvent of(@Nonnull GriffonApplication application, @Nonnull String name, @Nonnull GriffonAddon addon) {
        return new LoadAddonEndEvent(application, name, addon);
    }

    public LoadAddonEndEvent(@Nonnull GriffonApplication application, @Nonnull String name, @Nonnull GriffonAddon addon) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
        this.name = requireNonBlank(name, "Argument 'name' must not be blank");
        this.addon = requireNonNull(addon, "Argument 'addon' must not be null");
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public GriffonAddon getAddon() {
        return addon;
    }
}
