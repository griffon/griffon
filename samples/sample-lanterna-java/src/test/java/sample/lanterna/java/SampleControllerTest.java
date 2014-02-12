package sample.lanterna.java;

import griffon.core.artifact.ArtifactManager;
import griffon.core.test.GriffonUnitRule;
import griffon.core.test.TestFor;
import griffon.core.threading.UIThreadManager;
import griffon.inject.BindTo;
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Awaitility.fieldIn;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

@TestFor(SampleController.class)
public class SampleControllerTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Inject
    private ArtifactManager artifactManager;

    private SampleController controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    public void executeSayHelloActionWithNoInput() {
        SampleModel model = artifactManager.newInstance(SampleModel.class);

        controller.setModel(model);
        controller.invokeAction("sayHello");

        await().atMost(2, SECONDS)
            .until(fieldIn(model)
                .ofType(String.class)
                .andWithName("output"),
                notNullValue());
        assertEquals("Howdy stranger!", model.getOutput());
    }

    @Test
    public void executeSayHelloActionWithInput() {
        SampleModel model = artifactManager.newInstance(SampleModel.class);
        model.setInput("Griffon");

        controller.setModel(model);
        controller.invokeAction("sayHello");

        await().atMost(2, SECONDS)
            .until(fieldIn(model)
                .ofType(String.class)
                .andWithName("output"),
                notNullValue());
        assertEquals("Hello Griffon", model.getOutput());
    }

    @Singleton
    @BindTo(UIThreadManager.class)
    private DefaultUIThreadManager uiThreadManager;
}
