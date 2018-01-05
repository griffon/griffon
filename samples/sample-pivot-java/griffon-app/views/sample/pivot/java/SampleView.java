/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package sample.pivot.java;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.pivot.support.PivotAction;
import griffon.pivot.support.adapters.TextInputContentAdapter;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Orientation;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonView;

import javax.annotation.Nonnull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class SampleView extends AbstractPivotGriffonView {
    private SampleController controller;                                         //<1>
    private SampleModel model;                                                   //<1>

    @MVCMember
    public void setController(@Nonnull SampleController controller) {
        this.controller = controller;
    }

    @MVCMember
    public void setModel(@Nonnull SampleModel model) {
        this.model = model;
    }

    @Override
    public void initUI() {
        Window window = (Window) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        window.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        window.setMaximized(true);
        getApplication().getWindowManager().attach("mainWindow", window);        //<2>

        BoxPane vbox = new BoxPane(Orientation.VERTICAL);
        try {
            vbox.setStyles("{horizontalAlignment:'center', verticalAlignment:'center'}");
        } catch (SerializationException e) {
            // ignore
        }

        vbox.add(new Label(getApplication().getMessageSource().getMessage("name.label")));

        TextInput input = new TextInput();
        input.setName("inputField");
        input.getTextInputContentListeners().add(new TextInputContentAdapter() {  //<3>
            @Override
            public void textChanged(TextInput arg0) {
                model.setInput(arg0.getText());
            }
        });
        vbox.add(input);

        PivotAction sayHelloAction = toolkitActionFor(controller, "sayHello");
        final Button button = new PushButton(sayHelloAction.getName());
        button.setName("sayHelloButton");
        button.setAction(sayHelloAction);                                        //<4>
        vbox.add(button);

        final TextInput output = new TextInput();
        output.setName("outputField");
        output.setEditable(false);
        model.addPropertyChangeListener("output", new PropertyChangeListener() { //<3>
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                output.setText(String.valueOf(evt.getNewValue()));
            }
        });
        vbox.add(output);

        window.setContent(vbox);
    }
}
