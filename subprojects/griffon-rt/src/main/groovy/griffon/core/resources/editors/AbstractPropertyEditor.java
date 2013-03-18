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
import griffon.core.resources.formatters.ParseException;
import griffon.exceptions.GriffonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;

import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public abstract class AbstractPropertyEditor extends PropertyEditorSupport implements ExtendedPropertyEditor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPropertyEditor.class);

    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getAsText() {
        return isBlank(getFormat()) ? super.getAsText() : getFormattedValue();
    }

    @Override
    public void setAsText(String str) throws IllegalArgumentException {
        if (isBlank(getFormat())) {
            super.setValue(str);
        } else {
            setValueWithFormat(str);
        }
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof CharSequence) {
            setValueWithFormat(String.valueOf(value));
        } else {
            super.setValue(value);
        }
    }

    public String getFormattedValue() {
        Object value = getValue();
        Formatter formatter = resolveFormatter();
        if (formatter != null) {
            return formatter.format(value);
        }
        return value != null ? String.valueOf(value) : null;
    }

    public void setValueWithFormat(String value) {
        Formatter formatter = resolveFormatter();
        if (formatter != null) {
            try {
                setValue(formatter.parse(value));
            } catch (ParseException e) {
                Throwable t = sanitize(e);
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot parse value " + value, t);
                }
                throw new GriffonException(t);
            }
        } else {
            super.setValue(value);
        }
    }

    protected Formatter resolveFormatter() {
        return null;
    }

    protected ValueConversionException illegalValue(Object value, Class klass) {
        throw new ValueConversionException(value, klass);
    }

    protected ValueConversionException illegalValue(Object value, Class klass, Exception e) {
        throw new ValueConversionException(value, klass, e);
    }
}
