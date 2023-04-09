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
package org.codehaus.griffon.runtime.injection

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Module
import griffon.core.injection.Injector
import griffon.exceptions.InstanceNotFoundException
import griffon.exceptions.MembersInjectionException
import spock.lang.Specification

import javax.inject.Inject

import static griffon.util.AnnotationUtils.named

class GuiceInjectorSpec extends Specification {
    def 'Injector can resolve an object graph'() {
        given:
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Engine)
                bind(Car)
            }
        }
        com.google.inject.Injector gi = Guice.createInjector(module)
        InstanceTracker instanceTracker = new InstanceTracker()
        instanceTracker.injector = gi

        when:
        Injector injector = new GuiceInjector(instanceTracker)
        Car car = injector.getInstance(Car)

        then:
        car
        car.engine
    }

    def 'Injector can resolve an object graph with qualifier'() {
        given:
        Engine engine1 = new Engine()
        Engine engine2 = new Engine()
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Engine).annotatedWith(named('efficient')).toInstance(engine1)
                bind(Engine).annotatedWith(named('poor')).toInstance(engine2)
            }
        }
        com.google.inject.Injector gi = Guice.createInjector(module)
        InstanceTracker instanceTracker = new InstanceTracker()
        instanceTracker.injector = gi

        when:
        Injector injector = new GuiceInjector(instanceTracker)
        Engine engine = injector.getInstance(Engine, named('efficient'))

        then:
        engine == engine1
    }

    def 'Injector fails to find an instance'() {
        given:
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Engine)
                bind(Car)
            }
        }
        com.google.inject.Injector gi = Guice.createInjector(module)
        InstanceTracker instanceTracker = new InstanceTracker()
        instanceTracker.injector = gi

        when:
        Injector injector = new GuiceInjector(instanceTracker)
        injector.getInstance(Vehicle)

        then:
        thrown(InstanceNotFoundException)
    }

    def 'Injector fails to find an instance with qualifier'() {
        given:
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Engine)
                bind(Car)
            }
        }
        com.google.inject.Injector gi = Guice.createInjector(module)
        InstanceTracker instanceTracker = new InstanceTracker()
        instanceTracker.injector = gi

        when:
        Injector injector = new GuiceInjector(instanceTracker)
        injector.getInstance(Car, named("foo"))

        then:
        thrown(InstanceNotFoundException)
    }

    def 'Injector fails to inject members'() {
        given:
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
            }
        }
        com.google.inject.Injector gi = Guice.createInjector(module)
        InstanceTracker instanceTracker = new InstanceTracker()
        instanceTracker.injector = gi

        when:
        Injector injector = new GuiceInjector(instanceTracker)
        injector.injectMembers(new Garage())

        then:
        thrown(MembersInjectionException)
    }

    static interface Vehicle {

    }

    static class Car {
        @Inject Engine engine
    }

    @SuppressWarnings('EmptyClass')
    static class Engine {

    }

    static class Garage {
        @Inject Vehicle vehicle
    }
}
