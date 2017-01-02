/*
 * Copyright 2008-2017 the original author or authors.
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
public class SuggestionPopupItemAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SuggestionPopupItemListener {
    private CallableWithArgs<Void> itemsCleared;
    private CallableWithArgs<Void> itemsSorted;
    private CallableWithArgs<Void> itemInserted;
    private CallableWithArgs<Void> itemUpdated;
    private CallableWithArgs<Void> itemsRemoved;

    public CallableWithArgs<Void> getItemsCleared() {
        return this.itemsCleared;
    }

    public CallableWithArgs<Void> getItemsSorted() {
        return this.itemsSorted;
    }

    public CallableWithArgs<Void> getItemInserted() {
        return this.itemInserted;
    }

    public CallableWithArgs<Void> getItemUpdated() {
        return this.itemUpdated;
    }

    public CallableWithArgs<Void> getItemsRemoved() {
        return this.itemsRemoved;
    }


    public void setItemsCleared(CallableWithArgs<Void> itemsCleared) {
        this.itemsCleared = itemsCleared;
    }

    public void setItemsSorted(CallableWithArgs<Void> itemsSorted) {
        this.itemsSorted = itemsSorted;
    }

    public void setItemInserted(CallableWithArgs<Void> itemInserted) {
        this.itemInserted = itemInserted;
    }

    public void setItemUpdated(CallableWithArgs<Void> itemUpdated) {
        this.itemUpdated = itemUpdated;
    }

    public void setItemsRemoved(CallableWithArgs<Void> itemsRemoved) {
        this.itemsRemoved = itemsRemoved;
    }


    public void itemsCleared(org.apache.pivot.wtk.SuggestionPopup arg0) {
        if (itemsCleared != null) {
            itemsCleared.call(arg0);
        }
    }

    public void itemsSorted(org.apache.pivot.wtk.SuggestionPopup arg0) {
        if (itemsSorted != null) {
            itemsSorted.call(arg0);
        }
    }

    public void itemInserted(org.apache.pivot.wtk.SuggestionPopup arg0, int arg1) {
        if (itemInserted != null) {
            itemInserted.call(arg0, arg1);
        }
    }

    public void itemUpdated(org.apache.pivot.wtk.SuggestionPopup arg0, int arg1) {
        if (itemUpdated != null) {
            itemUpdated.call(arg0, arg1);
        }
    }

    public void itemsRemoved(org.apache.pivot.wtk.SuggestionPopup arg0, int arg1, int arg2) {
        if (itemsRemoved != null) {
            itemsRemoved.call(arg0, arg1, arg2);
        }
    }

}
