package ${project_package};

import griffon.test.pivot.GriffonPivotRule;
import griffon.test.core.TestFor;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;

@TestFor(${project_class_name}.class)
public class ${project_class_name}Test {
    private ${project_class_name} ${artifact_type};

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule();

    @Test
    public void smokeTest() {
        fail("Not yet implemented!");
    }
}