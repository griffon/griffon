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

import griffon.core.artifact.ArtifactManager;
import griffon.core.event.EventHandler;
import griffon.test.core.GriffonUnitRule;
import griffon.test.core.TestFor;
import griffon.inject.BindTo;
import javafx.embed.swing.JFXPanel;
import lombok.Getter;
import org.example.api.Github;
import org.example.api.Repository;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestFor(ReactiveController.class)
public class ReactiveControllerTest {
    private static final String ORGANIZATION = "griffon";

    static {
        new JFXPanel();
    }

    private ReactiveController controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Inject private ArtifactManager artifactManager;

    @Test
    public void happyPath() {
        // given:
        final ReactiveModel model = artifactManager.newInstance(ReactiveModel.class);
        controller.setModel(model);
        Repository repository = Repository.builder()
            .description("griffon")
            .fullName("griffon/griffon")
            .htmlUrl("https://github.com/griffon/griffon")
            .build();

        // expectations
        when(github.repositories(ORGANIZATION)).thenReturn(Observable.just(repository));

        // expect:
        assertThat(model.getRepositories().size(), is(0));

        // when:
        model.setOrganization(ORGANIZATION);
        controller.load();
        await().until(() -> model.getState() == State.READY);

        // then:
        assertThat(model.getRepositories().size(), is(1));
        assertThat(model.getRepositories(), hasItem(repository));
        verify(github).repositories(ORGANIZATION);
    }

    @Test
    public void failurePath() {
        // given:
        final ReactiveModel model = artifactManager.newInstance(ReactiveModel.class);
        controller.setModel(model);
        RuntimeException exception = new RuntimeException("boom");
        when(github.repositories(ORGANIZATION)).thenReturn(Observable.error(exception));

        // when:
        model.setOrganization(ORGANIZATION);
        controller.load();
        await().timeout(2, SECONDS).until(model::getState, equalTo(State.READY));

        // then:
        Assert.assertThat(model.getRepositories(), hasSize(0));
        Assert.assertThat(eventHandler.getEvent().getThrowable(), equalTo(exception));
        verify(github, only()).repositories(ORGANIZATION);
    }

    @BindTo(Github.class)
    private final Github github = mock(Github.class);

    public static class ApplicationEventHandlerStub implements EventHandler {
        @Getter
        private ThrowableEvent event;

        public void onThrowableEvent(ThrowableEvent event) {
            this.event = event;
        }
    }

    @BindTo(EventHandler.class)
    private final ApplicationEventHandlerStub eventHandler = new ApplicationEventHandlerStub();
}