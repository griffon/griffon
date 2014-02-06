/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.core.event

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.CallableWithArgs
import griffon.core.ExecutorServiceManager
import griffon.core.event.Event
import griffon.core.event.EventPublisher
import griffon.core.event.EventRouter
import griffon.core.threading.UIThreadManager
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.threading.UIThreadManagerTestSupport
import org.junit.Rule
import spock.lang.Specification

import javax.annotation.Nullable
import javax.inject.Inject
import javax.inject.Singleton

class DefaultEventPublisherSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private EventPublisher eventPublisher

    def 'Invoking an event by name in synchronous mode with a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(eventName1, eventHandler)

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(eventName1, eventHandler)

        when:

        eventPublisher.publishEventAsync(eventName1, [1, 'one'])
        eventPublisher.publishEventAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(eventName1, eventHandler)

        when:

        eventPublisher.publishEventOutsideUI(eventName1, [1, 'one'])
        eventPublisher.publishEventOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in synchronous mode with a Map listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener([(eventName1): eventHandler])

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a Map listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener([(eventName1): eventHandler])

        when:

        eventPublisher.publishEventAsync(eventName1, [1, 'one'])
        eventPublisher.publishEventAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a Map listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener([(eventName1): eventHandler])

        when:

        eventPublisher.publishEventOutsideUI(eventName1, [1, 'one'])
        eventPublisher.publishEventOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in synchronous mode with a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventPublisher.addEventListener(eventHandler)

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventPublisher.addEventListener(eventHandler)

        when:

        eventPublisher.publishEventAsync(eventName1, [1, 'one'])
        eventPublisher.publishEventAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventPublisher.addEventListener(eventHandler)

        when:

        eventPublisher.publishEventOutsideUI(eventName1, [1, 'one'])
        eventPublisher.publishEventOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event in synchronous mode with a callable listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(event1.class, eventHandler)

        when:

        eventPublisher.publishEvent(event1)
        eventPublisher.publishEvent(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in asynchronous mode with a callable listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(event1.class, eventHandler)

        when:

        eventPublisher.publishEventAsync(event1)
        eventPublisher.publishEventAsync(event2)
        Thread.sleep(200L)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in outside mode with a callable listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(event1.class, eventHandler)

        when:

        eventPublisher.publishEventOutsideUI(event1)
        eventPublisher.publishEventOutsideUI(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in synchronous mode with a Map listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(event1.class, eventHandler)

        when:

        eventPublisher.publishEvent(event1)
        eventPublisher.publishEvent(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in asynchronous mode with a Map listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(event1.class, eventHandler)

        when:

        eventPublisher.publishEventAsync(event1)
        eventPublisher.publishEventAsync(event2)
        Thread.sleep(200L)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in outside mode with a Map listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(event1.class, eventHandler)

        when:

        eventPublisher.publishEventOutsideUI(event1)
        eventPublisher.publishEventOutsideUI(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Register and unregister a callable listener by name'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(eventName1, eventHandler)
        eventPublisher.removeEventListener(eventName1, eventHandler)

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a Map listener by name'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener([(eventName1): eventHandler])
        eventPublisher.removeEventListener([(eventName1): eventHandler])

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventPublisher.addEventListener(MyEvent1, eventHandler)
        eventPublisher.removeEventListener(MyEvent1, eventHandler)

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventPublisher.addEventListener(eventHandler)
        eventPublisher.removeEventListener(eventHandler)

        when:

        eventPublisher.publishEvent(eventName1)
        eventPublisher.publishEvent(eventName2)

        then:

        !eventHandler.args
    }

    def 'Register and unregister a bean listener with nested listeners'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        Subject subject = new Subject()
        eventPublisher.addEventListener(subject)
        eventPublisher.addEventListener(subject.events)
        eventPublisher.removeEventListener(subject)

        when:

        eventPublisher.publishEvent(eventName1, [1, 'one'])
        eventPublisher.publishEvent(eventName2, [2, 'two'])

        then:

        !subject.args
    }

    def 'Register and unregister an invalid bean listener'() {
        given:

        def eventHandler = new Object()

        expect:

        eventPublisher.addEventListener(eventHandler)
        eventPublisher.removeEventListener(eventHandler)
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager).in(Singleton)
            bind(UIThreadManager).to(UIThreadManagerTestSupport).in(Singleton)
            bind(EventRouter).to(DefaultEventRouter).in(Singleton)
            bind(EventPublisher).to(DefaultEventPublisher).in(Singleton)
        }
    }
    
    static class TestEventHandler implements CallableWithArgs<Void> {
        Object[] args

        @Override
        @Nullable
        Void call(@Nullable Object... args) {
            this.args = args
            null
        }
    }

    static class MyEvent1 extends Event {
        MyEvent1(Object source) {
            super(source)
        }
    }

    static class MyEvent2 extends Event {
        MyEvent2(Object source) {
            super(source)
        }
    }

    static class EventHandler {
        List args

        void onMyEvent1(int arg0, String arg1) {
            this.args = [arg0, arg1]
        }

        void onMyEvent1(MyEvent1 event) {
            this.args = [event]
        }
    }
}
