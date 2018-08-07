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
package sample.lanterna3.java;

import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.lanterna3.support.LanternaAction;
import griffon.lanterna3.widgets.MutableButton;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.lanterna3.artifact.AbstractLanternaGriffonView;

import javax.annotation.Nonnull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class SampleView extends AbstractLanternaGriffonView {
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
        getApplication().getWindowManager().attach("mainWindow", window);        //<2>
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

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

        final Label output = new Label("");
        panel.addComponent(output);
        model.addPropertyChangeListener("output", new PropertyChangeListener() { //<3>
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                output.setText(String.valueOf(evt.getNewValue()));
            }
        });

        window.setComponent(panel);
    }
}
