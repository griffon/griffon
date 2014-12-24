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
public class ImageViewBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ImageViewBindingListener {
    private CallableWithArgs<Void> imageKeyChanged;
    private CallableWithArgs<Void> imageBindTypeChanged;
    private CallableWithArgs<Void> imageBindMappingChanged;

    public CallableWithArgs<Void> getImageKeyChanged() {
        return this.imageKeyChanged;
    }

    public CallableWithArgs<Void> getImageBindTypeChanged() {
        return this.imageBindTypeChanged;
    }

    public CallableWithArgs<Void> getImageBindMappingChanged() {
        return this.imageBindMappingChanged;
    }


    public void setImageKeyChanged(CallableWithArgs<Void> imageKeyChanged) {
        this.imageKeyChanged = imageKeyChanged;
    }

    public void setImageBindTypeChanged(CallableWithArgs<Void> imageBindTypeChanged) {
        this.imageBindTypeChanged = imageBindTypeChanged;
    }

    public void setImageBindMappingChanged(CallableWithArgs<Void> imageBindMappingChanged) {
        this.imageBindMappingChanged = imageBindMappingChanged;
    }


    public void imageKeyChanged(org.apache.pivot.wtk.ImageView arg0, java.lang.String arg1) {
        if (imageKeyChanged != null) {
            imageKeyChanged.call(arg0, arg1);
        }
    }

    public void imageBindTypeChanged(org.apache.pivot.wtk.ImageView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (imageBindTypeChanged != null) {
            imageBindTypeChanged.call(arg0, arg1);
        }
    }

    public void imageBindMappingChanged(org.apache.pivot.wtk.ImageView arg0, org.apache.pivot.wtk.ImageView.ImageBindMapping arg1) {
        if (imageBindMappingChanged != null) {
            imageBindMappingChanged.call(arg0, arg1);
        }
    }

}
