package ${groupId};

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

@TestFor(AppController.class)
public class AppControllerTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Inject
    private ArtifactManager artifactManager;

    private AppController controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    public void executeClickAction() {
        // given:
        AppModel model = artifactManager.newInstance(AppModel.class);
        controller.setModel(model);

        // when:
        controller.invokeAction("click");
        await().atMost(2, SECONDS).until(() -> model.getClickCount(), greaterThan(0));

        // then:
        assertEquals(1, model.getClickCount());
    }
}
