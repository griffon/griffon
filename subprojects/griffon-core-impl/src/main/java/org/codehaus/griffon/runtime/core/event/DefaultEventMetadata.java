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
package org.codehaus.griffon.runtime.core.event;

import griffon.annotations.core.Nonnull;
import griffon.annotations.event.EventMetadata;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class DefaultEventMetadata<E> implements EventMetadata<E> {
    private final Instant instant;
    private final E event;

    public DefaultEventMetadata(@Nonnull E event) {
        this.event = requireNonNull(event, "Argument 'event' must not be null");
        this.instant = Instant.now();
    }

    @Override
    @Nonnull
    public Instant getTimestamp() {
        return instant;
    }

    @Override
    @Nonnull
    public E getEvent() {
        return event;
    }
}
