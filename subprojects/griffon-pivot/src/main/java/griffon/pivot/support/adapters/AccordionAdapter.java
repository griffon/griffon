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
public class AccordionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.AccordionListener {
    private CallableWithArgs<?> panelsRemoved;
    private CallableWithArgs<?> panelInserted;
    private CallableWithArgs<?> headerDataRendererChanged;

    public CallableWithArgs<?> getPanelsRemoved() {
        return this.panelsRemoved;
    }

    public CallableWithArgs<?> getPanelInserted() {
        return this.panelInserted;
    }

    public CallableWithArgs<?> getHeaderDataRendererChanged() {
        return this.headerDataRendererChanged;
    }


    public void setPanelsRemoved(CallableWithArgs<?> panelsRemoved) {
        this.panelsRemoved = panelsRemoved;
    }

    public void setPanelInserted(CallableWithArgs<?> panelInserted) {
        this.panelInserted = panelInserted;
    }

    public void setHeaderDataRendererChanged(CallableWithArgs<?> headerDataRendererChanged) {
        this.headerDataRendererChanged = headerDataRendererChanged;
    }


    public void panelsRemoved(org.apache.pivot.wtk.Accordion arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (panelsRemoved != null) {
            panelsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void panelInserted(org.apache.pivot.wtk.Accordion arg0, int arg1) {
        if (panelInserted != null) {
            panelInserted.call(arg0, arg1);
        }
    }

    public void headerDataRendererChanged(org.apache.pivot.wtk.Accordion arg0, org.apache.pivot.wtk.Button.DataRenderer arg1) {
        if (headerDataRendererChanged != null) {
            headerDataRendererChanged.call(arg0, arg1);
        }
    }

}
