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
package griffon.javafx.beans.binding

import groovy.transform.Canonical
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

import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier

@Unroll
class MappingBindingsSpec extends Specification {
    def "Map #type to object literal"() {
        given:
        ObjectBinding binding = MappingBindings.mapToObject(source)

        expect:
        !binding.get()

        when:
        source.set(value)

        then:
        value == binding.get()

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
        Binding binding = MappingBindings."mapTo${type}"(source)

        expect:
        !binding.get()

        when:
        source.set(value)

        then:
        value == binding.get()

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
        Binding binding = MappingBindings."map${type}"(source, function as Function)

        when:
        source.set(value)

        then:
        result == binding.get()

        where:
        type      | source                          | value      | result     | function
        'Integer' | new SimpleIntegerProperty()     | 1          | 2          | { i -> i * 2 }
        'Long'    | new SimpleLongProperty()        | 1L         | 2L         | { i -> i * 2 }
        'Float'   | new SimpleFloatProperty()       | 1f         | 2f         | { i -> (i * 2f) as float }
        'Double'  | new SimpleDoubleProperty()      | 1d         | 2d         | { i -> i * 2 }
        'String'  | new SimpleStringProperty()      | '1'        | '11'       | { i -> i * 2 }
        'Boolean' | new SimpleBooleanProperty()     | false      | true       | { i -> true }
        'Object'  | new SimpleObjectProperty<Box>() | new Box(1) | new Box(2) | { i -> new Box(2) }
    }

    def "Map#type with observable function"() {
        given:
        ObjectProperty mapper = new SimpleObjectProperty(function1 as Function)
        Binding binding = MappingBindings."map${type}"(source, mapper)

        when:
        source.set(value)

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        where:
        type      | source                          | value      | result1    | result2    | function1                  | function2
        'Integer' | new SimpleIntegerProperty()     | 1          | 2          | 3          | { i -> i * 2 }             | { i -> i + 2 }
        'Long'    | new SimpleLongProperty()        | 1L         | 2L         | 3L         | { i -> i * 2 }             | { i -> i + 2 }
        'Float'   | new SimpleFloatProperty()       | 1f         | 2f         | 3f         | { i -> (i * 2f) as float } | { i -> (i + 2f) as float }
        'Double'  | new SimpleDoubleProperty()      | 1d         | 2d         | 3d         | { i -> i * 2 }             | { i -> i + 2 }
        'String'  | new SimpleStringProperty()      | '1'        | '11'       | '12'       | { i -> i * 2 }             | { i -> i + 2 }
        'Boolean' | new SimpleBooleanProperty()     | false      | true       | false      | { i -> true }              | { i -> false }
        'Object'  | new SimpleObjectProperty<Box>() | new Box(1) | new Box(2) | new Box(3) | { i -> new Box(2) }        | { i -> new Box(3) }
    }

