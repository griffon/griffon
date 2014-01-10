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
public class FileBrowserSheetAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.FileBrowserSheetListener {
    private CallableWithArgs<?> modeChanged;
    private CallableWithArgs<?> rootDirectoryChanged;
    private CallableWithArgs<?> selectedFilesChanged;
    private CallableWithArgs<?> disabledFileFilterChanged;

    public CallableWithArgs<?> getModeChanged() {
        return this.modeChanged;
    }

    public CallableWithArgs<?> getRootDirectoryChanged() {
        return this.rootDirectoryChanged;
    }

    public CallableWithArgs<?> getSelectedFilesChanged() {
        return this.selectedFilesChanged;
    }

    public CallableWithArgs<?> getDisabledFileFilterChanged() {
        return this.disabledFileFilterChanged;
    }


    public void setModeChanged(CallableWithArgs<?> modeChanged) {
        this.modeChanged = modeChanged;
    }

    public void setRootDirectoryChanged(CallableWithArgs<?> rootDirectoryChanged) {
        this.rootDirectoryChanged = rootDirectoryChanged;
    }

    public void setSelectedFilesChanged(CallableWithArgs<?> selectedFilesChanged) {
        this.selectedFilesChanged = selectedFilesChanged;
    }

    public void setDisabledFileFilterChanged(CallableWithArgs<?> disabledFileFilterChanged) {
        this.disabledFileFilterChanged = disabledFileFilterChanged;
    }


    public void modeChanged(org.apache.pivot.wtk.FileBrowserSheet arg0, org.apache.pivot.wtk.FileBrowserSheet.Mode arg1) {
        if (modeChanged != null) {
            modeChanged.call(arg0, arg1);
        }
    }

    public void rootDirectoryChanged(org.apache.pivot.wtk.FileBrowserSheet arg0, java.io.File arg1) {
        if (rootDirectoryChanged != null) {
            rootDirectoryChanged.call(arg0, arg1);
        }
    }

    public void selectedFilesChanged(org.apache.pivot.wtk.FileBrowserSheet arg0, org.apache.pivot.collections.Sequence arg1) {
        if (selectedFilesChanged != null) {
            selectedFilesChanged.call(arg0, arg1);
        }
    }

    public void disabledFileFilterChanged(org.apache.pivot.wtk.FileBrowserSheet arg0, org.apache.pivot.util.Filter arg1) {
        if (disabledFileFilterChanged != null) {
            disabledFileFilterChanged.call(arg0, arg1);
        }
    }

}
