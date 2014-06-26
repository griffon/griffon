package ${project_package};

import griffon.pivot.test.GriffonPivotFuncRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ${project_class_name}IntegrationTest {
    @Rule
    public final GriffonPivotFuncRule pivot = new GriffonPivotFuncRule();

    @Test
    public void smokeTest() {
        fail("Not yet implemented!");
    }
}
