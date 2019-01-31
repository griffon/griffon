/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.GridPane;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GridPaneAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.GridPaneListener {
    private CallableWithArgs<Void> columnCountChanged;
    private CallableWithArgs<Void> cellInserted;
    private CallableWithArgs<Void> cellsRemoved;
    private CallableWithArgs<Void> cellUpdated;
    private CallableWithArgs<Void> rowsRemoved;
    private CallableWithArgs<Void> rowInserted;

    public CallableWithArgs<Void> getColumnCountChanged() {
        return this.columnCountChanged;
    }

    public CallableWithArgs<Void> getCellInserted() {
        return this.cellInserted;
    }

    public CallableWithArgs<Void> getCellsRemoved() {
        return this.cellsRemoved;
    }

    public CallableWithArgs<Void> getCellUpdated() {
        return this.cellUpdated;
    }

    public CallableWithArgs<Void> getRowsRemoved() {
        return this.rowsRemoved;
    }

    public CallableWithArgs<Void> getRowInserted() {
        return this.rowInserted;
    }


    public void setColumnCountChanged(CallableWithArgs<Void> columnCountChanged) {
        this.columnCountChanged = columnCountChanged;
    }

    public void setCellInserted(CallableWithArgs<Void> cellInserted) {
        this.cellInserted = cellInserted;
    }

    public void setCellsRemoved(CallableWithArgs<Void> cellsRemoved) {
        this.cellsRemoved = cellsRemoved;
    }

    public void setCellUpdated(CallableWithArgs<Void> cellUpdated) {
        this.cellUpdated = cellUpdated;
    }

    public void setRowsRemoved(CallableWithArgs<Void> rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }

    public void setRowInserted(CallableWithArgs<Void> rowInserted) {
        this.rowInserted = rowInserted;
    }


    public void columnCountChanged(org.apache.pivot.wtk.GridPane arg0, int arg1) {
        if (columnCountChanged != null) {
            columnCountChanged.call(arg0, arg1);
        }
    }

    public void cellInserted(org.apache.pivot.wtk.GridPane.Row arg0, int arg1) {
        if (cellInserted != null) {
            cellInserted.call(arg0, arg1);
        }
    }

    public void cellsRemoved(org.apache.pivot.wtk.GridPane.Row arg0, int arg1, org.apache.pivot.collections.Sequence<Component> arg2) {
        if (cellsRemoved != null) {
            cellsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void cellUpdated(org.apache.pivot.wtk.GridPane.Row arg0, int arg1, org.apache.pivot.wtk.Component arg2) {
        if (cellUpdated != null) {
            cellUpdated.call(arg0, arg1, arg2);
        }
    }

    public void rowsRemoved(org.apache.pivot.wtk.GridPane arg0, int arg1, org.apache.pivot.collections.Sequence<GridPane.Row> arg2) {
        if (rowsRemoved != null) {
            rowsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void rowInserted(org.apache.pivot.wtk.GridPane arg0, int arg1) {
        if (rowInserted != null) {
            rowInserted.call(arg0, arg1);
        }
    }

}
