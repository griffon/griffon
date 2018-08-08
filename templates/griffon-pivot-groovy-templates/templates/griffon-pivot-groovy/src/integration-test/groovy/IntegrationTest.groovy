package ${project_package}

import griffon.test.pivot.GriffonPivotFuncRule
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

class ${project_class_name}IntegrationTest {
    @Rule
    public final GriffonPivotFuncRule fest = new GriffonPivotFuncRule()

    @Test
    void smokeTest() {
        fail("Not yet implemented!")
    }
}
