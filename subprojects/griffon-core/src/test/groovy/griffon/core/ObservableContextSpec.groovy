/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.core

import org.codehaus.griffon.runtime.core.DefaultObservableContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class ObservableContextSpec extends Specification {
    @Shared private ObservableContext ctx1 = new DefaultObservableContext()
    @Shared private ObservableContext ctx2 = new DefaultObservableContext(ctx1)

    def setup() {
        ctx1['foo'] = 'foo'
        ctx2['bar'] = 'bar'
    }

    def "Exercise listener methods"() {
        given:
        TestContextEventListener listener = new TestContextEventListener()

        when:
        ctx2.addContextEventListener(listener)

        then:
        ctx2.contextEventListeners == ([listener] as griffon.core.ObservableContext.ContextEventListener[])

        when:
        ctx2.addContextEventListener(listener)

        then:
        ctx2.contextEventListeners.size() == 1

        when:
        ctx2.removeContextEventListener(listener)

        then:
        ctx2.contextEventListeners.size() == 0
    }

    def "Listen to context events"() {
        given:
        TestContextEventListener listener = new TestContextEventListener()
        ctx2.addContextEventListener(listener)

        when:
        ctx2.put('key', 'value')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.ADD
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == null
        listener.contextEvent.newValue == 'value'

        when:
        ctx2.put('key', 'new')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.UPDATE
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == 'value'
        listener.contextEvent.newValue == 'new'

        when:
        ctx2.remove('key')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.REMOVE
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == 'new'
        listener.contextEvent.newValue == null

        when:
        ctx1.put('foo', 'bar')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.UPDATE
        listener.contextEvent.key == 'foo'
        listener.contextEvent.oldValue == 'foo'
        listener.contextEvent.newValue == 'bar'

        when:
        ctx1.put('key', 'value')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.ADD
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == null
        listener.contextEvent.newValue == 'value'

        when:
        ctx1.put('key', 'new')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.UPDATE
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == 'value'
        listener.contextEvent.newValue == 'new'

        when:
        ctx1.remove('key')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.REMOVE
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == 'new'
        listener.contextEvent.newValue == null

        when:
        ctx2.put('foo', 'value')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.UPDATE
        listener.contextEvent.key == 'foo'
        listener.contextEvent.oldValue == 'bar'
        listener.contextEvent.newValue == 'value'

        when:
        ctx2.remove('foo')

        then:
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.UPDATE
        listener.contextEvent.key == 'foo'
        listener.contextEvent.oldValue == 'value'
        listener.contextEvent.newValue == 'bar'

        when:
        ctx1.put('foo', 'foo')
        listener.contextEvent = null
        ctx2.put('foo', 'foo')

        then:
        !listener.contextEvent

        when:
        listener.contextEvent = null
        ctx2.remove('foo')

        then:
        !listener.contextEvent

        when:
        listener.contextEvent = null
        ctx2.remove('undefined')

        then:
        !listener.contextEvent

        when:
        listener.contextEvent = null
        ctx1.put('bar', 'foo')

        then:
        !listener.contextEvent

        when:
        ctx2.destroy()
        ctx1.put('key', 'value')

        then:
        listener.contextEvent == null
    }

    private static class TestContextEventListener implements ObservableContext.ContextEventListener {
        ObservableContext.ContextEvent contextEvent

        @Override
        void contextChanged(@Nonnull ObservableContext.ContextEvent contextEvent) {
            this.contextEvent = contextEvent
        }
    }
}
