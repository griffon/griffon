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
package griffon.core.formatters

import spock.lang.Shared
import spock.lang.Specification

abstract class FormatterSpecSupport extends Specification {
    @Shared
    private Locale defaultLocale = Locale.default

    def setup() {
        Locale.default = Locale.US
    }

    def cleanup() {
        Locale.default = defaultLocale
    }

    static Date epochAsDate() {
        // Thu Jan 01 00:00:00 1970
        TimeZone.setDefault(TimeZone.getTimeZone('Etc/GMT'))
        clearTime(new Date(0))
    }

    static Calendar epochAsCalendar() {
        // Thu Jan 01 00:00:00 1970
        TimeZone.setDefault(TimeZone.getTimeZone('Etc/GMT'))
        Calendar c = Calendar.getInstance()
        c.time = epochAsDate()
        c
    }

    private static void clearTimeCommon(Calendar self) {
        self.set(Calendar.HOUR_OF_DAY, 0)
        self.clear(Calendar.MINUTE)
        self.clear(Calendar.SECOND)
        self.clear(Calendar.MILLISECOND)
    }

    static Date clearTime(Date self) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(self)
        clearTimeCommon(calendar)
        self.setTime(calendar.getTime().getTime())
        return self
    }

    static Calendar clearTime(Calendar self) {
        clearTimeCommon(self)
        return self
    }
}
