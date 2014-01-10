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
public class FormAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.FormListener {
    private CallableWithArgs<?> sectionInserted;
    private CallableWithArgs<?> sectionsRemoved;
    private CallableWithArgs<?> sectionHeadingChanged;
    private CallableWithArgs<?> fieldInserted;
    private CallableWithArgs<?> fieldsRemoved;

    public CallableWithArgs<?> getSectionInserted() {
        return this.sectionInserted;
    }

    public CallableWithArgs<?> getSectionsRemoved() {
        return this.sectionsRemoved;
    }

    public CallableWithArgs<?> getSectionHeadingChanged() {
        return this.sectionHeadingChanged;
    }

    public CallableWithArgs<?> getFieldInserted() {
        return this.fieldInserted;
    }

    public CallableWithArgs<?> getFieldsRemoved() {
        return this.fieldsRemoved;
    }


    public void setSectionInserted(CallableWithArgs<?> sectionInserted) {
        this.sectionInserted = sectionInserted;
    }

    public void setSectionsRemoved(CallableWithArgs<?> sectionsRemoved) {
        this.sectionsRemoved = sectionsRemoved;
    }

    public void setSectionHeadingChanged(CallableWithArgs<?> sectionHeadingChanged) {
        this.sectionHeadingChanged = sectionHeadingChanged;
    }

    public void setFieldInserted(CallableWithArgs<?> fieldInserted) {
        this.fieldInserted = fieldInserted;
    }

    public void setFieldsRemoved(CallableWithArgs<?> fieldsRemoved) {
        this.fieldsRemoved = fieldsRemoved;
    }


    public void sectionInserted(org.apache.pivot.wtk.Form arg0, int arg1) {
        if (sectionInserted != null) {
            sectionInserted.call(arg0, arg1);
        }
    }

    public void sectionsRemoved(org.apache.pivot.wtk.Form arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (sectionsRemoved != null) {
            sectionsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void sectionHeadingChanged(org.apache.pivot.wtk.Form.Section arg0) {
        if (sectionHeadingChanged != null) {
            sectionHeadingChanged.call(arg0);
        }
    }

    public void fieldInserted(org.apache.pivot.wtk.Form.Section arg0, int arg1) {
        if (fieldInserted != null) {
            fieldInserted.call(arg0, arg1);
        }
    }

    public void fieldsRemoved(org.apache.pivot.wtk.Form.Section arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (fieldsRemoved != null) {
            fieldsRemoved.call(arg0, arg1, arg2);
        }
    }

}
