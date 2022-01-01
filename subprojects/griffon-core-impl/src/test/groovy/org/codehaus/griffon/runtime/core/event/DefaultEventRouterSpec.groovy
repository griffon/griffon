/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
import griffon.annotations.event.EventHandler
import griffon.core.ExceptionHandler
import griffon.core.ExecutorServiceManager
import griffon.core.Instantiator
import griffon.core.event.Event
import griffon.core.event.EventRouter
import griffon.core.threading.UIThreadManager
import griffon.util.AnnotationUtils
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

class DefaultEventRouterSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private EventRouter eventRouter

    def 'Invoking an event in synchronous mode with listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventRouter.subscribe(eventHandler)

        when:
        eventRouter.publishEvent(new MyEvent1())
        eventRouter.publishEvent(new MyEvent2())

        then:
        eventHandler.event instanceof MyEvent1
    }

    def 'Invoking an event in asynchronous mode with listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventRouter.subscribe(eventHandler)

        when:
        eventRouter.publishEventAsync(new MyEvent1())
        eventRouter.publishEventAsync(new MyEvent2())
        Thread.sleep(200L)

        then:
        eventHandler.event instanceof MyEvent1
    }

    def 'Invoking an event in outside mode with listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventRouter.subscribe(eventHandler)

        when:
        eventRouter.publishEventOutsideUI(new MyEvent1())
        eventRouter.publishEventOutsideUI(new MyEvent2())

        then:
        eventHandler.event instanceof MyEvent1
    }

    def 'Register and unregister listener'() {
        given:
        TestMyEvent1Handler eventHandler = new TestMyEvent1Handler()
        eventRouter.subscribe(eventHandler)
        eventRouter.unsubscribe(eventHandler)

        when:

        eventRouter.publishEvent(new MyEvent1())
        eventRouter.publishEvent(new MyEvent2())

        then:
        !eventHandler.event
    }

    /*
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
    */

    def 'Register and unregister an invalid listener'() {
        given:
        Object eventHandler = new Object()

        expect:
        eventRouter.subscribe(eventHandler)
        eventRouter.unsubscribe(eventHandler)
    }

    def 'Triggering an event with event published disabled does not notify listener'() {
        given:
        TestEventHandler eventHandler = new TestEventHandler()

        when:
        eventRouter.eventPublishingEnabled = false
        eventRouter.publishEvent(new MyEvent1())

        then:
        !eventHandler.event
        eventHandler.called == 0
        !eventRouter.eventPublishingEnabled
    }

    /*
    def 'Query existing listeners by event name'() {
        given:
        TestMyEvent1Handler eventHandler1 = new TestMyEvent1Handler()
        TestMyEvent2Handler eventHandler2 = new TestMyEvent2Handler()

        expect:
        !eventRouter.eventListeners

        when:

        eventRouter.subscribe(eventHandler1)
        eventRouter.subscribe(eventHandler2)
        eventRouter.subscribe(new TestEventHandler())

        then:

        eventRouter.eventListeners.size() == 4
        eventRouter.getEventListeners(MyEvent1.class.name).size() == 2
        eventRouter.getEventListeners(MyEvent2.class.name).size() == 2
    }
     */

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager)
            bind(UIThreadManager).to(UIThreadManagerTestSupport)
            bind(EventRouter).to(DefaultEventRouter)
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
        @EventHandler
        void handleMyEvent1(MyEvent1 event) {
            this.event = event
            this.called++
        }
    }

    static class TestMyEvent2Handler extends AbstractTestEventHandler {
        @EventHandler
        void handleMyEvent2(MyEvent2 event) {
            this.event = event
            this.called++
        }
    }

    static class TestEventHandler extends AbstractTestEventHandler {
        @EventHandler
        void handleMyEvent1(MyEvent1 event) {
            this.event = event
            this.called++
        }

        @EventHandler
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