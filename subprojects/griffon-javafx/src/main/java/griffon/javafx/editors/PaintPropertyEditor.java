/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.javafx.editors;

import griffon.core.editors.AbstractPropertyEditor;
import griffon.metadata.PropertyEditorFor;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import static griffon.core.editors.PropertyEditorResolver.findEditor;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
@PropertyEditorFor(Paint.class)
public class PaintPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        return getValueInternal().toString();
    }

    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleInternal(String.valueOf(value).trim());
        } else if (value instanceof List || value instanceof Map) {
            handleInternal(value);
        } else if (value instanceof Paint) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Paint.class);
        }
    }

    private void handleInternal(Object value) {
        for (Class<?> type : new Class[]{Color.class, RadialGradient.class, LinearGradient.class}) {
            try {
                PropertyEditor propertyEditor = findEditor(type);
                propertyEditor.setValue(value);
                super.setValueInternal(propertyEditor.getValue());
                return;
            } catch (Exception e) {
                // ignore
            }
        }
        throw illegalValue(value, Paint.class);
    }
}