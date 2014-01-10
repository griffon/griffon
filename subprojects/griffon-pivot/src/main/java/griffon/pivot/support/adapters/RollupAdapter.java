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
public class RollupAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.RollupListener {
    private CallableWithArgs<?> contentChanged;
    private CallableWithArgs<?> collapsibleChanged;
    private CallableWithArgs<?> headingChanged;

    public CallableWithArgs<?> getContentChanged() {
        return this.contentChanged;
    }

    public CallableWithArgs<?> getCollapsibleChanged() {
        return this.collapsibleChanged;
    }

    public CallableWithArgs<?> getHeadingChanged() {
        return this.headingChanged;
    }


    public void setContentChanged(CallableWithArgs<?> contentChanged) {
        this.contentChanged = contentChanged;
    }

    public void setCollapsibleChanged(CallableWithArgs<?> collapsibleChanged) {
        this.collapsibleChanged = collapsibleChanged;
    }

    public void setHeadingChanged(CallableWithArgs<?> headingChanged) {
        this.headingChanged = headingChanged;
    }


    public void contentChanged(org.apache.pivot.wtk.Rollup arg0, org.apache.pivot.wtk.Component arg1) {
        if (contentChanged != null) {
            contentChanged.call(arg0, arg1);
        }
    }

    public void collapsibleChanged(org.apache.pivot.wtk.Rollup arg0) {
        if (collapsibleChanged != null) {
            collapsibleChanged.call(arg0);
        }
    }

    public void headingChanged(org.apache.pivot.wtk.Rollup arg0, org.apache.pivot.wtk.Component arg1) {
        if (headingChanged != null) {
            headingChanged.call(arg0, arg1);
        }
    }

}
