package console

import griffon.core.artifact.ArtifactManager
import griffon.core.injection.Module
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.inject.DependsOn
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.inject.Inject

import static com.jayway.awaitility.Awaitility.await
import static com.jayway.awaitility.Awaitility.fieldIn
import static java.util.concurrent.TimeUnit.SECONDS
import static org.hamcrest.Matchers.notNullValue

@TestFor(ConsoleController)                                                   //<1>
class ConsoleControllerTest {
    private ConsoleController controller                                      //<2>

    @Inject
    private ArtifactManager artifactManager                                   //<3>

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()              //<4>

    @Test
    void testExecuteScriptAction() {
        // given:                                                             //<5>
        ConsoleModel model = artifactManager.newInstance(ConsoleModel.class)
        controller.model = model

        // when:                                                              //<6>
        String input = 'var = "Griffon"'
        model.scriptSource = input
        controller.invokeAction('executeScript')

        // then:                                                              //<7>
        await().atMost(2, SECONDS)
            .until(fieldIn(model)
            .ofType(Object)
            .andWithName('scriptResult'),
            notNullValue())
        assert input == model.scriptResult
    }

    private static class EchoEvaluator implements Evaluator {                 //<8>
        @Override
        Object evaluate(String input) {
            input
        }
    }

    @DependsOn('application')
    private static class TestModule extends AbstractModule {
        @Override
        protected void doConfigure() {
            bind(Evaluator)
                .to(EchoEvaluator)
                .asSingleton()
        }
    }

    @Nonnull
    private List<Module> moduleOverrides() {
        [new TestModule()]
    }
}
