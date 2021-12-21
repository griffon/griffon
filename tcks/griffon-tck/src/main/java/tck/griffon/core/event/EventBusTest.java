/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
package tck.griffon.core.event;

import griffon.annotations.event.EventFilter;
import griffon.annotations.event.EventHandler;
import griffon.annotations.event.EventMetadata;
import griffon.core.event.EventBus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andres Almiray
 */
public abstract class EventBusTest {
    protected abstract EventBus resolveEventBus();

    @Test
    public void subscribe_and_unsubscribe() {
        // given:
        EventBus eventBus = resolveEventBus();
        TestEvent1Handler eventHandler = new TestEvent1Handler();
        eventBus.subscribe(eventHandler);
        eventBus.unsubscribe(eventHandler);

        //when:
        eventBus.publishEvent(new Event1());
        eventBus.publishEvent(new Event2());

        // then:
        assertNull(eventHandler.event);
    }

    @Test
    public void publish_event_synchronously() {
        // given:
        EventBus eventBus = resolveEventBus();
        TestEvent1Handler eventHandler = new TestEvent1Handler();
        eventBus.subscribe(eventHandler);

        //when:
        eventBus.publishEvent(new Event1());
        eventBus.publishEvent(new Event2());

        // then:
        assertTrue(eventHandler.event instanceof Event1);
    }

    @Test
    public void publish_event_asynchronously() throws Exception {
        // given:
        EventBus eventBus = resolveEventBus();
        TestEvent1Handler eventHandler = new TestEvent1Handler();
        eventBus.subscribe(eventHandler);

        //when:
        eventBus.publishEventAsync(new Event1());
        eventBus.publishEventAsync(new Event2());
        Thread.sleep(200L);

        // then:
        assertTrue(eventHandler.event instanceof Event1);
    }

    @Test
    public void publish_event_with_filters() {
        // given:
        EventBus eventBus = resolveEventBus();
        TestEvent3Handler eventHandler = new TestEvent3Handler();
        eventBus.subscribe(eventHandler);

        //when:
        eventBus.publishEvent(new Event1());
        eventBus.publishEvent(new Event2());
        eventBus.publishEvent(new Event3(false));

        // then:
        assertEquals(0, eventHandler.called);
        assertNull(eventHandler.event);

        //when:
        eventBus.publishEvent(new Event1());
        eventBus.publishEvent(new Event2());
        eventBus.publishEvent(new Event3(true));

        // then:
        assertEquals(1, eventHandler.called);
        assertTrue(eventHandler.event instanceof Event3);
    }

    @Test
    public void publish_event_honors_priority() {
        // given:
        EventBus eventBus = resolveEventBus();
        TestEventHandlerPrio1 eventHandler1 = new TestEventHandlerPrio1();
        TestEventHandlerPrio2 eventHandler2 = new TestEventHandlerPrio2();
        eventBus.subscribe(eventHandler1);
        eventBus.subscribe(eventHandler2);
        Event4 event = new Event4();

        //when:
        eventBus.publishEvent(event);

        // then:
        assertEquals(2, event.handlers.size());
        assertSame(eventHandler1, event.handlers.get(0));
        assertSame(eventHandler2, event.handlers.get(1));
    }

    public interface Event {

    }

    public static abstract class AbstractTestEventHandler {
        protected Event event;
        protected int called;
    }

    public static class TestEvent1Handler extends AbstractTestEventHandler {
        @EventHandler
        public void handleEvent1(Event1 event) {
            this.event = event;
            this.called++;
        }
    }

    public static class TestEvent2Handler extends AbstractTestEventHandler {
        @EventHandler
        public void handleEvent2(Event2 event) {
            this.event = event;
            this.called++;
        }
    }

    public static class TestEventHandler extends AbstractTestEventHandler {
        @EventHandler
        public void handleEvent1(Event1 event) {
            this.event = event;
            this.called++;
        }

        @EventHandler
        public void handleEvent2(Event2 event) {
            this.event = event;
            this.called++;
        }
    }

    public static class TestEvent3Handler extends AbstractTestEventHandler {
        @EventHandler(filters = {Event3Filter.class})
        public void handleEvent3(Event3 event) {
            this.event = event;
            this.called++;
        }
    }

    public static class Event3Filter implements EventFilter<Event3> {
        @Override
        public Boolean apply(EventMetadata<Event3> metadata) {
            return metadata.getEvent().isActive();
        }
    }

    public static class TestEventHandlerPrio1 extends AbstractTestEventHandler {
        @EventHandler(priority = 1)
        public void handleEvent4(Event4 event) {
            this.event = event;
            this.called++;
            event.handlers.add(this);
        }
    }

    public static class TestEventHandlerPrio2 extends AbstractTestEventHandler {
        @EventHandler(priority = 2)
        public void handleEvent4(Event4 event) {
            this.event = event;
            this.called++;
            event.handlers.add(this);
        }
    }

    public static class Event1 implements Event {
    }

    public static class Event2 implements Event {
    }

    public static class Event3 implements Event {
        private boolean active;

        public Event3(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    public static class Event4 implements Event {
        private final List<Object> handlers = new ArrayList<>();
    }
}
