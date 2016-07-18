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

import griffon.core.injection.Module;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.test.GriffonUnitRule;
import griffon.core.test.TestModuleOverrides;
import griffon.inject.BindTo;
import org.codehaus.griffon.runtime.core.injection.AbstractTestingModule;
import org.example.api.Github;
import org.example.api.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReactiveIntegrationTest {
    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel();
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Inject private MVCGroupManager mvcGroupManager;

    private MVCGroup group;

    @Before
    public void setup() {
        group = mvcGroupManager.createMVCGroup("reactive");
    }

    @After
    public void cleanup() {
        if (group != null) {
            group.destroy();
        }
    }

    @Test
    public void happyPath() {
        // given:
        ReactiveModel model = mvcGroupManager.findModel("reactive", ReactiveModel.class);
        ReactiveController controller = mvcGroupManager.findController("reactive", ReactiveController.class);

        Repository repository = Repository.builder()
            .description("griffon")
            .fullName("griffon/griffon")
            .htmlUrl("https://github.com/griffon/griffon")
            .build();

        // expectations
        when(github.repositories("griffon")).thenReturn(Observable.just(repository));

        // expect:
        assertThat(model.getRepositories().size(), is(0));

        // when:
        model.setOrganization("griffon");
        controller.load();
        await().until(() -> model.getState() == State.READY);

        // then:
        assertThat(model.getRepositories().size(), is(1));
        assertThat(model.getRepositories(), hasItem(repository));
        verify(github).repositories("griffon");
    }

    @BindTo(Github.class)
    private final Github github = mock(Github.class);

    @Nonnull
    @TestModuleOverrides
    public List<Module> lazyInitialization() {
        return asList(new AbstractTestingModule() {
            @Override
            protected void doConfigure() {
                bind(ReactiveView.class)
                    .toProvider(() -> mock(ReactiveView.class));
            }
        });
    }
}
