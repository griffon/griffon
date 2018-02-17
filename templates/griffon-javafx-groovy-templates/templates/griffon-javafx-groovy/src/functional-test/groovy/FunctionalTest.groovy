package ${project_package}

import griffon.javafx.test.FunctionalJavaFXRunner
import griffon.javafx.test.GriffonTestFXClassRule
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.LabeledMatchers.hasText

@RunWith(FunctionalJavaFXRunner)
class ${project_class_name}FunctionalTest {
    @ClassRule
    public static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule('mainWindow')

    @Test
    void _01_clickButton() {
        // given:
        verifyThat('#clickLabel', hasText('0'))

        // when:
        testfx.clickOn('#click')

        // then:
        verifyThat('#clickLabel', hasText('1'))
    }
}
