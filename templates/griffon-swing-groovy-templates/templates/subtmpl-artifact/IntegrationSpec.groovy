package ${project_package}

import griffon.test.swing.GriffonFestRule
import org.fest.swing.fixture.FrameFixture
import org.junit.Rule
import spock.lang.Specification

class ${project_class_name}IntegrationSpec extends Specification {
    static {
        System.setProperty('griffon.swing.edt.violations.check', 'true')
        System.setProperty('griffon.swing.edt.hang.monitor', 'true')
    }

    @Rule
    public final GriffonFestRule fest = new GriffonFestRule()

    private FrameFixture window

    void "This is a smoke test" () {
        expect:
            false
    }
}