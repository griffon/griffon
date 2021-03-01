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

import griffon.core.artifact.GriffonModel;
import griffon.annotations.inject.MVCMember;
import org.kordamp.jipsy.annotations.ServiceProviderFor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import griffon.annotations.core.Nonnull;
import java.util.Map;

@ServiceProviderFor(GriffonModel.class)
public class SampleModel extends AbstractGriffonModel {
    private StringProperty output;
    private SampleView view;

    @Nonnull
    public final StringProperty outputProperty() {
        if (output == null) {
            output = new SimpleStringProperty(this, "output");
        }
        return output;
    }

    public void setOutput(String output) {
        outputProperty().set(output);
    }

    public String getOutput() {
        return output == null ? null : outputProperty().get();
    }

    @MVCMember
    public void setView(@Nonnull SampleView view) {
        this.view = view;
    }

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        outputProperty().addListener((observable, oldValue, newValue) -> {
            runInsideUIAsync(() -> view.getOutput().setText(newValue));        //<1>
        });
    }
}