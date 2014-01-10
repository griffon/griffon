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
public class TableViewSortAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewSortListener {
    private CallableWithArgs<?> sortChanged;
    private CallableWithArgs<?> sortAdded;
    private CallableWithArgs<?> sortUpdated;
    private CallableWithArgs<?> sortRemoved;

    public CallableWithArgs<?> getSortChanged() {
        return this.sortChanged;
    }

    public CallableWithArgs<?> getSortAdded() {
        return this.sortAdded;
    }

    public CallableWithArgs<?> getSortUpdated() {
        return this.sortUpdated;
    }

    public CallableWithArgs<?> getSortRemoved() {
        return this.sortRemoved;
    }


    public void setSortChanged(CallableWithArgs<?> sortChanged) {
        this.sortChanged = sortChanged;
    }

    public void setSortAdded(CallableWithArgs<?> sortAdded) {
        this.sortAdded = sortAdded;
    }

    public void setSortUpdated(CallableWithArgs<?> sortUpdated) {
        this.sortUpdated = sortUpdated;
    }

    public void setSortRemoved(CallableWithArgs<?> sortRemoved) {
        this.sortRemoved = sortRemoved;
    }


    public void sortChanged(org.apache.pivot.wtk.TableView arg0) {
        if (sortChanged != null) {
            sortChanged.call(arg0);
        }
    }

    public void sortAdded(org.apache.pivot.wtk.TableView arg0, java.lang.String arg1) {
        if (sortAdded != null) {
            sortAdded.call(arg0, arg1);
        }
    }

    public void sortUpdated(org.apache.pivot.wtk.TableView arg0, java.lang.String arg1, org.apache.pivot.wtk.SortDirection arg2) {
        if (sortUpdated != null) {
            sortUpdated.call(arg0, arg1, arg2);
        }
    }

    public void sortRemoved(org.apache.pivot.wtk.TableView arg0, java.lang.String arg1, org.apache.pivot.wtk.SortDirection arg2) {
        if (sortRemoved != null) {
            sortRemoved.call(arg0, arg1, arg2);
        }
    }

}
