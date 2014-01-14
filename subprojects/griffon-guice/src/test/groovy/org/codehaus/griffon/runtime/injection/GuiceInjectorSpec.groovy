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

        when:
        Injector injector = new GuiceInjector(gi)
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

        when:
        Injector injector = new GuiceInjector(gi)
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

        when:
        Injector injector = new GuiceInjector(gi)
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

        when:
        Injector injector = new GuiceInjector(gi)
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

        when:
        Injector injector = new GuiceInjector(gi)
        injector.injectMembers(new Garage())

        then:
        thrown(MembersInjectionException)
    }

    static interface Vehicle {

    }

    static class Car {
        @Inject Engine engine
    }

    static class Engine {

    }

    static class Garage {
        @Inject Vehicle vehicle
    }
}
