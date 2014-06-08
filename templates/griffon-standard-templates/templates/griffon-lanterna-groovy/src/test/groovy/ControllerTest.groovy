package ${project_package}

import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.core.threading.UIThreadManager
import griffon.inject.BindTo
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

@TestFor(${project_capitalized_name}Controller)
class ${project_capitalized_name}ControllerTest {
    private ${project_capitalized_name}Controller controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Test
    void testClickAction() {
        fail('Not yet implemented!')
    }

    @javax.inject.Singleton
    @BindTo(UIThreadManager.class)
    private DefaultUIThreadManager uiThreadManager
}