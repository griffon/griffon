/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class FileBrowserAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.FileBrowserListener {
    private CallableWithArgs<?> rootDirectoryChanged;
    private CallableWithArgs<?> selectedFileAdded;
    private CallableWithArgs<?> selectedFileRemoved;
    private CallableWithArgs<?> selectedFilesChanged;
    private CallableWithArgs<?> multiSelectChanged;
    private CallableWithArgs<?> disabledFileFilterChanged;

    public CallableWithArgs<?> getRootDirectoryChanged() {
        return this.rootDirectoryChanged;
    }

    public CallableWithArgs<?> getSelectedFileAdded() {
        return this.selectedFileAdded;
    }

    public CallableWithArgs<?> getSelectedFileRemoved() {
        return this.selectedFileRemoved;
    }

    public CallableWithArgs<?> getSelectedFilesChanged() {
        return this.selectedFilesChanged;
    }

    public CallableWithArgs<?> getMultiSelectChanged() {
        return this.multiSelectChanged;
    }

    public CallableWithArgs<?> getDisabledFileFilterChanged() {
        return this.disabledFileFilterChanged;
    }


    public void setRootDirectoryChanged(CallableWithArgs<?> rootDirectoryChanged) {
        this.rootDirectoryChanged = rootDirectoryChanged;
    }

    public void setSelectedFileAdded(CallableWithArgs<?> selectedFileAdded) {
        this.selectedFileAdded = selectedFileAdded;
    }

    public void setSelectedFileRemoved(CallableWithArgs<?> selectedFileRemoved) {
        this.selectedFileRemoved = selectedFileRemoved;
    }

    public void setSelectedFilesChanged(CallableWithArgs<?> selectedFilesChanged) {
        this.selectedFilesChanged = selectedFilesChanged;
    }

    public void setMultiSelectChanged(CallableWithArgs<?> multiSelectChanged) {
        this.multiSelectChanged = multiSelectChanged;
    }

    public void setDisabledFileFilterChanged(CallableWithArgs<?> disabledFileFilterChanged) {
        this.disabledFileFilterChanged = disabledFileFilterChanged;
    }


    public void rootDirectoryChanged(org.apache.pivot.wtk.FileBrowser arg0, java.io.File arg1) {
        if (rootDirectoryChanged != null) {
            rootDirectoryChanged.call(arg0, arg1);
        }
    }

    public void selectedFileAdded(org.apache.pivot.wtk.FileBrowser arg0, java.io.File arg1) {
        if (selectedFileAdded != null) {
            selectedFileAdded.call(arg0, arg1);
        }
    }

    public void selectedFileRemoved(org.apache.pivot.wtk.FileBrowser arg0, java.io.File arg1) {
        if (selectedFileRemoved != null) {
            selectedFileRemoved.call(arg0, arg1);
        }
    }

    public void selectedFilesChanged(org.apache.pivot.wtk.FileBrowser arg0, org.apache.pivot.collections.Sequence arg1) {
        if (selectedFilesChanged != null) {
            selectedFilesChanged.call(arg0, arg1);
        }
    }

    public void multiSelectChanged(org.apache.pivot.wtk.FileBrowser arg0) {
        if (multiSelectChanged != null) {
            multiSelectChanged.call(arg0);
        }
    }

    public void disabledFileFilterChanged(org.apache.pivot.wtk.FileBrowser arg0, org.apache.pivot.util.Filter arg1) {
        if (disabledFileFilterChanged != null) {
            disabledFileFilterChanged.call(arg0, arg1);
        }
    }

}
