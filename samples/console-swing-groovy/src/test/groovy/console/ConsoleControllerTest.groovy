/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package console

import griffon.core.artifact.ArtifactManager
import griffon.core.injection.Module
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.inject.DependsOn
import org.codehaus.griffon.runtime.core.injection.AbstractTestingModule
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.inject.Inject

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static org.awaitility.Awaitility.fieldIn
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
    private static class TestModule extends AbstractTestingModule {
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
