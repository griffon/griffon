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
import griffon.core.event.Event;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class InitializeMVCGroupEvent extends Event {
    private final MVCGroupConfiguration configuration;
    private final MVCGroup group;

    @Nonnull
    public static InitializeMVCGroupEvent of(@Nonnull MVCGroupConfiguration configuration, @Nonnull MVCGroup group) {
        return new InitializeMVCGroupEvent(configuration, group);
    }

    public InitializeMVCGroupEvent(@Nonnull MVCGroupConfiguration configuration, @Nonnull MVCGroup group) {
        this.configuration = requireNonNull(configuration, "Argument 'configuration' must not be null");
        this.group = requireNonNull(group, "Argument 'group' must not be null");
    }

    @Nonnull
    public MVCGroupConfiguration getConfiguration() {
        return configuration;
    }

    @Nonnull
    public MVCGroup getGroup() {
        return group;
    }
}
