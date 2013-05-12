/*
 * Copyright 2010-2013 the original author or authors.
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

package griffon.core.resources.editors;

import griffon.core.resources.formatters.Formatter;
import griffon.core.resources.formatters.ShortFormatter;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class ShortPropertyEditor extends AbstractPropertyEditor {
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof Number) {
            handleAsNumber((Number) value);
        } else {
            throw illegalValue(value, Short.class);
        }
    }

    private void handleAsString(String str) {
        try {
            super.setValueInternal(isBlank(str) ? null : Short.parseShort(str));
        } catch (NumberFormatException e) {
            throw illegalValue(str, Short.class, e);
        }
    }

    private void handleAsNumber(Number number) {
        super.setValueInternal(number.shortValue());
    }

    protected Formatter resolveFormatter() {
        return isBlank(getFormat()) ? null : new ShortFormatter(getFormat());
    }
}
