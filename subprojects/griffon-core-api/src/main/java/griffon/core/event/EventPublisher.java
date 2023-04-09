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
package griffon.core.event;

import griffon.annotations.core.Nonnull;

/**
 * Base contract for classes that can publish events using their own
 * event bus.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface EventPublisher extends EventBus {
    /**
     * Publishes an event.<p>
     * Listeners will be notified outside of the UI thread.
     *
     * @param event the event to be published.
     */
    <E> void publishEventOutsideUI(@Nonnull E event);

    /**
     * Returns whether events will be published by the event bus or not.
     *
     * @return true if event publishing is enabled; false otherwise.
     */
    boolean isEventPublishingEnabled();

    /**
     * Sets the enabled state for event publishing.</p>
     * Events will be automatically discarded when the enabled state is set to false.
     *
     * @param enabled the value fot the enabled state.
     */
    void setEventPublishingEnabled(boolean enabled);
}
