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
package griffon.javafx.support

import javafx.beans.binding.Binding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Function

@Unroll
class BindingUtilsSpec extends Specification {
    def "Map #type to object literal"() {
        given:
        ObjectBinding ob = BindingUtils.mapAsObject(source)

        expect:
        !ob.get()

        when:
        source.set(value)

        then:
        value == ob.get()

        where:
        type      | source                      | value
        'String'  | new SimpleStringProperty()  | '1'
        'Boolean' | new SimpleBooleanProperty() | true
        'Integer' | new SimpleIntegerProperty() | 1
        'Long'    | new SimpleLongProperty()    | 1L
        'Float'   | new SimpleFloatProperty()   | 1f
        'Double'  | new SimpleDoubleProperty()  | 1d
    }

    def "Map object observable to #type binding"() {
        given:
        Binding b = BindingUtils."mapAs${type}"(source)

        expect:
        !b.get()

        when:
        source.set(value)

        then:
        value == b.get()

        where:
        type      | source                                   | value
        'String'  | new SimpleObjectProperty<String>('')     | '1'
        'Boolean' | new SimpleObjectProperty<Boolean>(false) | true
        'Integer' | new SimpleObjectProperty<Integer>(0)     | 1
        'Long'    | new SimpleObjectProperty<Long>(0L)       | 1L
        'Float'   | new SimpleObjectProperty<Float>(0f)      | 1f
        'Double'  | new SimpleObjectProperty<Double>(0d)     | 1d
    }

    def "Map#type with function"() {
        given:
        Binding b = BindingUtils."map${type}"(source, function as Function)

        when:
        source.set(value)

        then:
        result == b.get()

        where:
        type      | source                      | value | result | function
        'Integer' | new SimpleIntegerProperty() | 1     | 2      | { i -> i * 2 }
        'Long'    | new SimpleLongProperty()    | 1L    | 2L     | { i -> i * 2 }
        'Float'   | new SimpleFloatProperty()   | 1f    | 2f     | { i -> (i * 2f) as float }
        'Double'  | new SimpleDoubleProperty()  | 1d    | 2d     | { i -> i * 2 }
        'String'  | new SimpleStringProperty()  | '1'   | '11'   | { i -> i * 2 }
    }

    def "Map#type with observable function"() {
        given:
        ObjectProperty mapper = new SimpleObjectProperty(function1 as Function)
        Binding b = BindingUtils."map${type}"(source, mapper)

        when:
        source.set(value)

        then:
        result1 == b.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == b.get()

        where:
        type      | source                      | value | result1 | result2 | function1                  | function2
        'Integer' | new SimpleIntegerProperty() | 1     | 2       | 3       | { i -> i * 2 }             | { i -> i + 2 }
        'Long'    | new SimpleLongProperty()    | 1L    | 2L      | 3L      | { i -> i * 2 }             | { i -> i + 2 }
        'Float'   | new SimpleFloatProperty()   | 1f    | 2f      | 3f      | { i -> (i * 2f) as float } | { i -> (i + 2f) as float }
        'Double'  | new SimpleDoubleProperty()  | 1d    | 2d      | 3d      | { i -> i * 2 }             | { i -> i + 2 }
        'String'  | new SimpleStringProperty()  | '1'   | '11'    | '12'    | { i -> i * 2 }             | { i -> i + 2 }
    }
}
