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
import org.apache.pivot.util.Vote;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TabPaneAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TabPaneListener {
    private CallableWithArgs<?> tabInserted;
    private CallableWithArgs<Vote> previewRemoveTabs;
    private CallableWithArgs<?> removeTabsVetoed;
    private CallableWithArgs<?> tabsRemoved;
    private CallableWithArgs<?> tabDataRendererChanged;
    private CallableWithArgs<?> closeableChanged;
    private CallableWithArgs<?> collapsibleChanged;
    private CallableWithArgs<?> cornerChanged;

    public CallableWithArgs<?> getTabInserted() {
        return this.tabInserted;
    }

    public CallableWithArgs<Vote> getPreviewRemoveTabs() {
        return this.previewRemoveTabs;
    }

    public CallableWithArgs<?> getRemoveTabsVetoed() {
        return this.removeTabsVetoed;
    }

    public CallableWithArgs<?> getTabsRemoved() {
        return this.tabsRemoved;
    }

    public CallableWithArgs<?> getTabDataRendererChanged() {
        return this.tabDataRendererChanged;
    }

    public CallableWithArgs<?> getCloseableChanged() {
        return this.closeableChanged;
    }

    public CallableWithArgs<?> getCollapsibleChanged() {
        return this.collapsibleChanged;
    }

    public CallableWithArgs<?> getCornerChanged() {
        return this.cornerChanged;
    }


    public void setTabInserted(CallableWithArgs<?> tabInserted) {
        this.tabInserted = tabInserted;
    }

    public void setPreviewRemoveTabs(CallableWithArgs<Vote> previewRemoveTabs) {
        this.previewRemoveTabs = previewRemoveTabs;
    }

    public void setRemoveTabsVetoed(CallableWithArgs<?> removeTabsVetoed) {
        this.removeTabsVetoed = removeTabsVetoed;
    }

    public void setTabsRemoved(CallableWithArgs<?> tabsRemoved) {
        this.tabsRemoved = tabsRemoved;
    }

    public void setTabDataRendererChanged(CallableWithArgs<?> tabDataRendererChanged) {
        this.tabDataRendererChanged = tabDataRendererChanged;
    }

    public void setCloseableChanged(CallableWithArgs<?> closeableChanged) {
        this.closeableChanged = closeableChanged;
    }

    public void setCollapsibleChanged(CallableWithArgs<?> collapsibleChanged) {
        this.collapsibleChanged = collapsibleChanged;
    }

    public void setCornerChanged(CallableWithArgs<?> cornerChanged) {
        this.cornerChanged = cornerChanged;
    }


    public void tabInserted(org.apache.pivot.wtk.TabPane arg0, int arg1) {
        if (tabInserted != null) {
            tabInserted.call(arg0, arg1);
        }
    }

    public org.apache.pivot.util.Vote previewRemoveTabs(org.apache.pivot.wtk.TabPane arg0, int arg1, int arg2) {
        if (previewRemoveTabs != null) {
            return previewRemoveTabs.call(arg0, arg1, arg2);
        }
        return Vote.APPROVE;
    }

    public void removeTabsVetoed(org.apache.pivot.wtk.TabPane arg0, org.apache.pivot.util.Vote arg1) {
        if (removeTabsVetoed != null) {
            removeTabsVetoed.call(arg0, arg1);
        }
    }

    public void tabsRemoved(org.apache.pivot.wtk.TabPane arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (tabsRemoved != null) {
            tabsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void tabDataRendererChanged(org.apache.pivot.wtk.TabPane arg0, org.apache.pivot.wtk.Button.DataRenderer arg1) {
        if (tabDataRendererChanged != null) {
            tabDataRendererChanged.call(arg0, arg1);
        }
    }

    public void closeableChanged(org.apache.pivot.wtk.TabPane arg0) {
        if (closeableChanged != null) {
            closeableChanged.call(arg0);
        }
    }

    public void collapsibleChanged(org.apache.pivot.wtk.TabPane arg0) {
        if (collapsibleChanged != null) {
            collapsibleChanged.call(arg0);
        }
    }

    public void cornerChanged(org.apache.pivot.wtk.TabPane arg0, org.apache.pivot.wtk.Component arg1) {
        if (cornerChanged != null) {
            cornerChanged.call(arg0, arg1);
        }
    }

}
