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
public class ImageViewAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ImageViewListener {
    private CallableWithArgs<?> imageChanged;
    private CallableWithArgs<?> asynchronousChanged;

    public CallableWithArgs<?> getImageChanged() {
        return this.imageChanged;
    }

    public CallableWithArgs<?> getAsynchronousChanged() {
        return this.asynchronousChanged;
    }


    public void setImageChanged(CallableWithArgs<?> imageChanged) {
        this.imageChanged = imageChanged;
    }

    public void setAsynchronousChanged(CallableWithArgs<?> asynchronousChanged) {
        this.asynchronousChanged = asynchronousChanged;
    }


    public void imageChanged(org.apache.pivot.wtk.ImageView arg0, org.apache.pivot.wtk.media.Image arg1) {
        if (imageChanged != null) {
            imageChanged.call(arg0, arg1);
        }
    }

    public void asynchronousChanged(org.apache.pivot.wtk.ImageView arg0) {
        if (asynchronousChanged != null) {
            asynchronousChanged.call(arg0);
        }
    }

}
