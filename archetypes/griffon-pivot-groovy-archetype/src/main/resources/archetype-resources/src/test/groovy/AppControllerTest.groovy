package \${groupId}

import griffon.core.artifact.ArtifactManager
import griffon.pivot.test.GriffonPivotRule
import griffon.core.test.TestFor
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject

import static org.awaitility.Awaitility.await

@TestFor(AppController)
class AppControllerTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Inject
    private ArtifactManager artifactManager

    private AppController controller

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule()

    @Test
    void executeClickAction() {
        // given:
        controller.model = artifactManager.newInstance(AppModel)

        // when:
        controller.invokeAction('click')
        await().until { controller.model.clickCount != 0 }

        // then:
        assert 1 == controller.model.clickCount
    }
}
