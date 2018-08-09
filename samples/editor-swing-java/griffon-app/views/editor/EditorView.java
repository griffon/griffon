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
package editor;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonView;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;

@ServiceProviderFor(GriffonView.class)
public class EditorView extends AbstractSwingGriffonView {
    @MVCMember @Nonnull
    private EditorModel model;
    @MVCMember @Nonnull
    private ContainerView parentView;
    @MVCMember @Nonnull
    private String tabName;

    private JScrollPane tab;
    private JTextArea editor;

    @Override
    public void initUI() {
        editor = new JTextArea();
        editor.setEditable(true);
        editor.setEnabled(true);

        model.getDocument().addPropertyChangeListener("contents", (e) -> editor.setText((String) e.getNewValue()));

        tab = new JScrollPane();
        tab.putClientProperty("mvcIdentifier", getMvcGroup().getMvcId());
        tab.setViewportView(editor);

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDirtyStatus();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDirtyStatus();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDirtyStatus();
            }

            private void updateDirtyStatus() {
                model.getDocument().setDirty(!Objects.equals(editor.getText(), model.getDocument().getContents()));
            }
        });

        JTabbedPane tabGroup = parentView.getTabGroup();
        tabGroup.addTab(tabName, tab);
        tabGroup.setSelectedIndex(tabGroup.getTabCount() - 1);
    }

    public JTextArea getEditor() {
        return editor;
    }

    @Override
    public void mvcGroupDestroy() {
        parentView.getTabGroup().remove(tab);
    }
}