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
        application.getEventRouter().publish(ApplicationEvent.DESTROY_INSTANCE.getName(), [model.class, model])
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
