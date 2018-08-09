/*
 * Copyright 2016-2018 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.event.EventHandler;
import griffon.core.injection.Module;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupManager;
import griffon.test.core.TestModuleOverrides;
import griffon.inject.BindTo;
import griffon.test.javafx.GriffonTestFXRule;
import org.codehaus.griffon.runtime.core.injection.AbstractTestingModule;
import org.junit.Rule;
import org.junit.Test;

import griffon.annotations.core.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SampleViewTest {
static {
System.setProperty("griffon.full.stacktrace", "true");
System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
}

    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule("mainWindow");

    @Inject private MVCGroupManager mvcGroupManager;

    @Test
    public void verify_view_setup() throws Exception {
        // given:
        // model is a real reference
        SampleModel model = mvcGroupManager.findModel("sample", SampleModel.class);
        // controller is a spy
        SampleController controller = mvcGroupManager.findController("sample", SampleController.class);

        // expectations:
        String input = "hello";
        final StringCapture capture = new StringCapture();
        // capture action method name if invoked
        doAnswer(invocation -> capture.setValue(invocation.getMethod().getName())).when(controller).sayHello();

        // when:
        testfx.clickOn("#input").write(input);
        testfx.clickOn("#sayHelloActionTarget");
        // wait for controller action to be invoked outside the UI thread
        await().timeout(2, SECONDS).until(() -> capture.getValue(), notNullValue());

        // then:
        assertThat(model.getInput(), equalTo(input));
        assertThat(capture.getValue(), equalTo("sayHello"));
    }

    @Nonnull
    @TestModuleOverrides
    private List<Module> mockedBindings() {
        return asList(new AbstractTestingModule() {
            @Override
            protected void doConfigure() {
                bind(SampleController.class)
                    .toProvider(() -> {
                        // use a spy instead of a mock
                        SampleController controller = spy(SampleController.class);
                        // return SampleController instead of Mockito subclass
                        when(controller.getTypeClass()).thenReturn(SampleController.class);
                        return controller;
                    })
                    .asSingleton();
            }
        });
    }

    @BindTo(EventHandler.class)
    public static class MVCGroupEventHandler implements EventHandler {
        @Inject private GriffonApplication application;

        public void onInitializeMVCGroup(MVCGroupConfiguration configuration, MVCGroup group) {
            // initialize members as they were not injected by the spy provider
            application.getInjector().injectMembers(group.getController());
            // initialize actions on spy before View is initialized
            application.getActionManager().createActions(group.getController());
        }
    }

    private static class StringCapture {
        private String value;

        public String getValue() {
            return value;
        }

        public StringCapture setValue(String value) {
            this.value = value;
            return this;
        }
    }
}