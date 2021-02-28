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
package org.codehaus.griffon.runtime.core.inject

import griffon.core.injection.InstanceBinding
import griffon.core.injection.Module
import griffon.core.injection.ProviderBinding
import griffon.core.injection.ProviderTypeBinding
import griffon.core.injection.TargetBinding
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Qualifier
import java.lang.annotation.Retention

import static griffon.util.AnnotationUtils.named
import static java.lang.annotation.RetentionPolicy.RUNTIME

class AbstractModuleSpec extends Specification {
    def "Verify all kinds of bindings"() {
        given:
        Module module = new MyModule()
        module.configure()
        List<griffon.core.injection.Binding<?>> bindings = module.bindings

        expect:
        // size
        bindings.size() == 8

        // type
        bindings[0] instanceof TargetBinding
        bindings[1] instanceof TargetBinding
        bindings[2] instanceof InstanceBinding
        bindings[3] instanceof ProviderBinding
        bindings[4] instanceof ProviderTypeBinding
        bindings[5] instanceof TargetBinding
        bindings[6] instanceof TargetBinding
        bindings[7] instanceof TargetBinding

        bindings[0].toString().contains 'TargetBinding'
        bindings[1].toString().contains 'TargetBinding'
        bindings[2].toString().contains 'InstanceBinding'
        bindings[3].toString().contains 'ProviderBinding'
        bindings[4].toString().contains 'ProviderTypeBinding'
        bindings[5].toString().contains 'TargetBinding'
        bindings[6].toString().contains 'TargetBinding'
        bindings[7].toString().contains 'TargetBinding'

        // singleton
        !bindings[0].singleton
        !bindings[1].singleton
        bindings[2].singleton
        !bindings[3].singleton
        !bindings[4].singleton
        bindings[5].singleton
        bindings[6].singleton
        !bindings[7].singleton

        // targets
        bindings[0].source == bindings[0].target
        bindings[1].source == Vehicle
        bindings[1].target == Car
        bindings[2].source == Garage
        bindings[2].instance == module.garage
        bindings[3].source == Boat
        bindings[3].provider == module.provider
        bindings[4].source == Plane
        bindings[4].providerType == PlaneProvider
        bindings[5].source == bindings[5].target
        bindings[6].source == Vehicle
        bindings[6].target == Car
        bindings[6].classifier instanceof Named
        bindings[6].classifier.value() == 'Batmovil'
        bindings[7].source == Vehicle
        bindings[7].target == Car
        bindings[7].classifierType == Fast
    }

    def "Broken module due to bad classifier"() {
        given:
        Module module = new FailedModule()

        when:
        module.configure()

        then:
        thrown(IllegalArgumentException)
    }

    static class MyModule extends AbstractModule {
        Garage garage = new Garage()
        Provider<Boat> provider = new BoatProvider()

        protected void doConfigure() {
            bind(Engine)
            bind(Vehicle).to(Car)
            bind(Garage).toInstance(garage)
            bind(Boat).toProvider(provider)
            bind(Plane).toProvider(PlaneProvider)
            bind(Singularity).asSingleton()
            bind(Vehicle).withClassifier(named('Batmovil')).to(Car).asSingleton()
            bind(Vehicle).withClassifier(Fast).to(Car)
        }
    }

    static class FailedModule extends AbstractModule {
        protected void doConfigure() {
            bind(Vehicle).withClassifier(Slow).to(Car)
        }
    }

    static interface Vehicle {

    }

    static class Car implements Vehicle {
        @Inject Engine engine
    }

    @SuppressWarnings('EmptyClass')
    static class Engine {

    }

    @SuppressWarnings('EmptyClass')
    static class Boat implements Vehicle {

    }

    @SuppressWarnings('EmptyClass')
    static class Plane implements Vehicle {

    }

    static class BoatProvider implements Provider<Boat> {
        @Override
        Boat get() {
            new Boat()
        }
    }

    static class PlaneProvider implements Provider<Plane> {
        @Override
        Plane get() {
            new Plane()
        }
    }

    static class Garage {
        @Inject Vehicle vehicle
    }

    @SuppressWarnings('EmptyClass')
    static class Singularity {

    }
}

@Qualifier
@Retention(RUNTIME)
@interface Fast {

}

@Retention(RUNTIME)
@interface Slow {

}