package sample.pivot.groovy

import griffon.core.artifact.ArtifactManager
import griffon.core.test.TestFor
import griffon.pivot.test.GriffonPivotRule
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
    }

    @Inject
    private ArtifactManager artifactManager

    private SampleController controller

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule()

    @Test
    void executeSayHelloActionWithNoInput() {
        TestBuilder builder = new TestBuilder()

        controller.builder = builder
        controller.invokeAction('sayHello')

        await().atMost(2, SECONDS)
            .until({ builder.output.text }, notNullValue())
        assert 'Howdy stranger!' == builder.output.text
    }

    @Test
    void executeSayHelloActionWithInput() {
        TestBuilder builder = new TestBuilder()
        builder.input.text = 'Griffon'

        controller.builder = builder
        controller.invokeAction('sayHello')

        await().atMost(2, SECONDS)
            .until({ builder.output.text }, notNullValue())
        assert 'Hello Griffon' == builder.output.text
    }
}

class TestBuilder extends FactoryBuilderSupport {
    TestBuilder() {
        this.getVariables().putAll(
            input: [:],
            output: [:]
        )
    }
}
