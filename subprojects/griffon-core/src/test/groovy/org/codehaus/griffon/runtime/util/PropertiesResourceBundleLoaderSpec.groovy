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
package org.codehaus.griffon.runtime.util

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.env.Environment
import griffon.core.env.Metadata
import griffon.core.resources.ResourceHandler
import griffon.util.AnnotationUtils
import griffon.util.PropertiesReader
import griffon.util.ResourceBundleLoader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.env.EnvironmentProvider
import org.codehaus.griffon.runtime.core.env.MetadataProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

@Unroll
class PropertiesResourceBundleLoaderSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject @Named('properties') private ResourceBundleLoader resourceBundleLoader

    def 'Load bundle and check #key = #value'() {
        when:
        Collection<ResourceBundle> bundles = resourceBundleLoader.load('org/codehaus/griffon/runtime/util/PropertiesBundle')

        then:
        println bundles[0].keySet()
        bundles.size() == 1
        bundles[0].getString(key) == value

        where:
        key << ['string', 'integer', 'keys.bar', 'foo']
        value << ['string', '42', 'bar', 'dev']
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(PropertiesReader).toProvider(guicify(new PropertiesReader.Provider())).in(Singleton)
            bind(Metadata).toProvider(guicify(new MetadataProvider())).in(Singleton)
            bind(Environment).toProvider(guicify(new EnvironmentProvider())).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
        }
    }
}
