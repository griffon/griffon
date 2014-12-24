/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;
import org.apache.pivot.util.CalendarDate;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class CalendarButtonAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.CalendarButtonListener {
    private CallableWithArgs<Void> monthChanged;
    private CallableWithArgs<Void> localeChanged;
    private CallableWithArgs<Void> disabledDateFilterChanged;
    private CallableWithArgs<Void> yearChanged;

    public CallableWithArgs<Void> getMonthChanged() {
        return this.monthChanged;
    }

    public CallableWithArgs<Void> getLocaleChanged() {
        return this.localeChanged;
    }

    public CallableWithArgs<Void> getDisabledDateFilterChanged() {
        return this.disabledDateFilterChanged;
    }

    public CallableWithArgs<Void> getYearChanged() {
        return this.yearChanged;
    }


    public void setMonthChanged(CallableWithArgs<Void> monthChanged) {
        this.monthChanged = monthChanged;
    }

    public void setLocaleChanged(CallableWithArgs<Void> localeChanged) {
        this.localeChanged = localeChanged;
    }

    public void setDisabledDateFilterChanged(CallableWithArgs<Void> disabledDateFilterChanged) {
        this.disabledDateFilterChanged = disabledDateFilterChanged;
    }

    public void setYearChanged(CallableWithArgs<Void> yearChanged) {
        this.yearChanged = yearChanged;
    }


    public void monthChanged(org.apache.pivot.wtk.CalendarButton arg0, int arg1) {
        if (monthChanged != null) {
            monthChanged.call(arg0, arg1);
        }
    }

    public void localeChanged(org.apache.pivot.wtk.CalendarButton arg0, java.util.Locale arg1) {
        if (localeChanged != null) {
            localeChanged.call(arg0, arg1);
        }
    }

    public void disabledDateFilterChanged(org.apache.pivot.wtk.CalendarButton arg0, org.apache.pivot.util.Filter<CalendarDate> arg1) {
        if (disabledDateFilterChanged != null) {
            disabledDateFilterChanged.call(arg0, arg1);
        }
    }

    public void yearChanged(org.apache.pivot.wtk.CalendarButton arg0, int arg1) {
        if (yearChanged != null) {
            yearChanged.call(arg0, arg1);
        }
    }

}
