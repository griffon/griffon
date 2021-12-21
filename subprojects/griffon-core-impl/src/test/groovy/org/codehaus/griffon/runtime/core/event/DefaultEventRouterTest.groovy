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
package org.codehaus.griffon.runtime.core.event

import com.google.guiceberry.GuiceBerryModule
import com.google.inject.AbstractModule
import griffon.core.ExceptionHandler
import griffon.core.ExecutorServiceManager
import griffon.core.Instantiator
import griffon.core.event.EventBus
import griffon.core.event.EventRouter
import griffon.core.threading.UIThreadManager
import griffon.util.AnnotationUtils
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension
import name.falgout.jeffrey.testing.junit.guice.IncludeModule
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.ExceptionHandlerProvider
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.codehaus.griffon.runtime.core.threading.UIThreadManagerTestSupport
import org.codehaus.griffon.runtime.util.SimpleInstantiator
import org.junit.jupiter.api.extension.ExtendWith
import tck.griffon.core.event.EventBusTest

import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ExecutorService

import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(GuiceExtension)
@IncludeModule(TestModule)
class DefaultEventRouterTest extends EventBusTest {
    @Inject
    private EventRouter eventRouter

    @Override
    protected EventBus resolveEventBus() {
        return eventRouter
    }

    def 'Invoking an event in outside mode with listener'() {
        // given:
        TestEvent1Handler eventHandler = new TestEvent1Handler()
        eventRouter.subscribe(eventHandler)

        // when:
        eventRouter.publishEventOutsideUI(new Event1())
        eventRouter.publishEventOutsideUI(new Event2())

        // then:
        assertTrue eventHandler.event instanceof Event1
    }

    /*
    def 'Register and unregister a bean listener with nested listeners'() {
        given:

        String eventName1 = Event1.simpleName
        String eventName2 = Event2.simpleName
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

    def 'Register and unregister an invalid listener'() {
        // given:
        Object eventHandler = new Object()

        // expect:
        eventRouter.subscribe(eventHandler)
        eventRouter.unsubscribe(eventHandler)
    }
    */

    def 'Triggering an event with event published disabled does not notify listener'() {
        // given:
        TestEventHandler eventHandler = new TestEventHandler()

        // when:
        eventRouter.eventPublishingEnabled = false
        eventRouter.publishEvent(new Event1())

        // then:
        assertTrue !eventHandler.event
        assertTrue eventHandler.called == 0
        assertTrue !eventRouter.eventPublishingEnabled
    }

    /*
    def 'Query existing listeners by event name'() {
        given:
        TestEvent1Handler eventHandler1 = new TestEvent1Handler()
        TestEvent2Handler eventHandler2 = new TestEvent2Handler()

        expect:
        !eventRouter.eventListeners

        when:

        eventRouter.subscribe(eventHandler1)
        eventRouter.subscribe(eventHandler2)
        eventRouter.subscribe(new TestEventHandler())

        then:

        eventRouter.eventListeners.size() == 4
        eventRouter.getEventListeners(Event1.class.name).size() == 2
        eventRouter.getEventListeners(Event2.class.name).size() == 2
    }
     */

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager).in(Singleton)
            bind(UIThreadManager).to(UIThreadManagerTestSupport).in(Singleton)
            bind(EventRouter).to(DefaultEventRouter).in(Singleton)
            bind(ExceptionHandler).toProvider(ExceptionHandlerProvider).in(Singleton)
            bind(ExecutorService).annotatedWith(AnnotationUtils.named('defaultExecutorService')).toProvider(DefaultExecutorServiceProvider).in(Singleton)
            bind(Instantiator).to(SimpleInstantiator).in(Singleton)
        }
    }
}