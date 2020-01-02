/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

import java.io.File;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class FileBrowserSheetAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.FileBrowserSheetListener {
    private CallableWithArgs<Void> modeChanged;
    private CallableWithArgs<Void> rootDirectoryChanged;
    private CallableWithArgs<Void> selectedFilesChanged;
    private CallableWithArgs<Void> disabledFileFilterChanged;

    public CallableWithArgs<Void> getModeChanged() {
        return this.modeChanged;
    }

    public CallableWithArgs<Void> getRootDirectoryChanged() {
        return this.rootDirectoryChanged;
    }

    public CallableWithArgs<Void> getSelectedFilesChanged() {
        return this.selectedFilesChanged;
    }

    public CallableWithArgs<Void> getDisabledFileFilterChanged() {
        return this.disabledFileFilterChanged;
    }


    public void setModeChanged(CallableWithArgs<Void> modeChanged) {
        this.modeChanged = modeChanged;
    }

    public void setRootDirectoryChanged(CallableWithArgs<Void> rootDirectoryChanged) {
        this.rootDirectoryChanged = rootDirectoryChanged;
    }

    public void setSelectedFilesChanged(CallableWithArgs<Void> selectedFilesChanged) {
        this.selectedFilesChanged = selectedFilesChanged;
    }

    public void setDisabledFileFilterChanged(CallableWithArgs<Void> disabledFileFilterChanged) {
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

    public void selectedFilesChanged(org.apache.pivot.wtk.FileBrowserSheet arg0, org.apache.pivot.collections.Sequence<File> arg1) {
        if (selectedFilesChanged != null) {
            selectedFilesChanged.call(arg0, arg1);
        }
    }

    public void disabledFileFilterChanged(org.apache.pivot.wtk.FileBrowserSheet arg0, org.apache.pivot.util.Filter<File> arg1) {
        if (disabledFileFilterChanged != null) {
            disabledFileFilterChanged.call(arg0, arg1);
        }
    }

}
