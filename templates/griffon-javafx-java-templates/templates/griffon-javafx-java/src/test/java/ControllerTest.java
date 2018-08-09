package ${project_package};

import griffon.core.artifact.ArtifactManager;
import griffon.test.core.GriffonUnitRule;
import griffon.test.core.TestFor;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.greatherThan;

@TestFor(${project_class_name}Controller.class)
public class ${project_class_name}ControllerTest {
    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel();
    }

    @Inject
    private ArtifactManager artifactManager;

    private ${project_class_name}Controller controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    public void executeClickAction() {
        // given:
        ${project_class_name}Model model = artifactManager.newInstance(${project_class_name}Model.class);
        controller.setModel(model);

        // when:
        controller.invokeAction("click");
        await().atMost(2, SECONDS).until(() -> model.getClickCount(), greaterThan(0));

        // then:
        assertEquals("1", model.getClickCount());
    }
}
