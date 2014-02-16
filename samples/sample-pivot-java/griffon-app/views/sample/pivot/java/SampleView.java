/*
 * Copyright 2008-2014 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import griffon.core.controller.Action;
import griffon.metadata.ArtifactProviderFor;
import griffon.pivot.support.PivotAction;
import griffon.pivot.support.adapters.TextInputContentAdapter;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.*;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import sample.pivot.java.SampleController;
import sample.pivot.java.SampleModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class SampleView extends AbstractGriffonView {
    private SampleController controller;                                         //<1>
    private SampleModel model;                                                   //<1>

    @Inject
    public SampleView(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setController(SampleController controller) {
        this.controller = controller;
    }

    public void setModel(SampleModel model) {
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

        Action sayHelloAction = getApplication().getActionManager()
            .actionFor(controller, "sayHello");
        final Button button = new PushButton(sayHelloAction.getName());
        button.setName("sayHelloButton");
        button.setAction((PivotAction) sayHelloAction.getToolkitAction());       //<4>
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
