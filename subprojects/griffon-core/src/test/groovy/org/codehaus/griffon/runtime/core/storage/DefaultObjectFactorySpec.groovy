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
package org.codehaus.griffon.runtime.core.storage

import griffon.core.Configuration
import griffon.core.GriffonApplication
import griffon.core.event.EventRouter
import griffon.core.storage.ObjectFactory
import griffon.util.AbstractMapResourceBundle
import griffon.util.ExpandableResourceBundle
import integration.TestGriffonApplication
import org.codehaus.griffon.runtime.core.AbstractConfiguration
import org.codehaus.griffon.runtime.core.event.DefaultEventRouter
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class DefaultObjectFactorySpec extends Specification {
    void "Find config under #key"() {
        given:
        def configuration = new TestConfiguration()
        def application = new DummyGriffonApplication()
        ObjectFactory factory = new DefaultObjectFactory(configuration, application)

        when:
        factory.singleKey = singleKey
        factory.pluralKey = pluralKey
        String actual = factory.create(key)
        factory.event('RandomEvent', [])

        then:
        actual == expected
        configuration == factory.configuration
        application == factory.application

        cleanup:
        factory.destroy(key, actual)

        where:
        singleKey | pluralKey | key           | expected
        'string'  | 'strings' | 'default'     | 'single'
        'string'  | 'strings' | 'internal'    | 'multiple'
        'string'  | 'strings' | 'nonexistant' | null
        's'       | 'ss'      | 'default'     | null
        's'       | 'ss'      | 'internal'    | null
        's'       | 'ss'      | 'nonexistant' | null
    }

    private
    static class DummyGriffonApplication extends TestGriffonApplication {
        EventRouter eventRouter = new DefaultEventRouter()

        @Nonnull
        @Override
        EventRouter getEventRouter() {
            eventRouter
        }
    }
}

class TestConfiguration extends AbstractConfiguration {
    private ResourceBundle resourceBundle

    TestConfiguration() {
        resourceBundle = ExpandableResourceBundle.wrapResourceBundle(new AbstractMapResourceBundle() {
            @Override
            protected void initialize(@Nonnull Map<String, Object> entries) {
                entries['string'] = [
                    type: 'single'
                ]
                entries['strings'] = [
                    internal: [
                        type: 'multiple'
                    ]
                ]
            }
        })
    }

    @Override
    boolean containsKey(@Nonnull String key) {
        return resourceBundle.containsKey(key)
    }

    @Nonnull
    @Override
    Map<String, Object> asFlatMap() {
        return [
            'string.type': 'single',
            'strings.internal.type': 'multiple'
        ]
    }

    @Nonnull
    @Override
    ResourceBundle asResourceBundle() {
        return resourceBundle
    }

    @Override
    Object get(@Nonnull String key) {
        return resourceBundle.getObject(key)
    }
}

class DefaultObjectFactory extends AbstractObjectFactory<String> {
    private String singleKey = 'string'
    private String pluralKey = 'strings'

    DefaultObjectFactory(
        @Nonnull Configuration configuration,
        @Nonnull GriffonApplication application) {
        super(configuration, application)
    }

    void setSingleKey(String singleKey) {
        this.singleKey = singleKey
    }

    void setPluralKey(String pluralKey) {
        this.pluralKey = pluralKey
    }

    @Nonnull
    @Override
    protected String getSingleKey() {
        singleKey
    }

    @Nonnull
    @Override
    protected String getPluralKey() {
        pluralKey
    }

    @Nonnull
    @Override
    String create(@Nonnull String name) {
        narrowConfig(name).type
    }

    @Override
    void destroy(@Nonnull String name, @Nonnull String instance) {
        // empty
    }
}
