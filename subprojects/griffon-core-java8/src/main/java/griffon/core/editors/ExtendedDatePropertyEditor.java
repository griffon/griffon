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

import griffon.metadata.PropertyEditorFor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
@PropertyEditorFor(Date.class)
public class ExtendedDatePropertyEditor extends DatePropertyEditor {
    @Override
    protected void setValueInternal(Object value) {
        if (value instanceof LocalDate) {
            handleAsLocalDate((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            handleAsLocalDate(((LocalDateTime) value).toLocalDate());
        } else {
            super.setValueInternal(value);
        }
    }

    protected void handleAsLocalDate(LocalDate value) {
        super.setValueInternal(new Date(value.toEpochDay()));
    }
}