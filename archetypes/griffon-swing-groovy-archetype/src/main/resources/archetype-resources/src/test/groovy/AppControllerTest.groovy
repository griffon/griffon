package \${groupId}

import griffon.core.artifact.ArtifactManager
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject

import static org.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS

@TestFor(AppController)
class AppControllerTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Inject
    private ArtifactManager artifactManager

    private AppController controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Test
    void executeClickAction() {
        // given:
        controller.model = artifactManager.newInstance(AppModel)

        // when:
        controller.invokeAction('click')
        await().atMost(2, SECONDS)

        // then:
        assert 1 == controller.model.clickCount
    }
}
