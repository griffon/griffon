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
package editor;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@ArtifactProviderFor(GriffonModel.class)
public class ContainerModel extends AbstractGriffonModel implements ChangeListener {
    private static final String MVC_IDENTIFIER = "mvcIdentifier";
    private final DocumentModel documentModel = new DocumentModel();
    private String mvcIdentifier;

    public ContainerModel() {
        addPropertyChangeListener(MVC_IDENTIFIER, (e) -> {
            Document document = null;
            if (e.getNewValue() != null) {
                EditorModel model = getApplication().getMvcGroupManager().getModel(mvcIdentifier, EditorModel.class);
                document = model.getDocument();
            } else {
                document = new Document();
            }
            documentModel.setDocument(document);
        });
    }

    public String getMvcIdentifier() {
        return mvcIdentifier;
    }

    public void setMvcIdentifier(String mvcIdentifier) {
        firePropertyChange(MVC_IDENTIFIER, this.mvcIdentifier, this.mvcIdentifier = mvcIdentifier);
    }

    public DocumentModel getDocumentModel() {
        return documentModel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex < 0) {
            setMvcIdentifier(null);
        } else {
            JComponent tab = (JComponent) tabbedPane.getComponentAt(selectedIndex);
            setMvcIdentifier((String) tab.getClientProperty(MVC_IDENTIFIER));
        }
    }
}