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
package griffon.core.editors;

import griffon.core.formatters.Formatter;
import griffon.core.formatters.LocaleFormatter;

import java.util.Locale;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class LocalePropertyEditor extends AbstractPropertyEditor {
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof Locale) {
            super.setValueInternal(value);
        } else if (value instanceof CharSequence) {
            super.setValueInternal(resolveFormatter().parse(String.valueOf(value)));
        } else {
            throw illegalValue(value, Locale.class);
        }
    }

    protected Formatter<Locale> resolveFormatter() {
        return new LocaleFormatter();
    }
}
