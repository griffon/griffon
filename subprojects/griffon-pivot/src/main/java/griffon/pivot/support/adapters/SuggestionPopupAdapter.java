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
public class SuggestionPopupAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SuggestionPopupListener {
    private CallableWithArgs<Void> listSizeChanged;
    private CallableWithArgs<Void> suggestionDataChanged;
    private CallableWithArgs<Void> suggestionRendererChanged;

    public CallableWithArgs<Void> getListSizeChanged() {
        return this.listSizeChanged;
    }

    public CallableWithArgs<Void> getSuggestionDataChanged() {
        return this.suggestionDataChanged;
    }

    public CallableWithArgs<Void> getSuggestionRendererChanged() {
        return this.suggestionRendererChanged;
    }


    public void setListSizeChanged(CallableWithArgs<Void> listSizeChanged) {
        this.listSizeChanged = listSizeChanged;
    }

    public void setSuggestionDataChanged(CallableWithArgs<Void> suggestionDataChanged) {
        this.suggestionDataChanged = suggestionDataChanged;
    }

    public void setSuggestionRendererChanged(CallableWithArgs<Void> suggestionRendererChanged) {
        this.suggestionRendererChanged = suggestionRendererChanged;
    }


    public void listSizeChanged(org.apache.pivot.wtk.SuggestionPopup arg0, int arg1) {
        if (listSizeChanged != null) {
            listSizeChanged.call(arg0, arg1);
        }
    }

    public void suggestionDataChanged(org.apache.pivot.wtk.SuggestionPopup arg0, org.apache.pivot.collections.List<?> arg1) {
        if (suggestionDataChanged != null) {
            suggestionDataChanged.call(arg0, arg1);
        }
    }

    public void suggestionRendererChanged(org.apache.pivot.wtk.SuggestionPopup arg0, org.apache.pivot.wtk.ListView.ItemRenderer arg1) {
        if (suggestionRendererChanged != null) {
            suggestionRendererChanged.call(arg0, arg1);
        }
    }

}
