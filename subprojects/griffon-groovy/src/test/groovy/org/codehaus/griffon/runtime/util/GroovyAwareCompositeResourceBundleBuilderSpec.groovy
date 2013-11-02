package org.codehaus.griffon.runtime.util

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.resources.ResourceHandler
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.ConfigReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

class GroovyAwareCompositeResourceBundleBuilderSpec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    def "Load Groovy and properties bundles"() {
        given:
        ResourceBundle bundle = bundleBuilder.create('org.codehaus.griffon.runtime.util.GroovyBundle')

        expect:
        bundle.getString(key) == value

        where:
        key              || value
        'properties.key' || 'properties'
        'groovy.key'     || 'groovy'
    }

    def "Load Java and properties bundles"() {
        given:
        ResourceBundle bundle = bundleBuilder.create('org.codehaus.griffon.runtime.util.JavaBundle')

        expect:
        bundle.getString(key) == value

        where:
        key              || value
        'properties.key' || 'properties'
        'java.key'       || 'java'
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(ConfigReader).toProvider(guicify(new ConfigReader.Provider()))
            bind(CompositeResourceBundleBuilder).to(GroovyAwareCompositeResourceBundleBuilder).in(Singleton)
        }
    }
}
