package org.codehaus.griffon.runtime.core.event

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.CallableWithArgs
import griffon.core.ExecutorServiceManager
import griffon.core.event.Event
import griffon.core.event.EventRouter
import griffon.core.threading.UIThreadManager
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.threading.UIThreadManagerTestSupport
import org.junit.Rule
import spock.lang.Specification

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Singleton

class DefaultEventRouterSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private EventRouter eventRouter

    def 'Invoking an event by name in synchronous mode with a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)

        when:

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)

        when:

        eventRouter.publishAsync(eventName1, [1, 'one'])
        eventRouter.publishAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)

        when:

        eventRouter.publishOutsideUI(eventName1, [1, 'one'])
        eventRouter.publishOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in synchronous mode with a Map listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in asynchronous mode with a Map listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publishAsync(eventName1, [1, 'one'])
        eventRouter.publishAsync(eventName2, [2, 'two'])
        Thread.sleep(200L)

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event by name in outside mode with a Map listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publishOutsideUI(eventName1, [1, 'one'])
        eventRouter.publishOutsideUI(eventName2, [2, 'two'])

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

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

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

        eventRouter.publishAsync(eventName1, [1, 'one'])
        eventRouter.publishAsync(eventName2, [2, 'two'])
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

        eventRouter.publishOutsideUI(eventName1, [1, 'one'])
        eventRouter.publishOutsideUI(eventName2, [2, 'two'])

        then:

        eventHandler.args == [1, 'one']
    }

    def 'Invoking an event in synchronous mode with a callable listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publish(event1)
        eventRouter.publish(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in asynchronous mode with a callable listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishAsync(event1)
        eventRouter.publishAsync(event2)
        Thread.sleep(200L)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in outside mode with a callable listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishOutsideUI(event1)
        eventRouter.publishOutsideUI(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in synchronous mode with a Map listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publish(event1)
        eventRouter.publish(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in asynchronous mode with a Map listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishAsync(event1)
        eventRouter.publishAsync(event2)
        Thread.sleep(200L)

        then:

        eventHandler.args == [event1]
    }

    def 'Invoking an event in outside mode with a Map listener'() {
        given:

        Event event1 = new MyEvent1(new Object())
        Event event2 = new MyEvent2(new Object())
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(event1.class, eventHandler)

        when:

        eventRouter.publishOutsideUI(event1)
        eventRouter.publishOutsideUI(event2)

        then:

        eventHandler.args == [event1]
    }

    def 'Register and unregister a callable listener by name'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(eventName1, eventHandler)
        eventRouter.removeEventListener(eventName1, eventHandler)

        when:

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a Map listener by name'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener([(eventName1): eventHandler])
        eventRouter.removeEventListener([(eventName1): eventHandler])

        when:

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

        then:

        !eventHandler.args
    }

    def 'Register and unregister a callable listener'() {
        given:

        String eventName1 = MyEvent1.simpleName
        String eventName2 = MyEvent2.simpleName
        TestEventHandler eventHandler = new TestEventHandler()
        eventRouter.addEventListener(MyEvent1, eventHandler)
        eventRouter.removeEventListener(MyEvent1, eventHandler)

        when:

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

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

        eventRouter.publish(eventName1)
        eventRouter.publish(eventName2)

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

        eventRouter.publish(eventName1, [1, 'one'])
        eventRouter.publish(eventName2, [2, 'two'])

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

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager).in(Singleton)
            bind(UIThreadManager).to(UIThreadManagerTestSupport).in(Singleton)
            bind(EventRouter).to(DefaultEventRouter).in(Singleton)
        }
    }

    static class TestEventHandler implements CallableWithArgs<Void> {
        Object[] args

        @Override
        Void call(@Nonnull Object[] args) {
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

class Subject {
    Object[] args

    final Map<String, CallableWithArgs<?>> events = [
        MyEvent1: new CallableWithArgs<Void>() {
            Void call(@Nonnull Object[] args) {
                Subject.this.args = args
                null
            }
        }
    ]
}