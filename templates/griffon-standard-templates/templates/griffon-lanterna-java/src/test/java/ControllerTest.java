package ${project_package};

import griffon.core.test.GriffonUnitRule;
import griffon.core.test.TestFor;
import griffon.core.threading.UIThreadManager;
import griffon.inject.BindTo;
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.fail;

@TestFor(${project_class_name}Controller.class)
public class ${project_class_name}ControllerTest {
    private ${project_class_name}Controller controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    public void testClickAction() {
        fail("Not yet implemented!");
    }

    @Singleton
    @BindTo(UIThreadManager.class)
    private DefaultUIThreadManager uiThreadManager;
}