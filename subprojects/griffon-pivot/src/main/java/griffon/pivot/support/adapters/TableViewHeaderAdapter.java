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
package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TableViewHeaderAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewHeaderListener {
    private CallableWithArgs<Void> tableViewChanged;
    private CallableWithArgs<Void> sortModeChanged;

    public CallableWithArgs<Void> getTableViewChanged() {
        return this.tableViewChanged;
    }

    public CallableWithArgs<Void> getSortModeChanged() {
        return this.sortModeChanged;
    }


    public void setTableViewChanged(CallableWithArgs<Void> tableViewChanged) {
        this.tableViewChanged = tableViewChanged;
    }

    public void setSortModeChanged(CallableWithArgs<Void> sortModeChanged) {
        this.sortModeChanged = sortModeChanged;
    }


    public void tableViewChanged(org.apache.pivot.wtk.TableViewHeader arg0, org.apache.pivot.wtk.TableView arg1) {
        if (tableViewChanged != null) {
            tableViewChanged.call(arg0, arg1);
        }
    }

    public void sortModeChanged(org.apache.pivot.wtk.TableViewHeader arg0, org.apache.pivot.wtk.TableViewHeader.SortMode arg1) {
        if (sortModeChanged != null) {
            sortModeChanged.call(arg0, arg1);
        }
    }

}