    def "Map#type with function (observables) value1=#value1 value2=#value2"() {
        given:
        Binding binding = MappingBindings."map${type}s"(ob1, ob2, defaultValue, function as BiFunction)

        when:
        ob1.set(value1)
        ob2.set(value2)

        then:
        result == binding.get()

        where:
        type      | ob1                             | ob2                             | defaultValue | value1     | value2     | result     | function
        'Integer' | new SimpleIntegerProperty()     | new SimpleIntegerProperty()     | 0            | 1          | 2          | 3          | { a, b -> a + b }
        'Long'    | new SimpleLongProperty()        | new SimpleLongProperty()        | 0L           | 1L         | 2L         | 3L         | { a, b -> a + b }
        'Float'   | new SimpleFloatProperty()       | new SimpleFloatProperty()       | 0f           | 1f         | 2f         | 3f         | { a, b -> (a + b) as float }
        'Double'  | new SimpleDoubleProperty()      | new SimpleDoubleProperty()      | 0d           | 1d         | 2d         | 3d         | { a, b -> a + b }
        'Boolean' | new SimpleBooleanProperty()     | new SimpleBooleanProperty()     | false        | false      | true       | true       | { a, b -> a || b }
        'String'  | new SimpleStringProperty()      | new SimpleStringProperty()      | '0'          | '1'        | '2'        | '12'       | { a, b -> a + b }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | null       | null       | new Box(3) | { a, b -> new Box(4) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | new Box(1) | null       | new Box(3) | { a, b -> new Box(4) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | null       | new Box(2) | new Box(3) | { a, b -> new Box(4) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | new Box(1) | new Box(2) | new Box(4) | { a, b -> new Box(4) }
    }

    def "Map#type with observable function (observables)"() {
        given:
        ObjectProperty mapper = new SimpleObjectProperty(function1 as BiFunction)
        Binding binding = MappingBindings."map${type}s"(ob1, ob2, defaultValue, mapper)

        when:
        ob1.set(value1)
        ob2.set(value2)

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as BiFunction)

        then:
        result2 == binding.get()

        where:
        type      | ob1                             | ob2                             | defaultValue | value1     | value2     | result1    | result2    | function1              | function2
        'Integer' | new SimpleIntegerProperty()     | new SimpleIntegerProperty()     | 0            | 1          | 2          | 3          | -1         | { a, b -> a + b }      | { a, b -> a - b }
        'Long'    | new SimpleLongProperty()        | new SimpleLongProperty()        | 0L           | 1L         | 2L         | 3L         | -1L        | { a, b -> a + b }            | { a, b -> a - b }
        'Float'   | new SimpleFloatProperty()       | new SimpleFloatProperty()       | 0f           | 1f         | 2f         | 3f         | -1f        | { a, b -> (a + b) as float } | { a, b -> (a - b) as float }
        'Double'  | new SimpleDoubleProperty()      | new SimpleDoubleProperty()      | 0d           | 1d         | 2d         | 3d         | -1d        | { a, b -> a + b }      | { a, b -> a - b }
        'Boolean' | new SimpleBooleanProperty()     | new SimpleBooleanProperty()     | false        | false      | true       | true       | false      | { a, b -> a || b }     | { a, b -> a && b }
        'String'  | new SimpleStringProperty()      | new SimpleStringProperty()      | '0'          | '1'        | '2'        | '12'       | '3'        | { a, b -> a + b }      | { a, b -> '3' }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | null       | null       | new Box(3) | new Box(3) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | new Box(1) | null       | new Box(3) | new Box(3) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | null       | new Box(2) | new Box(3) | new Box(3) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | new Box(3)   | new Box(1) | new Box(2) | new Box(4) | new Box(5) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
    }

    def "Map#type with function and supplier (observables) value1=#value1 value2=#value2"() {
        given:
        Binding binding = MappingBindings."map${type}s"(ob1, ob2, supplier as Supplier, function as BiFunction)

        when:
        ob1.set(value1)
        ob2.set(value2)

        then:
        result == binding.get()

        where:
        type      | ob1                             | ob2                             | supplier | value1     | value2     | result     | function
        'Integer' | new SimpleIntegerProperty()     | new SimpleIntegerProperty()     | {
            0
        }                                                                                        | 1          | 2          | 3          | { a, b -> a + b }
        'Long'    | new SimpleLongProperty()        | new SimpleLongProperty()        | {
            0L
        }                                                                                        | 1L         | 2L         | 3L         | { a, b -> a + b }
        'Float'   | new SimpleFloatProperty()       | new SimpleFloatProperty()       | {
            0f
        }                                                                                        | 1f         | 2f         | 3f         | { a, b -> (a + b) as float }
        'Double'  | new SimpleDoubleProperty()      | new SimpleDoubleProperty()      | {
            0d
        }                                                                                        | 1d         | 2d         | 3d         | { a, b -> a + b }
        'Boolean' | new SimpleBooleanProperty()     | new SimpleBooleanProperty()     | {
            false
        }                                                                                        | false      | true       | true       | { a, b -> a || b }
        'String'  | new SimpleStringProperty()      | new SimpleStringProperty()      | {
            '0'
        }                                                                                        | '1'        | '2'        | '12'       | { a, b -> a + b }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | null       | null       | new Box(3) | { a, b -> new Box(4) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | new Box(1) | null       | new Box(3) | { a, b -> new Box(4) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | null       | new Box(2) | new Box(3) | { a, b -> new Box(4) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | new Box(1) | new Box(2) | new Box(4) | { a, b -> new Box(4) }
    }

    def "Map#type with observable function and supplier (observables)"() {
        given:
        ObjectProperty mapper = new SimpleObjectProperty(function1 as BiFunction)
        Binding binding = MappingBindings."map${type}s"(ob1, ob2, supplier as Supplier, mapper)

        when:
        ob1.set(value1)
        ob2.set(value2)

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as BiFunction)

        then:
        result2 == binding.get()

        where:
        type      | ob1                             | ob2                             | supplier | value1                                                                                        | value2     | result1    | result2    | function1              | function2
        'Integer' | new SimpleIntegerProperty()     | new SimpleIntegerProperty()     | {
            0
        }                                                                                        | 1     | 2          | 3          | -1         | { a, b -> a + b }      | { a, b -> a - b }
        'Long'    | new SimpleLongProperty()        | new SimpleLongProperty()        | {
            0L
        }                                                                                        | 1L         | 2L         | 3L         | -1L        | { a, b -> a + b }            | { a, b -> a - b }
        'Float'   | new SimpleFloatProperty()       | new SimpleFloatProperty()       | {
            0f
        }                                                                                        | 1f         | 2f         | 3f         | -1f        | { a, b -> (a + b) as float } | { a, b -> (a - b) as float }
        'Double'  | new SimpleDoubleProperty()      | new SimpleDoubleProperty()      | {
            0d
        }                                                                                        | 1d         | 2d         | 3d         | -1d        | { a, b -> a + b }            | { a, b -> a - b }
        'Boolean' | new SimpleBooleanProperty()     | new SimpleBooleanProperty()     | {
            false
        }                                                                                        | false | true       | true       | false      | { a, b -> a || b }     | { a, b -> a && b }
        'String'  | new SimpleStringProperty()      | new SimpleStringProperty()      | {
            '0'
        }                                                                                        | '1'                                                                                           | '2'        | '12'       | '3'        | { a, b -> a + b }      | { a, b -> '3' }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | null                                                                                          | null       | new Box(3) | new Box(3) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | new Box(1)                                                                                    | null       | new Box(3) | new Box(3) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | null                                                                                          | new Box(2) | new Box(3) | new Box(3) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
        'Object'  | new SimpleObjectProperty<Box>() | new SimpleObjectProperty<Box>() | {
            new Box(3)
        }                                                                                        | new Box(1)                                                                                    | new Box(2) | new Box(4) | new Box(5) | { a, b -> new Box(4) } | { a, b -> new Box(5) }
    }

    @Canonical
    private static class Box {
        int id
    }
}
