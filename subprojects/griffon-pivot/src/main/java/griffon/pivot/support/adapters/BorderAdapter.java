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
public class BorderAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.BorderListener {
    private CallableWithArgs<Void> titleChanged;
    private CallableWithArgs<Void> contentChanged;

    public CallableWithArgs<Void> getTitleChanged() {
        return this.titleChanged;
    }

    public CallableWithArgs<Void> getContentChanged() {
        return this.contentChanged;
    }


    public void setTitleChanged(CallableWithArgs<Void> titleChanged) {
        this.titleChanged = titleChanged;
    }

    public void setContentChanged(CallableWithArgs<Void> contentChanged) {
        this.contentChanged = contentChanged;
    }


    public void titleChanged(org.apache.pivot.wtk.Border arg0, java.lang.String arg1) {
        if (titleChanged != null) {
            titleChanged.call(arg0, arg1);
        }
    }

    public void contentChanged(org.apache.pivot.wtk.Border arg0, org.apache.pivot.wtk.Component arg1) {
        if (contentChanged != null) {
            contentChanged.call(arg0, arg1);
        }
    }

}
