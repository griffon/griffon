package org.codehaus.griffon.runtime.theme;

import com.acme.NotThemeAwareModel;
import com.acme.ThemeAwareModel;
import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.injection.Module;
import griffon.core.resources.ResourceResolver;
import griffon.core.test.GriffonUnitRule;
import griffon.plugins.theme.ThemeManager;
import griffon.util.CollectionUtils;
import griffon.util.TypeUtils;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static griffon.util.AnnotationUtils.named;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThemeTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Inject
    private ThemeManager themeManager;

    @Inject
    private GriffonApplication application;

    @Before
    @After
    public void setup() {
        application.setLocale(Locale.ENGLISH);
    }

    @Test
    public void themesAreConfiguredProperly() {
        // given:
        Set<String> themes = themeManager.getThemes();

        // then:
        assertTrue(TypeUtils.equals(themes, CollectionUtils.<String>newSet("red", "blue")));
        assertFalse(themes.contains("white"));
    }

    @Test
    public void themeChangeWithThemeAwareTarget() throws Exception {
        // given:
        ThemeAwareModel model = application.getArtifactManager().newInstance(ThemeAwareModel.class);

        // expect:
        assertEquals("red", themeManager.getCurrentTheme());
        assertEquals("RED", model.getString());
        assertEquals("nonThemed", model.getNonThemed());

        // when:
        themeManager.setCurrentTheme("blue");

        // then:
        assertEquals("blue", themeManager.getCurrentTheme());
        assertEquals("BLUE", model.getString());
        assertEquals("nonThemed", model.getNonThemed());

        // when:
        themeManager.setCurrentTheme("white");

        // then:
        assertEquals("blue", themeManager.getCurrentTheme());
        assertEquals("BLUE", model.getString());
        assertEquals("nonThemed", model.getNonThemed());
    }

    @Test
    public void themeChangeWhenLocaleChanges() throws Exception {
        // given:
        ThemeAwareModel model = application.getArtifactManager().newInstance(ThemeAwareModel.class);

        // expect:
        assertEquals("RED", model.getString());

        // when:
        themeManager.setCurrentTheme("blue");

        // then:
        assertEquals("BLUE", model.getString());

        // when:
        application.setLocaleAsString("es");

        // then:
        assertEquals("AZUL", model.getString());
    }

    @Test
    public void destroyedInstanceNoLongerReceivesThemeUpdates() throws Exception {
        // given:
        ThemeAwareModel model = application.getArtifactManager().newInstance(ThemeAwareModel.class);

        // expect:
        assertEquals("RED", model.getString());

        // when:
        themeManager.setCurrentTheme("blue");

        // then:
        assertEquals("BLUE", model.getString());

        // when:
        application.getEventRouter().publishEvent(ApplicationEvent.DESTROY_INSTANCE.getName(), asList(model.getClass(), model));
        themeManager.setCurrentTheme("red");

        // then:
        assertEquals("BLUE", model.getString());
    }

    @Test
    public void themeChangeWithNotThemeAwareTarget() throws Exception {
        // given:
        NotThemeAwareModel model = application.getArtifactManager().newInstance(NotThemeAwareModel.class);

        // expect:
        assertEquals("RED", model.getString());

        // when:
        themeManager.setCurrentTheme("blue");

        // then:
        assertEquals("RED", model.getString());

        // when:
        themeManager.setCurrentTheme("white");

        // then:
        assertEquals("RED", model.getString());
    }

    @Nonnull
    private List<Module> moduleOverrides() {
        return CollectionUtils.<Module>newList(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(ResourceResolver.class)
                    .withClassifier(named("red"))
                    .toProvider(new ResourceResolverProvider("red"))
                    .asSingleton();

                bind(ResourceResolver.class)
                    .withClassifier(named("blue"))
                    .toProvider(new ResourceResolverProvider("blue"))
                    .asSingleton();
            }
        });
    }
}
