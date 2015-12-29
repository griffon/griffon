/*
 * Copyright 2008-2016 the original author or authors.
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
package sample.swing.java;

import griffon.core.artifact.GriffonView;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

import static java.util.Arrays.asList;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

@ArtifactProviderFor(GriffonView.class)
public class SampleView extends AbstractSwingGriffonView {
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
        JFrame window = (JFrame) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        window.setName("mainWindow");
        window.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        window.setSize(320, 120);
        window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        window.setIconImage(getImage("/griffon-icon-48x48.png"));
        window.setIconImages(asList(
            getImage("/griffon-icon-48x48.png"),
            getImage("/griffon-icon-32x32.png"),
            getImage("/griffon-icon-16x16.png")
        ));
        getApplication().getWindowManager().attach("mainWindow", window);        //<2>

        window.getContentPane().setLayout(new GridLayout(4, 1));
        window.getContentPane().add(
            new JLabel(getApplication().getMessageSource().getMessage("name.label"))
        );
        final JTextField nameField = new JTextField();
        nameField.setName("inputField");
        nameField.getDocument().addDocumentListener(new DocumentListener() {     //<3>
            @Override
            public void insertUpdate(DocumentEvent e) {
                model.setInput(nameField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                model.setInput(nameField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                model.setInput(nameField.getText());
            }
        });
        window.getContentPane().add(nameField);

        Action action = toolkitActionFor(controller, "sayHello");                //<4>
        final JButton button = new JButton(action);
        button.setName("sayHelloButton");
        window.getContentPane().add(button);

        final JLabel outputLabel = new JLabel();
        outputLabel.setName("outputLabel");
        model.addPropertyChangeListener("output", new PropertyChangeListener() { // <3>
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                outputLabel.setText(String.valueOf(evt.getNewValue()));
            }
        });
        window.getContentPane().add(outputLabel);
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(SampleView.class.getResource(path));
    }
}
