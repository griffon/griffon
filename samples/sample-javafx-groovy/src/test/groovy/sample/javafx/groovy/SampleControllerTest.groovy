package sample.javafx.groovy

import griffon.core.artifact.ArtifactManager
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import javafx.embed.swing.JFXPanel
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS
import static org.hamcrest.Matchers.notNullValue

@TestFor(SampleController)
class SampleControllerTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
        // force initialization JavaFX Toolkit
        new JFXPanel()
    }

    @Inject
    private ArtifactManager artifactManager

    private SampleController controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Test
    void executeSayHelloActionWithNoInput() {
        SampleModel model = artifactManager.newInstance(SampleModel)

        controller.model = model
        controller.invokeAction('sayHello')

        await().atMost(2, SECONDS)
            .until({ model.output }, notNullValue())
        assert 'Howdy stranger!' == model.output
    }

    @Test
    void executeSayHelloActionWithInput() {
        final SampleModel model = artifactManager.newInstance(SampleModel)
        model.input = 'Griffon'

        controller.model = model
        controller.invokeAction('sayHello')

        await().atMost(2, SECONDS)
            .until({ model.output }, notNullValue())
        assert 'Hello Griffon' == model.output
    }
}
