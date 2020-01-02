/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import griffon.core.ExceptionHandler
import griffon.core.ExecutorServiceManager
import griffon.core.event.Event
import griffon.core.event.EventPublisher
import griffon.core.event.EventRouter
import griffon.core.threading.UIThreadManager
import griffon.util.AnnotationUtils
import griffon.util.Instantiator
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.ExceptionHandlerProvider
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.codehaus.griffon.runtime.core.threading.UIThreadManagerTestSupport
import org.codehaus.griffon.runtime.util.SimpleInstantiator
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ExecutorService

class DefaultEventPublisherSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private EventPublisher eventPublisher

    def 'Invoking an event in synchronous mode with listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventPublisher.subscribe(eventHandler)

        when:
        eventPublisher.publishEvent(new MyEvent1())
        eventPublisher.publishEvent(new MyEvent2())

        then:
        eventHandler.event instanceof MyEvent1
    }

    def 'Invoking an event in asynchronous mode with listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventPublisher.subscribe(eventHandler)

        when:
        eventPublisher.publishEventAsync(new MyEvent1())
        eventPublisher.publishEventAsync(new MyEvent2())
        Thread.sleep(200L)

        then:
        eventHandler.event instanceof MyEvent1
    }

    def 'Invoking an event in outside mode with listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventPublisher.subscribe(eventHandler)

        when:
        eventPublisher.publishEventOutsideUI(new MyEvent1())
        eventPublisher.publishEventOutsideUI(new MyEvent2())

        then:
        eventHandler.event instanceof MyEvent1
    }

    def 'Register and unregister listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventPublisher.subscribe(eventHandler)
        eventPublisher.unsubscribe(eventHandler)

        when:

        eventPublisher.publishEvent(new MyEvent1())
        eventPublisher.publishEvent(new MyEvent2())

        then:
        !eventHandler.event
    }

    /*
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
    */

    def 'Register and unregister an invalid listener'() {
        given:
        Object eventHandler = new Object()

        expect:
        eventPublisher.subscribe(eventHandler)
        eventPublisher.unsubscribe(eventHandler)
    }

    def 'Triggering an event with event published disabled does not notify listener'() {
        given:
        TestEventHandler eventHandler = new TestEventHandler()

        when:
        eventPublisher.eventPublishingEnabled = false
        eventPublisher.publishEvent(new MyEvent1())

        then:
        !eventHandler.event
        eventHandler.called == 0
        !eventPublisher.eventPublishingEnabled
    }

    /*
    def 'Query existing listeners by event name'() {
        given:
        TestMyEvent1Handler eventHandler1 = new TestMyEvent1Handler()
        TestMyEvent2Handler eventHandler2 = new TestMyEvent2Handler()

        expect:
        !eventPublisher.eventListeners

        when:

        eventPublisher.subscribe(eventHandler1)
        eventPublisher.subscribe(eventHandler2)
        eventPublisher.subscribe(new TestEventHandler())

        then:

        eventPublisher.eventListeners.size() == 4
        eventPublisher.getEventListeners(MyEvent1.class.name).size() == 2
        eventPublisher.getEventListeners(MyEvent2.class.name).size() == 2
    }
     */

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager)
            bind(UIThreadManager).to(UIThreadManagerTestSupport)
            bind(EventRouter).to(DefaultEventRouter)
            bind(EventPublisher).to(DefaultEventPublisher)
            bind(ExceptionHandler).toProvider(ExceptionHandlerProvider)
            bind(ExecutorService).annotatedWith(AnnotationUtils.named('defaultExecutorService')).toProvider(DefaultExecutorServiceProvider)
            bind(Instantiator).to(SimpleInstantiator).in(Singleton)
        }
    }

    static abstract class AbstractTestEventHandler {
        int called
        Object event
    }

    static class TestMyEvent1Handler extends AbstractTestEventHandler {
        @javax.application.event.EventHandler
        void handleMyEvent1(MyEvent1 event) {
            this.event = event
            this.called++
        }
    }

    static class TestMyEvent2Handler extends AbstractTestEventHandler {
        @javax.application.event.EventHandler
        void handleMyEvent2(MyEvent2 event) {
            this.event = event
            this.called++
        }
    }

    static class TestEventHandler extends AbstractTestEventHandler {
        @javax.application.event.EventHandler
        void handleMyEvent1(MyEvent1 event) {
            this.event = event
            this.called++
        }

        @javax.application.event.EventHandler
        void handleMyEvent2(MyEvent2 event) {
            this.event = event
            this.called++
        }
    }

    static class MyEvent1 extends Event {
    }

    static class MyEvent2 extends Event {
    }
}
