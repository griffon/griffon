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

    private static List methodsOf(Class<?> type, boolean result) {
        List data = []
        for (Method m : type.methods) {
            data << [result, MethodDescriptor.forMethod(m, true)]
        }
        data
    }
}
