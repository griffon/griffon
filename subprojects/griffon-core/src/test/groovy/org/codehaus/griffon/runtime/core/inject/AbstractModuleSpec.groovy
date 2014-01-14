package org.codehaus.griffon.runtime.core.inject

import griffon.core.injection.*
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

    static class Engine {

    }

    static class Boat implements Vehicle {

    }

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