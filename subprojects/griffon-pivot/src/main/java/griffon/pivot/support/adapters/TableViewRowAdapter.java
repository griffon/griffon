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
public class TableViewRowAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewRowListener {
    private CallableWithArgs<?> rowsCleared;
    private CallableWithArgs<?> rowsSorted;
    private CallableWithArgs<?> rowsRemoved;
    private CallableWithArgs<?> rowUpdated;
    private CallableWithArgs<?> rowInserted;

    public CallableWithArgs<?> getRowsCleared() {
        return this.rowsCleared;
    }

    public CallableWithArgs<?> getRowsSorted() {
        return this.rowsSorted;
    }

    public CallableWithArgs<?> getRowsRemoved() {
        return this.rowsRemoved;
    }

    public CallableWithArgs<?> getRowUpdated() {
        return this.rowUpdated;
    }

    public CallableWithArgs<?> getRowInserted() {
        return this.rowInserted;
    }


    public void setRowsCleared(CallableWithArgs<?> rowsCleared) {
        this.rowsCleared = rowsCleared;
    }

    public void setRowsSorted(CallableWithArgs<?> rowsSorted) {
        this.rowsSorted = rowsSorted;
    }

    public void setRowsRemoved(CallableWithArgs<?> rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }

    public void setRowUpdated(CallableWithArgs<?> rowUpdated) {
        this.rowUpdated = rowUpdated;
    }

    public void setRowInserted(CallableWithArgs<?> rowInserted) {
        this.rowInserted = rowInserted;
    }


    public void rowsCleared(org.apache.pivot.wtk.TableView arg0) {
        if (rowsCleared != null) {
            rowsCleared.call(arg0);
        }
    }

    public void rowsSorted(org.apache.pivot.wtk.TableView arg0) {
        if (rowsSorted != null) {
            rowsSorted.call(arg0);
        }
    }

    public void rowsRemoved(org.apache.pivot.wtk.TableView arg0, int arg1, int arg2) {
        if (rowsRemoved != null) {
            rowsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void rowUpdated(org.apache.pivot.wtk.TableView arg0, int arg1) {
        if (rowUpdated != null) {
            rowUpdated.call(arg0, arg1);
        }
    }

    public void rowInserted(org.apache.pivot.wtk.TableView arg0, int arg1) {
        if (rowInserted != null) {
            rowInserted.call(arg0, arg1);
        }
    }

}
