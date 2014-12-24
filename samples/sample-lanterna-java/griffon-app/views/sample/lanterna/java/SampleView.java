/*
 * Copyright 2008-2015 the original author or authors.
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
package sample.lanterna.java;

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import griffon.core.artifact.GriffonView;
import griffon.lanterna.support.LanternaAction;
import griffon.lanterna.widgets.MutableButton;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class SampleView extends AbstractLanternaGriffonView {
    private SampleController controller;                                         //<1>
    private SampleModel model;                                                   //<1>

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
        getApplication().getWindowManager().attach("mainWindow", window);        //<2>
        Panel panel = new Panel(Panel.Orientation.VERTICAL);

        panel.addComponent(new Label(getApplication().getMessageSource().getMessage("name.label")));

        final TextBox input = new TextBox();
        panel.addComponent(input);

        LanternaAction sayHelloAction = toolkitActionFor(controller, "sayHello");
        final Runnable runnable = sayHelloAction.getRunnable();
        sayHelloAction.setRunnable(new Runnable() {                              //<3>
            @Override
            public void run() {
                model.setInput(input.getText());
                runnable.run();
            }
        });
        panel.addComponent(new MutableButton(sayHelloAction));                   //<4>

        final Label output = new Label();
        panel.addComponent(output);
        model.addPropertyChangeListener("output", new PropertyChangeListener() { //<3>
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                output.setText(String.valueOf(evt.getNewValue()));
            }
        });

        window.addComponent(panel);
    }
}
