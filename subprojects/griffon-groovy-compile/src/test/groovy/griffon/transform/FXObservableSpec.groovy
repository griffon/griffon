/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.transform

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.FloatProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.LongProperty
import javafx.beans.property.MapProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SetProperty
import javafx.beans.property.StringProperty
import spock.lang.Specification

class FXObservableSpec extends Specification {
    void "Observable primitive properties"() {
        given:
        String script = '''
        @griffon.transform.FXObservable
        class Bean {
            boolean theBoolean
            byte theByte
            short theShort
            int theInt
            long theLong
            float theFloat
            double theDouble
        }
        new Bean()
        '''

        when:
        def bean = new GroovyShell().evaluate(script)

        then:
        bean.theBooleanProperty() instanceof BooleanProperty
        bean.theByteProperty() instanceof IntegerProperty
        bean.theShortProperty() instanceof IntegerProperty
        bean.theIntProperty() instanceof IntegerProperty
        bean.theLongProperty() instanceof LongProperty
        bean.theFloatProperty() instanceof FloatProperty
        bean.theDoubleProperty() instanceof DoubleProperty
    }

    void "Observable wrapper properties"() {
        given:
        String script = '''
        @griffon.transform.FXObservable
        class Bean {
            Boolean theBoolean
            byte theByte
            short theShort
            int theInt
            Long theLong
            Float theFloat
            Double theDouble
            String theString
            Date theDate
            Object theObject
        }
        new Bean()
        '''

        when:
        def bean = new GroovyShell().evaluate(script)

        then:
        bean.theBooleanProperty() instanceof BooleanProperty
        bean.theByteProperty() instanceof IntegerProperty
        bean.theShortProperty() instanceof IntegerProperty
        bean.theIntProperty() instanceof IntegerProperty
        bean.theLongProperty() instanceof LongProperty
        bean.theFloatProperty() instanceof FloatProperty
        bean.theDoubleProperty() instanceof DoubleProperty
        bean.theStringProperty() instanceof StringProperty
        bean.theDateProperty() instanceof ObjectProperty
        bean.theObjectProperty() instanceof ObjectProperty
    }

    void "Observable collection properties"() {
        given:
        String script = '''
        @griffon.transform.FXObservable
        class Bean {
            List<String> theList
            Map<String,String> theMap
            Set<String> theSet
        }
        new Bean()
        '''

        when:
        def bean = new GroovyShell().evaluate(script)

        then:
        bean.theListProperty() instanceof ListProperty
        bean.theMapProperty() instanceof MapProperty
        bean.theSetProperty() instanceof SetProperty
    }
}