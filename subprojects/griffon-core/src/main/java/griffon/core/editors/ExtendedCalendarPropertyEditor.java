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
package griffon.core.editors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class ExtendedCalendarPropertyEditor extends CalendarPropertyEditor {
    @Override
    protected void setValueInternal(Object value) {
        if (value instanceof LocalDate) {
            handleAsLocalDate((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            handleAsLocalDateTime(((LocalDateTime) value));
        } else {
            super.setValueInternal(value);
        }
    }

    protected void handleAsLocalDate(LocalDate value) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(value.toEpochDay()));
        super.setValueInternal(c);
    }

    protected void handleAsLocalDateTime(LocalDateTime value) {
        LocalDate localDate = value.toLocalDate();
        LocalTime localTime = value.toLocalTime();

        Calendar c = Calendar.getInstance();
        c.set(
            localDate.getYear(),
            localDate.getMonthValue() - 1,
            localDate.getDayOfMonth(),
            localTime.getHour(),
            localTime.getMinute(),
            localTime.getSecond()
        );

        super.setValueInternal(c);
    }
}