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
package griffon.core.events;

import griffon.annotations.core.Nonnull;
import griffon.core.event.Event;
import griffon.core.mvc.MVCGroup;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class CreateMVCGroupEvent extends Event {
    private final MVCGroup group;

    @Nonnull
    public static CreateMVCGroupEvent of(@Nonnull MVCGroup group) {
        return new CreateMVCGroupEvent(group);
    }

    public CreateMVCGroupEvent(@Nonnull MVCGroup group) {
        this.group = requireNonNull(group, "Argument 'group' must not be null");
    }

    @Nonnull
    public MVCGroup getGroup() {
        return group;
    }
}
