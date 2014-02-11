package sample.swing.java;

import griffon.core.test.GriffonFestRule;
import org.fest.swing.fixture.FrameFixture;
import org.junit.Rule;
import org.junit.Test;

public class SampleTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("griffon.swing.edt.violations.check", "true");
        System.setProperty("griffon.swing.edt.hang.monitor", "true");
    }

    @Rule
    public final GriffonFestRule fest = new GriffonFestRule();

    private FrameFixture window;

    @Test
    public void typeNameAndClickButton() {
        // given:
        window.textBox("inputField").enterText("Griffon");

        // when:
        window.button("sayHelloButton").click();

        // then:
        window.label("outputLabel").requireText("Hello Griffon");
    }

    @Test
    public void doNotTypeNameAndClickButton() {
        // given:
        window.textBox("inputField").enterText("");

        // when:
        window.button("sayHelloButton").click();

        // then:
        window.label("outputLabel").requireText("Howdy stranger!");
    }
}
