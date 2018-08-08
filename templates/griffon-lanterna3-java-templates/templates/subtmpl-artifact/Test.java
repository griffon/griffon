package ${project_package};

import griffon.test.core.GriffonUnitRule;
import griffon.test.core.TestFor;
import griffon.core.threading.UIThreadManager;
import griffon.inject.BindTo;
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.fail;

@TestFor(${project_class_name}.class)
public class ${project_class_name}Test {
    private ${project_class_name} ${artifact_type};

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    public void smokeTest() {
        fail("Not yet implemented!");
    }

    @Singleton
    @BindTo(UIThreadManager.class)
    private DefaultUIThreadManager uiThreadManager;
}