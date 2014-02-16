package sample.pivot.java;

import com.google.inject.Inject;
import griffon.core.mvc.MVCGroupManager;
import griffon.pivot.test.GriffonPivotFuncRule;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.junit.Rule;
import org.junit.Test;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Awaitility.fieldIn;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class SampleTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("griffon.swing.edt.violations.check", "true");
        System.setProperty("griffon.swing.edt.hang.monitor", "true");
    }

    @Rule
    public final GriffonPivotFuncRule pivot = new GriffonPivotFuncRule();

    @Inject
    private MVCGroupManager mvcGroupManager;

    @Test
    public void typeNameAndClickButton() {
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                // given:
                pivot.find("inputField", TextInput.class).setText("Griffon");

                // when:
                pivot.find("sayHelloButton", PushButton.class).press();
            }
        });

        SampleModel model = (SampleModel) mvcGroupManager.getModels().get("sample");
        await().atMost(5, SECONDS)
            .until(fieldIn(model)
                .ofType(String.class)
                .andWithName("output"),
                notNullValue());

        // then:
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                assertEquals("Hello Griffon", pivot.find("outputField", TextInput.class).getText());
            }
        });
    }

    @Test
    public void doNotTypeNameAndClickButton() {
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                // given:
                pivot.find("inputField", TextInput.class).setText("");

                // when:
                pivot.find("sayHelloButton", PushButton.class).press();
            }
        });

        SampleModel model = (SampleModel) mvcGroupManager.getModels().get("sample");
        await().atMost(5, SECONDS)
            .until(fieldIn(model)
                .ofType(String.class)
                .andWithName("output"),
                notNullValue());

        // then:
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                assertEquals("Howdy stranger!", pivot.find("outputField", TextInput.class).getText());
            }
        });
    }
}
