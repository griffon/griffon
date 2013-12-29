/*
 * Copyright 2010-2014 the original author or authors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.beans.PropertyEditorSupport;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public abstract class AbstractPropertyEditor extends PropertyEditorSupport implements ExtendedPropertyEditor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPropertyEditor.class);

    private String format;

    @Nullable
    public String getFormat() {
        return format;
    }

    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    @Override
    public String getAsText() {
        return isBlank(getFormat()) ? getAsTextInternal() : getFormattedValue();
    }

    @Override
    public void setAsText(String str) throws IllegalArgumentException {
        if (isBlank(getFormat())) {
            setAsTextInternal(str);
        } else {
            setFormattedValue(str);
        }
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof CharSequence) {
            setFormattedValue(String.valueOf(value));
        } else {
            setValueInternal(value);
        }
    }

    protected void setValueInternal(Object value) {
        super.setValue(value);
    }

    protected Object getValueInternal() {
        return super.getValue();
    }

    protected void setAsTextInternal(String str) throws IllegalArgumentException {
        setValueInternal(str);
    }

    protected String getAsTextInternal() {
        return super.getAsText();
    }

    @SuppressWarnings("unchecked")
    public String getFormattedValue() {
        Object value = getValueInternal();
        Formatter formatter = resolveFormatter();
        if (formatter != null) {
            return formatter.format(value);
        }
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void setFormattedValue(String value) {
        Formatter<?> formatter = resolveFormatter();
        if (formatter != null) {
            try {
                setValueInternal(formatter.parse(value));
            } catch (ParseException e) {
                e = (ParseException) sanitize(e);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Cannot parse value " + value, e);
                }

                throw new ValueConversionException(value, e);
            }
        } else {
            setValueInternal(value);
        }
    }

    protected Formatter<?> resolveFormatter() {
        return null;
    }

    protected ValueConversionException illegalValue(Object value, Class<?> klass) {
        throw new ValueConversionException(value, klass);
    }

    protected ValueConversionException illegalValue(Object value, Class<?> klass, Exception e) {
        throw new ValueConversionException(value, klass, e);
    }
}
