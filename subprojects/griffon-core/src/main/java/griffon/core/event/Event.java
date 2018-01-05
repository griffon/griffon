/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import javax.annotation.Nonnull;
import java.util.EventObject;

/**
 * @author Andres Almiray
 */
public abstract class Event extends EventObject {
    private static final long serialVersionUID = -2080599710005680415L;

    /**
     * System time when the event happened
     */
    private final long timestamp;

    /**
     * Create a new Event.
     *
     * @param source the component that published the event (never <code>null</code>)
     */
    public Event(@Nonnull Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}
