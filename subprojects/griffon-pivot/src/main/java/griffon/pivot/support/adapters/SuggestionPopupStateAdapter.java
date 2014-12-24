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
import org.apache.pivot.util.Vote;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SuggestionPopupStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SuggestionPopupStateListener {
    private CallableWithArgs<Vote> previewSuggestionPopupClose;
    private CallableWithArgs<Void> suggestionPopupCloseVetoed;
    private CallableWithArgs<Void> suggestionPopupClosed;

    public CallableWithArgs<Vote> getPreviewSuggestionPopupClose() {
        return this.previewSuggestionPopupClose;
    }

    public CallableWithArgs<Void> getSuggestionPopupCloseVetoed() {
        return this.suggestionPopupCloseVetoed;
    }

    public CallableWithArgs<Void> getSuggestionPopupClosed() {
        return this.suggestionPopupClosed;
    }


    public void setPreviewSuggestionPopupClose(CallableWithArgs<Vote> previewSuggestionPopupClose) {
        this.previewSuggestionPopupClose = previewSuggestionPopupClose;
    }

    public void setSuggestionPopupCloseVetoed(CallableWithArgs<Void> suggestionPopupCloseVetoed) {
        this.suggestionPopupCloseVetoed = suggestionPopupCloseVetoed;
    }

    public void setSuggestionPopupClosed(CallableWithArgs<Void> suggestionPopupClosed) {
        this.suggestionPopupClosed = suggestionPopupClosed;
    }


    public org.apache.pivot.util.Vote previewSuggestionPopupClose(org.apache.pivot.wtk.SuggestionPopup arg0, boolean arg1) {
        if (previewSuggestionPopupClose != null) {
            return previewSuggestionPopupClose.call(arg0, arg1);
        }
        return Vote.APPROVE;
    }

    public void suggestionPopupCloseVetoed(org.apache.pivot.wtk.SuggestionPopup arg0, org.apache.pivot.util.Vote arg1) {
        if (suggestionPopupCloseVetoed != null) {
            suggestionPopupCloseVetoed.call(arg0, arg1);
        }
    }

    public void suggestionPopupClosed(org.apache.pivot.wtk.SuggestionPopup arg0) {
        if (suggestionPopupClosed != null) {
            suggestionPopupClosed.call(arg0);
        }
    }

}
