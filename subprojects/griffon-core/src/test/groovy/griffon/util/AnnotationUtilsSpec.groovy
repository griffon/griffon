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

import griffon.inject.DependsOn
import griffon.inject.Typed
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Named

@Unroll
class AnnotationUtilsSpec extends Specification {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    }

    def "Find out if #target are annotated wit @#annotation.name"() {
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
}
