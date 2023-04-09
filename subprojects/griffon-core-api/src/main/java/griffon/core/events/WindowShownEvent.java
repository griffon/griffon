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
package griffon.core.events;

import griffon.annotations.core.Nonnull;
import griffon.core.event.Event;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class WindowShownEvent<W> extends Event {
    private final String name;
    private final W window;

    public WindowShownEvent(@Nonnull String name, @Nonnull W window) {
        this.name = requireNonBlank(name, "Argument 'name' must not be blank");
        this.window = requireNonNull(window, "Argument 'window' must not be null");
    }

    @Nonnull
    public W getWindow() {
        return window;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public static <W> WindowShownEvent of(@Nonnull String name, @Nonnull W window) {
        return new WindowShownEvent<>(name, window);
    }
}
