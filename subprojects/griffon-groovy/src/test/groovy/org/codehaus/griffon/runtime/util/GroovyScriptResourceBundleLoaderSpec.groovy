/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
import griffon.core.env.Environment
import griffon.core.env.Metadata
import griffon.core.resources.ResourceHandler
import griffon.util.AnnotationUtils
import griffon.util.ConfigReader
import griffon.util.Instantiator
import griffon.util.ResourceBundleLoader
import griffon.util.ResourceBundleReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.env.EnvironmentProvider
import org.codehaus.griffon.runtime.core.env.MetadataProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.groovy.util.GroovyScriptResourceBundleLoader
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

@Unroll
class GroovyScriptResourceBundleLoaderSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject @Named('groovy') private ResourceBundleLoader resourceBundleLoader

    def 'Load bundle #name and check #key = #value'() {
        when:
        Collection<ResourceBundle> bundles = resourceBundleLoader.load(name)
        println bundles

        then:
        println bundles[0].keySet()
        bundles.size() == 1
        bundles[0].getObject(key) == value

        where:
        name << (['org/codehaus/griffon/runtime/util/GroovyClassBundle'] * 4) +
            (['org/codehaus/griffon/runtime/util/JavaClassBundle'] * 4) +
            (['org/codehaus/griffon/runtime/util/GroovyScriptBundle'] * 4)
        key << (['string', 'integer', 'keys.bar', 'foo'] * 3)
        value << (['string', 42, 'bar', 'dev'] * 3)
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(ResourceBundleReader).toProvider(guicify(new ResourceBundleReader.Provider())).in(Singleton)
            bind(ConfigReader).toProvider(guicify(new ConfigReader.Provider())).in(Singleton)
            bind(Metadata).toProvider(guicify(new MetadataProvider())).in(Singleton)
            bind(Environment).toProvider(guicify(new EnvironmentProvider())).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('groovy')).to(GroovyScriptResourceBundleLoader).in(Singleton)
            bind(Instantiator).toInstance({ c -> c.newInstance() } as Instantiator)
        }
    }
}
