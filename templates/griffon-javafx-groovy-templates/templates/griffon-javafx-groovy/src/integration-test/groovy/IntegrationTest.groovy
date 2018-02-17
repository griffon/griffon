package ${project_package}

import griffon.javafx.test.GriffonTestFXRule
import org.junit.Rule
import org.junit.Test

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.LabeledMatchers.hasText

class ${project_class_name}IntegrationTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule('mainWindow')

    @Test
    void clickButton() {
        // given:
        verifyThat('#clickLabel', hasText('0'))

        // when:
        testfx.clickOn('#click')

        // then:
        verifyThat('#clickLabel', hasText('1'))
    }
}
