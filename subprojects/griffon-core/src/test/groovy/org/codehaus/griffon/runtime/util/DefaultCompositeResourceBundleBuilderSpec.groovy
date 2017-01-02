/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.util

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.resources.ResourceHandler
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.PropertiesReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject
import javax.inject.Singleton

@Unroll
class DefaultCompositeResourceBundleBuilderSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private ResourceHandler resourceHandler

    @Inject
    private PropertiesReader propertiesReader

    def 'Create throws #exception'() {
        given:
        CompositeResourceBundleBuilder builder = new DefaultCompositeResourceBundleBuilder(resourceHandler, propertiesReader)

        when:
        builder.create(filename, locale)

        then:
        thrown(exception)

        where:
        filename | locale         | exception
        null     | Locale.default | IllegalArgumentException
        'empty'  | null           | NullPointerException
        'empty'  | Locale.default | IllegalArgumentException
    }

    def 'Excercise bundle creation with #filename'() {
        given:
        CompositeResourceBundleBuilder builder = new DefaultCompositeResourceBundleBuilder(resourceHandler, propertiesReader)

        expect:
        builder.create(filename)

        where:
        filename << [
            'org.codehaus.griffon.runtime.util.NotFoundBundle',
            'org.codehaus.griffon.runtime.util.NotABundle',
            'org.codehaus.griffon.runtime.util.BrokenBundle']
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(PropertiesReader).in(Singleton)
        }
    }
}
