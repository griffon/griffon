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

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.example.api.Github;
import org.example.api.Repository;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import griffon.annotations.core.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.example.State.READY;
import static org.example.State.RUNNING;

@ArtifactProviderFor(GriffonController.class)
public class ReactiveController extends AbstractGriffonController {
    private ReactiveModel model;

    @MVCMember
    public void setModel(@Nonnull ReactiveModel model) {
        this.model = model;
    }

    @Inject private Github github;

    public void load() {
        Observable<Repository> observable = github.repositories(model.getOrganization());
        if (model.getLimit() > 0) {
            observable = observable.take(model.getLimit());
        }

        Subscription subscription = observable
            .timeout(10, TimeUnit.SECONDS)
            .doOnSubscribe(() -> model.setState(RUNNING))
            .doOnTerminate(() -> model.setState(READY))
            .doOnError(this::handleThrowable)
            .subscribeOn(Schedulers.io())
            .subscribe(model.getRepositories()::add);
        model.setSubscription(subscription);
    }

    public void cancel() {
        if (model.getSubscription() != null) {
            model.getSubscription().unsubscribe();
            model.setState(READY);
        }
    }

    private void handleThrowable(@Nonnull Throwable throwable) {
        getApplication().getEventRouter().publishEvent(new ThrowableEvent(this, throwable));
    }
}