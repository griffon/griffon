/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package org.codehaus.griffon.formatter;

import griffon.annotations.core.Nullable;
import griffon.formatter.ParseException;

import java.util.Locale;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LocaleFormatter extends AbstractFormatter<Locale> {
    @Nullable
    @Override
    public String format(@Nullable Locale locale) {
        if (locale == null) { return null; }
        StringBuilder b = new StringBuilder();
        b.append(locale.getLanguage());
        if (!isBlank(locale.getCountry())) {
            b.append("_").append(locale.getCountry());
            if (!isBlank(locale.getVariant())) {
                b.append("_").append(locale.getVariant());
            }
        }

        return b.toString();
    }

    @Nullable
    @Override
    public Locale parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) { return null; }
        String[] parts = str.split("_");
        switch (parts.length) {
            case 1:
                return new Locale(parts[0]);
            case 2:
                return new Locale(parts[0], parts[1]);
            case 3:
                return new Locale(parts[0], parts[1], parts[2]);
            default:
                return null;
        }
    }
}
