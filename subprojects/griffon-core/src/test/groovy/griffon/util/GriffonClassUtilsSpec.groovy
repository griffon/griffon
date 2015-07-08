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
package griffon.util

import griffon.core.Vetoable
import griffon.core.artifact.GriffonArtifact
import griffon.core.artifact.GriffonMvcArtifact
import griffon.core.event.EventPublisher
import griffon.core.i18n.MessageSource
import griffon.core.mvc.MVCHandler
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourceResolver
import griffon.core.threading.ThreadingHandler
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

@Unroll
class GriffonClassUtilsSpec extends Specification {
    void "isResourceResolverMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isResourceResolverMethod(method)

        where:
        [result, method] << methodsOf(ResourceResolver, true).plus(methodsOf(ResourceHandler, false))
    }

    void "isResourceHandlerMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isResourceHandlerMethod(method)

        where:
        [result, method] << methodsOf(ResourceHandler, true).plus(methodsOf(ThreadingHandler, false))
    }

    void "isEventPublisherMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isEventPublisherMethod(method)

        where:
        [result, method] << methodsOf(EventPublisher, true).plus(methodsOf(ResourceHandler, false))
    }

    void "isMessageSourceMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isMessageSourceMethod(method)

        where:
        [result, method] << methodsOf(MessageSource, true).plus(methodsOf(ResourceHandler, false))
    }

    void "isMvcMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isMvcMethod(method)

        where:
        [result, method] << methodsOf(MVCHandler, true)
            .plus(methodsOf(GriffonMvcArtifact, true))
            .plus(methodsOf(EventPublisher, false))
    }

    void "isThreadingMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isThreadingMethod(method)

        where:
        [result, method] << methodsOf(ThreadingHandler, true).plus(methodsOf(ResourceHandler, false))
    }

    void "isObservableMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isObservableMethod(method)

        where:
        [result, method] << methodsOf(griffon.core.Observable, true)
            .plus(methodsOf(Vetoable, true))
            .plus(methodsOf(ResourceHandler, false))
    }

    void "isArtifactMethod() returns #result for '#method'"() {
        expect:
        assert result == GriffonClassUtils.isArtifactMethod(method)

        where:
        [result, method] << methodsOf(GriffonArtifact, true).plus(methodsOf(EventPublisher, false))
    }

    void "InvokeInstanceMethod can resolved overloaded calls inputs=#inputs"() {
        expect:
        output == GriffonClassUtils.invokeInstanceMethod(new Bean(), 'doSomething', *inputs)

        where:
        inputs        || output
        ['foo', 1]    || 'java.lang.String:java.lang.Integer'
        ['foo', []]   || 'java.lang.String:java.lang.Object'
        ['foo', null] || 'java.lang.String:java.lang.Object'
        ['foo']       || 'java.lang.String'
    }

    void "Verify arguments for requireNonEmpty: null byte array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((byte[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null boolean array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((boolean[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null short array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((short[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null int array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((int[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null long array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((long[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null float array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((float[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null double array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((double[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null char array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((char[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null Object array throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((Object[]) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null Collection throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((Collection) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null Map throws NPE"() {
        when:
        GriffonClassUtils.requireNonEmpty((Map) null)

        then:
        thrown(NullPointerException)
    }

    void "Verify arguments for requireNonEmpty: null message for  #input (#input.getClass()) throws IAE"() {
        when:
        GriffonClassUtils.requireNonEmpty(input, null)

        then:
        thrown(IllegalArgumentException)

        where:
        input << [
            new byte[0],
            new boolean[0],
            new short[0],
            new int[0],
            new long[0],
            new float[0],
            new double[0],
            new char[0],
            new Object[0],
            [],
            [:]
        ]
    }

    void "Verify arguments for requireNonEmpty: empty  #input (#input.getClass()) throws ISE"() {
        when:
        GriffonClassUtils.requireNonEmpty(input)

        then:
        thrown(IllegalStateException)

        where:
        input << [
            new byte[0],
            new boolean[0],
            new short[0],
            new int[0],
            new long[0],
            new float[0],
            new double[0],
            new char[0],
            new Object[0],
            [],
            [:]
        ]
    }

    void "Verify arguments for requireNonEmpty: empty  #input (#input.getClass()) with message throws ISE"() {
        when:
        GriffonClassUtils.requireNonEmpty(input, 'message')

        then:
        thrown(IllegalStateException)

        where:
        input << [
            new byte[0],
            new boolean[0],
            new short[0],
            new int[0],
            new long[0],
            new float[0],
            new double[0],
            new char[0],
            new Object[0],
            [],
            [:]
        ]
    }

    void "RequireNonEmpty is successful for #input (#input.getClass())"() {
        when:
        def output1 = GriffonClassUtils.requireNonEmpty(input)
        def output2 = GriffonClassUtils.requireNonEmpty(input, 'message')

        then:
        input == output1
        input == output2

        where:
        input << [
            [(byte) 1] as byte[],
            [(short) 1] as short[],
            [(int) 1] as int[],
            [(long) 1] as long[],
            [(float) 1] as float[],
            [(double) 1] as double[],
            [(char) 1] as char[],
            [true] as boolean[],
            [new Object()] as Object[],
            [1],
            [key: 'value']
        ]
    }

    private static List methodDescriptorsOf(Class<?> type, boolean result) {
        List data = []
        for (Method m : type.methods) {
            data << [result, MethodDescriptor.forMethod(m, true)]
        }
        data
    }

    private static List methodsOf(Class<?> type, boolean result) {
        List data = []
        for (Method m : type.methods) {
            data << [result, m]
        }
        data
    }

    static class Bean {
        def doSomething(String arg0, Integer arg1) {
            String.name + ':' + Integer.name
        }

        def doSomething(String arg0, Object arg1) {
            String.name + ':' + Object.name
        }

        def doSomething(String arg0) {
            String.name
        }
    }
}
