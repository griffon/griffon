package ${package};

import griffon.core.artifact.ArtifactManager;
import griffon.pivot.test.GriffonPivotRule;
import griffon.core.test.TestFor;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@TestFor(_APPController.class)
public class _APPControllerTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Inject
    private ArtifactManager artifactManager;

    private _APPController controller;

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule();

    @Test
    public void executeClickAction() {
        // given:
        _APPModel model = artifactManager.newInstance(_APPModel.class);
        controller.setModel(model);

        // when:
        controller.invokeAction("click");
        await().atMost(2, SECONDS);

        // then:
        assertEquals(1, model.getClickCount());
    }
}
