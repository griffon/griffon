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
package editor;

import java.beans.PropertyChangeListener;

import static griffon.util.GriffonClassUtils.setPropertyValue;

public class DocumentModel extends Document {
    private Document document;

    private final PropertyChangeListener proxyUpdater = (e) -> setPropertyValue(this, e.getPropertyName(), e.getNewValue());

    public DocumentModel() {
        addPropertyChangeListener("document", (e) -> {
            if (e.getOldValue() instanceof Document) {
                ((Document) e.getOldValue()).removePropertyChangeListener(proxyUpdater);
            }
            if (e.getNewValue() instanceof Document) {
                ((Document) e.getNewValue()).addPropertyChangeListener(proxyUpdater);
                ((Document) e.getNewValue()).copyTo(DocumentModel.this);
            }
        });
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        firePropertyChange("document", this.document, this.document = document);
    }
}
