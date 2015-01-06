/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.prefs

import com.acme.NotPreferencesAwareModel
import com.acme.PreferencesAwareModel
import com.acme.ValueHolder
import griffon.core.ApplicationEvent
import griffon.core.GriffonApplication
import griffon.core.injection.Module
import griffon.core.test.GriffonUnitRule
import griffon.plugins.preferences.PreferencesManager
import griffon.plugins.preferences.PreferencesPersistor
import griffon.plugins.preferences.persistors.AbstractMapBasedPreferencesPersistor
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.inject.Inject

class PreferencesTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private PreferencesManager preferencesManager

    @Inject
    private GriffonApplication application

    @Inject
    private PreferencesPersistor preferencesPersistor

    @Before
    public void setup() {
        ((InMemoryPreferencesPersistor) preferencesPersistor).map.putAll(
            [
                com: [
                    acme: [
                        PreferencesAwareModel: [
                            value: 'value1'
                        ],
                        NotPreferencesAwareModel: [
                            value: 'value1'
                        ],
                    ]
                ]
            ]
        )
        preferencesPersistor.read(preferencesManager)
    }

    @Test
    void preferencesAreInjectedAsExpected() {
        // given:
        ValueHolder model1 = application.artifactManager.newInstance(PreferencesAwareModel)
        ValueHolder model2 = application.artifactManager.newInstance(NotPreferencesAwareModel)

        // expect:
        assert model1.value == 'value1'
        assert model2.value == 'value1'

        // when:
        preferencesManager.preferences.node(PreferencesAwareModel)['value'] = 'value2'
        preferencesManager.preferences.node(NotPreferencesAwareModel)['value'] = 'value2'

        // expect:
        assert model1.value == 'value2'
        assert model2.value == 'value1'
    }

    @Test
    void writeFromModelToPreferences() {
        // given:
        ValueHolder model = application.artifactManager.newInstance(PreferencesAwareModel)

        // expect:
        assert model.value == 'value1'

        // when:
        model.value = 'value2'
        preferencesManager.save(model)
        preferencesPersistor.write(preferencesManager)

        // expect:
        assert 'value2' == preferencesManager.preferences.node(PreferencesAwareModel)['value']
        assert 'value2' == preferencesPersistor.map.com.acme.PreferencesAwareModel.value
    }

    @Test
    void removeNodeAndWriteBack() {
        // given:
        ValueHolder model = application.artifactManager.newInstance(PreferencesAwareModel)

        // expect:
        assert model.value == 'value1'

        // when:
        preferencesManager.preferences.removeNode(PreferencesAwareModel)
        model.value = 'value2'

        //then:
        assert !preferencesManager.preferences.node(PreferencesAwareModel)['value']
    }

    @Test
    void destroyedInstanceNoLongerReceivesPreferencesUpdate() {
        // given:
        ValueHolder model = application.artifactManager.newInstance(PreferencesAwareModel)

        // expect:
        assert model.value == 'value1'

        // when:
        application.getEventRouter().publishEvent(ApplicationEvent.DESTROY_INSTANCE.getName(), [model.class, model])
        preferencesManager.preferences.node(PreferencesAwareModel)['value'] = 'value2'

        // expect:
        assert model.value == 'value1'
    }

    private static class InMemoryPreferencesPersistor extends AbstractMapBasedPreferencesPersistor {
        final Map<String, Object> map = [:]

        @Inject
        InMemoryPreferencesPersistor(@Nonnull GriffonApplication application) {
            super(application)
        }

        @Nonnull
        @Override
        protected InputStream inputStream() throws IOException {
            return System.in
        }

        @Nonnull
        @Override
        protected OutputStream outputStream() throws IOException {
            return System.out
        }

        @Nonnull
        @Override
        protected Map<String, Object> read(@Nonnull InputStream inputStream) throws IOException {
            return map
        }

        @Override
        protected void write(@Nonnull Map<String, Object> map, @Nonnull OutputStream outputStream) throws IOException {
            this.map.clear()
            this.map.putAll(map)
        }
    }

    @Nonnull
    private List<Module> moduleOverrides() {
        [
            new AbstractModule() {
                @Override
                protected void doConfigure() {
                    bind(PreferencesPersistor)
                        .to(InMemoryPreferencesPersistor)
                        .asSingleton()
                }
            }
        ]
    }
}
