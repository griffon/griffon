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
package griffon.util

import griffon.annotations.inject.BindTo
import griffon.annotations.inject.DependsOn
import griffon.annotations.inject.Evicts
import griffon.annotations.inject.Typed
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Named

@Unroll
class AnnotationUtilsSpec extends Specification {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    }

    def "Test setup of #type annotation"() {
        expect:
        type.isAssignableFrom(annotation1.class)
        type.isAssignableFrom(annotation2.class)
        type == annotation1.annotationType()
        annotation1 == annotation2
        annotation1 != annotation3
        annotation1.hashCode() == annotation2.hashCode()
        annotation1.hashCode() != annotation3.hashCode()

        where:
        type   | annotation1                    | annotation2                    | annotation3
        Named  | AnnotationUtils.named('foo')   | AnnotationUtils.named('foo')   | AnnotationUtils.named('bar')
        Typed  | AnnotationUtils.typed(Object)  | AnnotationUtils.typed(Object)  | AnnotationUtils.typed(String)
        BindTo | AnnotationUtils.bindto(Object) | AnnotationUtils.bindto(Object) | AnnotationUtils.bindto(String)
    }

    def "Find out if #target is annotated wit @#annotation.name"() {
        expect:
        outcome == AnnotationUtils.isAnnotatedWith(target, annotation)

        where:
        target       | annotation | outcome
        Object       | Named      | false
        new Object() | Named      | false
        Bean         | Named      | true
        new Bean()   | Named      | true
        Bean         | Typed      | false
        new Bean()   | Typed      | false
    }

    def "Enforce that #target is annotated with @#annotation.name"() {
        when:
        AnnotationUtils.requireAnnotation(target, annotation)

        then:
        thrown(IllegalArgumentException)

        where:
        target       | annotation | outcome
        Object       | Named      | false
        new Object() | Named      | false
        Bean         | Typed      | false
        new Bean()   | Typed      | false
    }

    def "Instances are mapped by their names"() {
        given:
        List instances = [new One(), new Two(), new Three()]

        when:
        Map named = AnnotationUtils.mapInstancesByName(instances, '')

        then:
        'one' in named.keySet()
        'two' in named.keySet()
        'three' in named.keySet()
    }

    def "Instances are evicted and mapped by their names (1)"() {
        given:
        List instances = [new Foo(), new FooEvictor()]

        when:
        Map named = AnnotationUtils.mapInstancesByName(instances, '', 'Object')

        then:
        1 == named.size()
        'foo' in named.keySet()
        [instances[1]] == (named.values() as List)
    }

    def "Instances are evicted and mapped by their names (2)"() {
        given:
        List instances = [new FooEvictor(), new Foo()]

        when:
        Map named = AnnotationUtils.mapInstancesByName(instances, '', 'Object')

        then:
        1 == named.size()
        'foo' in named.keySet()
        [instances[0]] == (named.values() as List)
    }

    def "Multiple instances with same name should cause an exception"() {
        given:
        List instances = [new Foo(), new FooImpersonator()]

        when:
        AnnotationUtils.mapInstancesByName(instances, '', 'Object')

        then:
        thrown(IllegalArgumentException)
    }

    def "Multiple evictions with same name should cause an exception"() {
        given:
        List instances = [new Foo(), new FooEvictor(), new FooEvictor2()]

        when:
        AnnotationUtils.mapInstancesByName(instances, '', 'Object')

        then:
        thrown(IllegalArgumentException)
    }

    def "Instances are sorted by their dependencies"() {
        given:
        List instances = [new Three(), new Two(), new One()]

        when:
        Map sorted = AnnotationUtils.sortByDependencies(instances, '', 'bean')

        then:
        sorted.keySet() == (['one', 'two', 'three'] as Set)
    }

    def "Cyclic dependencies yield empty result"() {
        given:
        List instances = [new Cycle2(), new Cycle1()]

        when:
        Map sorted = AnnotationUtils.sortByDependencies(instances, '', 'bean')

        then:
        !sorted.keySet()
    }

    @Named
    static class Bean {}

    @Named('one')
    static class One {}

    @DependsOn('one')
    @Named('two')
    static class Two {}

    @DependsOn('two')
    @Named('three')
    static class Three {}

    @DependsOn('cycle2')
    @Named('cycle1')
    static class Cycle1 {}

    @DependsOn('cycle1')
    @Named('cycle2')
    static class Cycle2 {}

    @Named('foo')
    static class Foo {}

    @Named('foo')
    static class FooImpersonator {}

    @Named
    @Evicts('foo')
    static class FooEvictor {}

    @Named
    @Evicts('foo')
    static class FooEvictor2 {}
}
