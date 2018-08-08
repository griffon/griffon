package ${project_package}

import griffon.test.core.GriffonFestRule
import org.fest.swing.fixture.FrameFixture
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

class ${project_class_name}IntegrationTest {
    static {
        System.setProperty('griffon.swing.edt.violations.check', 'true')
        System.setProperty('griffon.swing.edt.hang.monitor', 'true')
    }

    @Rule
    public final GriffonFestRule fest = new GriffonFestRule()

    private FrameFixture window

    @Test
    void smokeTest() {
        fail('Not implemented yet!')
    }
}
