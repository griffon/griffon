/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.core.formatters;

import griffon.annotations.core.Nullable;

import java.util.Locale;

import static griffon.util.GriffonApplicationUtils.parseLocale;
import static griffon.util.GriffonNameUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 2.5.0
 */
public class LocaleFormatter extends AbstractFormatter<Locale> {
    @Nullable
    @Override
    public String format(@Nullable Locale locale) {
        if (locale == null) { return null; }
        StringBuilder b = new StringBuilder();
        b.append(locale.getLanguage());
        if (isNotBlank(locale.getCountry())) {
            b.append("_").append(locale.getCountry());
            if (isNotBlank(locale.getVariant())) {
                b.append("_").append(locale.getVariant());
            }
        }

        return b.toString();
    }

    @Nullable
    @Override
    public Locale parse(@Nullable String str) throws ParseException {
        return parseLocale(str);
    }
}
