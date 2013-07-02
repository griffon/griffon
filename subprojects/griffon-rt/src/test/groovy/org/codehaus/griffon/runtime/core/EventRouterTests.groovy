package org.codehaus.griffon.runtime.core

import griffon.core.Event
import griffon.core.EventRouter

class EventRouterTests extends GroovyTestCase {
    void testSmoke() {
        // given
        EventRouter eventRouter = new CustomEventRouter()
        CustomEventListener listener = new CustomEventListener()
        eventRouter.addEventListener(listener)
        assert !listener.event

        // when
        Event event = new CustomEvent(eventRouter, 'EVENT')
        eventRouter.publish(event)

        // then
        assert listener.event == event

        // when
        listener.event = null
        eventRouter.publishOutsideUI(event)

        // then
        assert listener.event == event

        // when
        listener.event = null
        eventRouter.publishAsync(event)

        // then
        assert listener.event == event
    }

    private static class CustomEventListener {
        Event event

        void onCustomEvent(CustomEvent event) {
            this.event = event
        }
    }

    private static class CustomEventRouter extends AbstractEventRouter {
        @Override
        protected void doPublishOutsideUI(Runnable publisher) {
            publisher.run()
        }

        @Override
        protected void doPublishAsync(Runnable publisher) {
            publisher.run()
        }
    }

    private static class CustomEvent extends Event {
        final String arg

        CustomEvent(Object source, String arg) {
            super(source)
            this.arg = arg
        }
    }
}
