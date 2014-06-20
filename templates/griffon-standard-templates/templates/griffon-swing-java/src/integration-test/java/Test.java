package ${project_package};

import griffon.core.test.GriffonFestRule;
import org.fest.swing.fixture.FrameFixture;
import org.junit.Rule;
import org.junit.Test;

public class ${project_class_name}Test {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("griffon.swing.edt.violations.check", "true");
        System.setProperty("griffon.swing.edt.hang.monitor", "true");
    }

    @Rule
    public final GriffonFestRule fest = new GriffonFestRule();

    private FrameFixture window;

    @Test
    public void clickButton() {
        // given:
        window.label("clickLabel").requireText("0");

        // when:
        window.button("clickButton").click();

        // then:
        window.label("clickLabel").requireText("1");
    }
}
