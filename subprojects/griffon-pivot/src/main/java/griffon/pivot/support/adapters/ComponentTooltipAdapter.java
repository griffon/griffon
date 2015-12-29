/*
 * Copyright 2008-2016 the original author or authors.
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
public class ComponentTooltipAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentTooltipListener {
    private CallableWithArgs<Void> tooltipTriggered;

    public CallableWithArgs<Void> getTooltipTriggered() {
        return this.tooltipTriggered;
    }


    public void setTooltipTriggered(CallableWithArgs<Void> tooltipTriggered) {
        this.tooltipTriggered = tooltipTriggered;
    }


    public void tooltipTriggered(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        if (tooltipTriggered != null) {
            tooltipTriggered.call(arg0, arg1, arg2);
        }
    }

}
