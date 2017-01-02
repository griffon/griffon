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
import org.apache.pivot.wtk.Component;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class AccordionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.AccordionListener {
    private CallableWithArgs<Void> panelsRemoved;
    private CallableWithArgs<Void> panelInserted;
    private CallableWithArgs<Void> headerDataRendererChanged;

    public CallableWithArgs<Void> getPanelsRemoved() {
        return this.panelsRemoved;
    }

    public CallableWithArgs<Void> getPanelInserted() {
        return this.panelInserted;
    }

    public CallableWithArgs<Void> getHeaderDataRendererChanged() {
        return this.headerDataRendererChanged;
    }


    public void setPanelsRemoved(CallableWithArgs<Void> panelsRemoved) {
        this.panelsRemoved = panelsRemoved;
    }

    public void setPanelInserted(CallableWithArgs<Void> panelInserted) {
        this.panelInserted = panelInserted;
    }

    public void setHeaderDataRendererChanged(CallableWithArgs<Void> headerDataRendererChanged) {
        this.headerDataRendererChanged = headerDataRendererChanged;
    }


    public void panelsRemoved(org.apache.pivot.wtk.Accordion arg0, int arg1, org.apache.pivot.collections.Sequence<Component> arg2) {
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
