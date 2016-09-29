/*
 * Copyright 2016 the original author or authors.
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
package org.example;

import griffon.core.artifact.ArtifactManager;
import griffon.core.injection.Module;
import griffon.core.test.GriffonUnitRule;
import griffon.core.test.TestFor;
import javafx.scene.control.TextField;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.codehaus.griffon.runtime.core.injection.AbstractTestingModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
@TestFor(SampleController.class)
public class SampleControllerTest {
    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel();
    }

    private SampleController controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Inject private ArtifactManager artifactManager;
    @Inject private SampleService sampleService;
    @Inject private SampleView view;

    @Test
    @Parameters({",Howdy stranger!",
        "Test, Hello Test"})
    public void sayHelloAction(String input, String output) {
        // given:
        SampleModel model = artifactManager.newInstance(SampleModel.class);
        controller.setModel(model);
        controller.setView(view);

        // expect:
        assertThat(model.getOutput(), nullValue());

        // expectations
        when(view.getInput()).thenReturn(new TextField());
        when(sampleService.sayHello(input)).thenReturn(output);

        // when:
        view.getInput().setText(input);
        controller.invokeAction("sayHello");

        // then:
        await().until(() -> model.getOutput(), notNullValue());
        assertThat(model.getOutput(), equalTo(output));
        verify(sampleService, only()).sayHello(input);
    }

    @Nonnull
    private List<Module> moduleOverrides() {
        return asList(new AbstractTestingModule() {
            @Override
            protected void doConfigure() {
                bind(SampleService.class)
                    .toProvider(() -> mock(SampleService.class))
                    .asSingleton();
                bind(SampleView.class)
                    .toProvider(() -> mock(SampleView.class));
            }
        });
    }
}