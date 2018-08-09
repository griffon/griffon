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
package org.codehaus.griffon.runtime.core.event

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.annotations.core.Nullable
import griffon.core.ExceptionHandler
import griffon.core.ExecutorServiceManager
import griffon.core.RunnableWithArgs
import griffon.core.event.Event
import griffon.core.event.EventRouter
import griffon.core.threading.UIThreadManager
import griffon.util.AnnotationUtils
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.ExceptionHandlerProvider
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.codehaus.griffon.runtime.core.threading.UIThreadManagerTestSupport
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import java.util.concurrent.ExecutorService

class DefaultEventRouterSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private EventRouter eventRouter

    def 'Invoking an event by name in synchronous mode with a runnable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a runnable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)

        when:

        eventRouter.publishEventAsync(eventName1, [1, 'one'])
        eventRouter.publishEventAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a runnable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)

        when:

        eventRouter.publishEventOutsideUI(eventName1, [1, 'one'])
        eventRouter.publishEventOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in synchronous mode with a Map listener (runnable)'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a Map listener (runnable)'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publishEventAsync(eventName1, [1, 'one'])
        eventRouter.publishEventAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a Map listener (runnable)'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publishEventOutsideUI(eventName1, [1, 'one'])
        eventRouter.publishEventOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in synchronous mode with a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventRouter.addEventListener(eventHandler)

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventRouter.addEventListener(eventHandler)

        when:

        eventRouter.publishEventAsync(eventName1, [1, 'one'])
        eventRouter.publishEventAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventRouter.addEventListener(eventHandler)

        when:

        eventRouter.publishEventOutsideUI(eventName1, [1, 'one'])
        eventRouter.publishEventOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event in synchronous mode with a runnable listener'() {
        given:

        Event event1 = new MyEvent1()
        Event event2 = new MyEvent2()
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishEvent(event1)
        eventRouter.publishEvent(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in asynchronous mode with a runnable listener'() {
        given:

        Event event1 = new MyEvent1()
        Event event2 = new MyEvent2()
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishEventAsync(event1)
        eventRouter.publishEventAsync(event2)
        Thread.sleep(200L)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in outside mode with a runnable listener (runnable)'() {
        given:

        Event event1 = new MyEvent1()
        Event event2 = new MyEvent2()
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishEventOutsideUI(event1)
        eventRouter.publishEventOutsideUI(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in synchronous mode with a Map listener (runnable)'() {
        given:

        Event event1 = new MyEvent1()
        Event event2 = new MyEvent2()
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishEvent(event1)
        eventRouter.publishEvent(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in asynchronous mode with a Map listener (runnable)'() {
        given:

        Event event1 = new MyEvent1()
        Event event2 = new MyEvent2()
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishEventAsync(event1)
        eventRouter.publishEventAsync(event2)
        Thread.sleep(200L)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in outside mode with a Map listener (runnable)'() {
        given:

        Event event1 = new MyEvent1()
        Event event2 = new MyEvent2()
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishEventOutsideUI(event1)
        eventRouter.publishEventOutsideUI(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Register and unregister a runnable listener by name'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)
        eventRouter.removeEventListener(eventName1, eventHandler)

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a Map listener by name (runnable)'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])
        eventRouter.removeEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a runnable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler = new TestRunnableEventHandler()
        eventRouter.addEventListener(MyEvent1, eventHandler)
        eventRouter.removeEventListener(MyEvent1, eventHandler)

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a bean listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        EventHandler eventHandler = new EventHandler()
        eventRouter.addEventListener(eventHandler)
        eventRouter.removeEventListener(eventHandler)

        when:

        eventRouter.publishEvent(eventName1)
        eventRouter.publishEvent(eventName2)

        then:

        !eventHandler.args
    }

    def 'Register and unregister a bean listener with nested listeners'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        Subject subject = new Subject()
        eventRouter.addEventListener(subject)
        eventRouter.addEventListener(subject.events)
        eventRouter.removeEventListener(subject)

        when:

        eventRouter.publishEvent(eventName1, [1, 'one'])
        eventRouter.publishEvent(eventName2, [2, 'two'])

        then:

        !subject.args
    }

    def 'Register and unregister an invalid bean listener'() {
        given:

        def eventHandler = new Object()

        expect:

        eventRouter.addEventListener(eventHandler)
        eventRouter.removeEventListener(eventHandler)
    }

    def 'Query existing listeners by event name'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestRunnableEventHandler eventHandler1 = new TestRunnableEventHandler()
        TestRunnableEventHandler eventHandler2 = new TestRunnableEventHandler()

        expect:

        !eventRouter.eventListeners

        when:

        eventRouter.addEventListener(eventName1, eventHandler1)
        eventRouter.addEventListener([(eventName2): eventHandler2])
        eventRouter.addEventListener(new EventHandler())
        eventRouter.addEventListener(new Subject().events)

        then:

        eventRouter.eventListeners.size() == 5
        eventRouter.getEventListeners(eventName1).size() == 3
        eventRouter.getEventListeners(eventName2).size() == 2
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager)
            bind(UIThreadManager).to(UIThreadManagerTestSupport)
            bind(EventRouter).to(DefaultEventRouter)
            bind(ExceptionHandler).toProvider(ExceptionHandlerProvider)
            bind(ExecutorService).annotatedWith(AnnotationUtils.named('defaultExecutorService')).toProvider(DefaultExecutorServiceProvider)
        }
    }

    static class TestRunnableEventHandler implements RunnableWithArgs {
        Object[] args

        @Override
        void run(@Nullable Object... args) {
            this.args = args
        }
    }

    static class MyEvent1 extends Event {
    }

    static class MyEvent2 extends Event {
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

class Subject {
    Object[] args

    final Map<String, Object> events = [
        MyEvent1: new RunnableWithArgs() {
            void run(@Nullable Object... args) {
                Subject.this.args = args
            }
        },
        MyEvent2: new RunnableWithArgs() {
            void run(@Nullable Object... args) {
                Subject.this.args = args
            }
        }
    ]
}