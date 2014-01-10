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
public class ScrollPaneAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ScrollPaneListener {
    private CallableWithArgs<?> horizontalScrollBarPolicyChanged;
    private CallableWithArgs<?> rowHeaderChanged;
    private CallableWithArgs<?> verticalScrollBarPolicyChanged;
    private CallableWithArgs<?> columnHeaderChanged;
    private CallableWithArgs<?> cornerChanged;

    public CallableWithArgs<?> getHorizontalScrollBarPolicyChanged() {
        return this.horizontalScrollBarPolicyChanged;
    }

    public CallableWithArgs<?> getRowHeaderChanged() {
        return this.rowHeaderChanged;
    }

    public CallableWithArgs<?> getVerticalScrollBarPolicyChanged() {
        return this.verticalScrollBarPolicyChanged;
    }

    public CallableWithArgs<?> getColumnHeaderChanged() {
        return this.columnHeaderChanged;
    }

    public CallableWithArgs<?> getCornerChanged() {
        return this.cornerChanged;
    }


    public void setHorizontalScrollBarPolicyChanged(CallableWithArgs<?> horizontalScrollBarPolicyChanged) {
        this.horizontalScrollBarPolicyChanged = horizontalScrollBarPolicyChanged;
    }

    public void setRowHeaderChanged(CallableWithArgs<?> rowHeaderChanged) {
        this.rowHeaderChanged = rowHeaderChanged;
    }

    public void setVerticalScrollBarPolicyChanged(CallableWithArgs<?> verticalScrollBarPolicyChanged) {
        this.verticalScrollBarPolicyChanged = verticalScrollBarPolicyChanged;
    }

    public void setColumnHeaderChanged(CallableWithArgs<?> columnHeaderChanged) {
        this.columnHeaderChanged = columnHeaderChanged;
    }

    public void setCornerChanged(CallableWithArgs<?> cornerChanged) {
        this.cornerChanged = cornerChanged;
    }


    public void horizontalScrollBarPolicyChanged(org.apache.pivot.wtk.ScrollPane arg0, org.apache.pivot.wtk.ScrollPane.ScrollBarPolicy arg1) {
        if (horizontalScrollBarPolicyChanged != null) {
            horizontalScrollBarPolicyChanged.call(arg0, arg1);
        }
    }

    public void rowHeaderChanged(org.apache.pivot.wtk.ScrollPane arg0, org.apache.pivot.wtk.Component arg1) {
        if (rowHeaderChanged != null) {
            rowHeaderChanged.call(arg0, arg1);
        }
    }

    public void verticalScrollBarPolicyChanged(org.apache.pivot.wtk.ScrollPane arg0, org.apache.pivot.wtk.ScrollPane.ScrollBarPolicy arg1) {
        if (verticalScrollBarPolicyChanged != null) {
            verticalScrollBarPolicyChanged.call(arg0, arg1);
        }
    }

    public void columnHeaderChanged(org.apache.pivot.wtk.ScrollPane arg0, org.apache.pivot.wtk.Component arg1) {
        if (columnHeaderChanged != null) {
            columnHeaderChanged.call(arg0, arg1);
        }
    }

    public void cornerChanged(org.apache.pivot.wtk.ScrollPane arg0, org.apache.pivot.wtk.Component arg1) {
        if (cornerChanged != null) {
            cornerChanged.call(arg0, arg1);
        }
    }

}
