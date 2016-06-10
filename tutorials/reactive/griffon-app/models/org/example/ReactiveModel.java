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

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;
import org.example.api.Repository;
import rx.Subscription;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;
import static org.example.State.READY;

@ArtifactProviderFor(GriffonModel.class)
public class ReactiveModel extends AbstractGriffonModel {
    private final ObservableList<Repository> repositories = observableArrayList();
    private StringProperty organization;
    private ObjectProperty<Subscription> subscription;
    private IntegerProperty limit;
    private ObjectProperty<State> state;

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case RUNNING:
                    repositories.clear();
                    break;
                case READY:
                    setSubscription(null);
            }
        });
    }

    @Nonnull
    public ObservableList<Repository> getRepositories() {
        return repositories;
    }

    @Nonnull
    public StringProperty organizationProperty() {
        if (organization == null) {
            organization = new SimpleStringProperty(this, "organization", "griffon");
        }
        return organization;
    }

    @Nonnull
    public String getOrganization() {
        return organizationProperty().get();
    }

    public void setOrganization(@Nonnull String organization) {
        organizationProperty().set(organization);
    }

    @Nullable
    public Subscription getSubscription() {
        return subscriptionProperty().get();
    }

    @Nonnull
    public ObjectProperty<Subscription> subscriptionProperty() {
        if (subscription == null) {
            subscription = new SimpleObjectProperty<>(this, "subscription");
        }
        return subscription;
    }

    public void setSubscription(@Nullable Subscription subscription) {
        subscriptionProperty().set(subscription);
    }

    public int getLimit() {
        return limitProperty().get();
    }

    @Nonnull
    public IntegerProperty limitProperty() {
        if (limit == null) {
            limit = new SimpleIntegerProperty(this, "limit", 10);
        }
        return limit;
    }

    public void setLimit(int limit) {
        limitProperty().set(limit);
    }

    @Nullable
    public State getState() {
        return stateProperty().get();
    }

    @Nonnull
    public ObjectProperty<State> stateProperty() {
        if (state == null) {
            state = new SimpleObjectProperty<>(this, "state", READY);
        }
        return state;
    }

    public void setState(@Nullable State state) {
        stateProperty().set(state);
    }
}