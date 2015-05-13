package ${project_package}

import griffon.javafx.test.GriffonTestFXClassRule
import org.junit.ClassRule
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.LabeledMatchers.hasText

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ${project_class_name}FunctionalTest {
    @ClassRule
    public static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule('mainWindow')

    @Test
    void clickButton() {
        // given:
        verifyThat('#clickLabel', hasText('0'))

        // when:
        testfx.clickOn('#clickActionTarget')

        // then:
        verifyThat('#clickLabel', hasText('1'))
    }
}
