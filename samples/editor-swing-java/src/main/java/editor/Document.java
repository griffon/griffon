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
package editor;

import org.codehaus.griffon.runtime.core.AbstractObservable;

import java.io.File;

public class Document extends AbstractObservable {
    private String title;
    private String contents;
    private boolean dirty;
    private File file;

    public Document() {
    }

    public Document(File file, String title) {
        this.file = file;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        firePropertyChange("title", this.title, this.title = title);
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        firePropertyChange("contents", this.contents, this.contents = contents);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        firePropertyChange("dirty", this.dirty, this.dirty = dirty);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        firePropertyChange("file", this.file, this.file = file);
    }

    public void copyTo(Document doc) {
        doc.title = title;
        doc.contents = contents;
        doc.dirty = dirty;
        doc.file = file;
    }
}
