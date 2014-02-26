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
package griffon.builder.pivot.factory

import org.apache.pivot.util.CalendarDate
import org.apache.pivot.wtk.Slider
import org.apache.pivot.wtk.content.CalendarDateSpinnerData

/**
 * @author Andres Almiray
 */
class CalendarDateSpinnerDataFactory extends PivotBeanFactory {
    CalendarDateSpinnerDataFactory() {
        super(CalendarDateSpinnerData, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (!attributes.containsKey('lowerBound')) {
            throw new IllegalArgumentException("In $name you must define a value for lowerBound: of type ${CalendarDate.class.name}")
        }
        if (!attributes.containsKey('upperBound')) {
            throw new IllegalArgumentException("In $name you must define a value for upperBound: of type ${CalendarDate.class.name}")
        }

        CalendarDate lowerBound = attributes.remove('lowerBound')
        CalendarDate upperBound = attributes.remove('upperBound')
        return new CalendarDateSpinnerData(lowerBound, upperBound)
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (parent instanceof Slider) parent.setSpinnerData(node)
    }
}
