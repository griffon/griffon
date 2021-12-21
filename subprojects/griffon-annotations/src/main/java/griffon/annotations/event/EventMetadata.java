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
package griffon.annotations.event;

import java.time.Instant;

/**
 * Defines metadata of an event. This type provides additional information that can be used in a {@link javax.application.event.EventFilter}
 *
 * @param <E> type of the event
 *
 * @author Hendrik Ebbers
 * @author Andres Almiray
 */
public interface EventMetadata<E> {
    /**
     * The point in time when this event was published.
     *
     * @return Never returns {@code null}.
     */
    Instant getTimestamp();

    /**
     * The event that was published.
     *
     * @return Never returns {@code null}.
     */
    E getEvent();
}
