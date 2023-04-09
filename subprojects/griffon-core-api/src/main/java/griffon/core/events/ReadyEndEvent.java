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
import griffon.core.GriffonApplication;
import griffon.core.event.Event;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ReadyEndEvent extends Event {
    private final GriffonApplication application;

    @Nonnull
    public static ReadyEndEvent of(@Nonnull GriffonApplication application) {
        return new ReadyEndEvent(application);
    }

    public ReadyEndEvent(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }
}
