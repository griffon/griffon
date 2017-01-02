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
public class ViewportAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ViewportListener {
    private CallableWithArgs<Void> scrollTopChanged;
    private CallableWithArgs<Void> scrollLeftChanged;
    private CallableWithArgs<Void> viewChanged;

    public CallableWithArgs<Void> getScrollTopChanged() {
        return this.scrollTopChanged;
    }

    public CallableWithArgs<Void> getScrollLeftChanged() {
        return this.scrollLeftChanged;
    }

    public CallableWithArgs<Void> getViewChanged() {
        return this.viewChanged;
    }


    public void setScrollTopChanged(CallableWithArgs<Void> scrollTopChanged) {
        this.scrollTopChanged = scrollTopChanged;
    }

    public void setScrollLeftChanged(CallableWithArgs<Void> scrollLeftChanged) {
        this.scrollLeftChanged = scrollLeftChanged;
    }

    public void setViewChanged(CallableWithArgs<Void> viewChanged) {
        this.viewChanged = viewChanged;
    }


    public void scrollTopChanged(org.apache.pivot.wtk.Viewport arg0, int arg1) {
        if (scrollTopChanged != null) {
            scrollTopChanged.call(arg0, arg1);
        }
    }

    public void scrollLeftChanged(org.apache.pivot.wtk.Viewport arg0, int arg1) {
        if (scrollLeftChanged != null) {
            scrollLeftChanged.call(arg0, arg1);
        }
    }

    public void viewChanged(org.apache.pivot.wtk.Viewport arg0, org.apache.pivot.wtk.Component arg1) {
        if (viewChanged != null) {
            viewChanged.call(arg0, arg1);
        }
    }

}
