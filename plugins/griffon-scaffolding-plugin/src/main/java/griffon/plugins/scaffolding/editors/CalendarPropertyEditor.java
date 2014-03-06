/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.plugins.scaffolding.editors;

import org.joda.time.*;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Andres Almiray
 */
public class CalendarPropertyEditor extends griffon.core.editors.CalendarPropertyEditor {
    @Override
    protected void setValueInternal(Object value) {
        if (value instanceof DateTime) {
            super.setValueInternal(calendar(((DateTime) value).toDate()));
        } else if (value instanceof LocalDateTime) {
            super.setValueInternal(calendar(((LocalDateTime) value).toDate()));
        } else if (value instanceof LocalDate) {
            super.setValueInternal(calendar(((LocalDate) value).toDate()));
        } else if (value instanceof DateMidnight) {
            super.setValueInternal(calendar(((DateMidnight) value).toDate()));
        } else if (value instanceof Instant) {
            super.setValueInternal(calendar(((Instant) value).toDate()));
        } else {
            super.setValueInternal(value);
        }
    }

    private static Calendar calendar(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c;
    }
}