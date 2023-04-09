/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.annotations.core.Nonnull
import griffon.converter.ConverterRegistry
import org.codehaus.griffon.converter.DefaultConverterRegistry
import org.codehaus.griffon.converter.IntegerConverter
import org.codehaus.griffon.runtime.core.DefaultObservableContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


@Unroll
class ObservableContextSpec extends Specification {
    @Shared private ConverterRegistry converterRegistry = new DefaultConverterRegistry()
    @Shared private ObservableContext ctx1 = new DefaultObservableContext(converterRegistry)
    @Shared private ObservableContext ctx2 = new DefaultObservableContext(converterRegistry,ctx1)
    @Shared private ObservableContext ctx3 = new DefaultObservableContext(converterRegistry, ctx1)

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

        when:
        ctx1.addContextEventListener(listener)
        String val = ctx1.removeAs('key')

        then:
        val == 'value'
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.REMOVE
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == 'value'
        listener.contextEvent.newValue == null

        when:
        ctx1.put('key', '1')
        converterRegistry.clear()
        converterRegistry.registerConverter(Integer, IntegerConverter)
        Integer converted = ctx1.removeConverted('key', Integer)

        then:
        converted == 1
        listener.contextEvent.type == ObservableContext.ContextEvent.Type.REMOVE
        listener.contextEvent.key == 'key'
        listener.contextEvent.oldValue == 1
        listener.contextEvent.newValue == null

        cleanup:
        converterRegistry.clear()
    }

    void "listen to lateral context events"() {
        given:
        TestContextEventListener listener2 = new TestContextEventListener()
        ctx2.addContextEventListener(listener2)
        TestContextEventListener listener3 = new TestContextEventListener()
        ctx3.addContextEventListener(listener3)

        when:
        ctx2.put('key', 'value')

        then:
        listener2.contextEvent
        listener2.contextEvent.type == ObservableContext.ContextEvent.Type.ADD
        listener2.contextEvent.key == 'key'
        listener2.contextEvent.oldValue == null
        listener2.contextEvent.newValue == 'value'
        listener3.contextEvent == null
    }

    private static class TestContextEventListener implements ObservableContext.ContextEventListener {
        ObservableContext.ContextEvent contextEvent

        @Override
        void contextChanged(@Nonnull ObservableContext.ContextEvent contextEvent) {
            this.contextEvent = contextEvent
        }
    }
}
