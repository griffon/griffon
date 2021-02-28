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
package griffon.javafx.groovy

import groovy.transform.Canonical
import groovy.transform.Sortable
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier

import static java.lang.Boolean.parseBoolean
import static java.lang.Double.parseDouble
import static java.lang.Float.parseFloat
import static java.lang.Integer.parseInt
import static java.lang.Long.parseLong

@Unroll
class ReducingBindingsExtensionSpec extends Specification {
    def "Reduce List with default value"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        items.addAll(elements)

        when:
        Binding binding = items.reduce(defaultValue, reducer as BinaryOperator)

        then:
        value == binding.get()

        where:
        elements        | defaultValue | reducer           | value
        []              | '<empty>'    | { a, b -> a + b } | '<empty>'
        ['1', '2', '3'] | '<empty>'    | { a, b -> a + b } | '123'
    }

    def "Reduce List with supplier"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        items.addAll(elements)

        when:
        Binding binding = items.reduce(supplier as Supplier, reducer as BinaryOperator)

        then:
        value == binding.get()

        where:
        elements        | supplier      | reducer           | value
        []              | { '<empty>' } | { a, b -> a + b } | '<empty>'
        ['1', '2', '3'] | { '<empty>' } | { a, b -> a + b } | '123'
    }

    def "Reduce List with default value and observable reducer"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        items.addAll(elements)

        when:
        Binding binding = items.reduce(defaultValue, observableReducer)

        then:
        value1 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        value2 == binding.get()

        where:
        elements        | defaultValue | reducer1          | value1    | reducer2                | value2
        []              | '<empty>'    | { a, b -> a + b } | '<empty>' | { a, b -> a + ',' + b } | '<empty>'
        ['1', '2', '3'] | '<empty>'    | { a, b -> a + b } | '123'     | { a, b -> a + ',' + b } | '1,2,3'
    }

    def "Reduce List with supplier and observable reducer"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        items.addAll(elements)

        when:
        Binding binding = items.reduce(supplier as Supplier, observableReducer)

        then:
        value1 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        value2 == binding.get()

        where:
        elements        | supplier      | reducer1          | value1    | reducer2                | value2
        []              | { '<empty>' } | { a, b -> a + b } | '<empty>' | { a, b -> a + ',' + b } | '<empty>'
        ['1', '2', '3'] | { '<empty>' } | { a, b -> a + b } | '123'     | { a, b -> a + ',' + b } | '1,2,3'
    }

    def "Reduce Set with default value"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        items.addAll(elements)

        when:
        Binding binding = items.reduce(defaultValue, reducer as BinaryOperator)

        then:
        value == binding.get()

        where:
        elements        | defaultValue | reducer           | value
        []              | '<empty>'    | { a, b -> a + b } | '<empty>'
        ['1', '2', '3'] | '<empty>'    | { a, b -> a + b } | '123'
    }

    def "Reduce Set with supplier"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        items.addAll(elements)

        when:
        Binding binding = items.reduce(supplier as Supplier, reducer as BinaryOperator)

        then:
        value == binding.get()

        where:
        elements        | supplier      | reducer           | value
        []              | { '<empty>' } | { a, b -> a + b } | '<empty>'
        ['1', '2', '3'] | { '<empty>' } | { a, b -> a + b } | '123'
    }

    def "Reduce Set with default value and observable reducer"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        items.addAll(elements)

        when:
        Binding binding = items.reduce(defaultValue, observableReducer)

        then:
        value1 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        value2 == binding.get()

        where:
        elements        | defaultValue | reducer1          | value1    | reducer2                | value2
        []              | '<empty>'    | { a, b -> a + b } | '<empty>' | { a, b -> a + ',' + b } | '<empty>'
        ['1', '2', '3'] | '<empty>'    | { a, b -> a + b } | '123'     | { a, b -> a + ',' + b } | '1,2,3'
    }

    def "Reduce Set with supplier and observable reducer"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        items.addAll(elements)

        when:
        Binding binding = items.reduce(supplier as Supplier, observableReducer)

        then:
        value1 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        value2 == binding.get()

        where:
        elements        | supplier      | reducer1          | value1    | reducer2                | value2
        []              | { '<empty>' } | { a, b -> a + b } | '<empty>' | { a, b -> a + ',' + b } | '<empty>'
        ['1', '2', '3'] | { '<empty>' } | { a, b -> a + b } | '123'     | { a, b -> a + ',' + b } | '1,2,3'
    }

    def "Reduce Map with default value"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        items.putAll(elements)

        when:
        Binding binding = items.reduce(defaultValue, reducer as BinaryOperator)

        then:
        value == binding.get()

        where:
        elements                 | defaultValue | reducer           | value
        [:]                      | '<empty>'    | { a, b -> a + b } | '<empty>'
        [a: '1', b: '2', c: '3'] | '<empty>'    | { a, b -> a + b } | '123'
    }

    def "Reduce Map with supplier"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        items.putAll(elements)

        when:
        Binding binding = items.reduce(supplier as Supplier, reducer as BinaryOperator)

        then:
        value == binding.get()

        where:
        elements                 | supplier      | reducer           | value
        [:]                      | { '<empty>' } | { a, b -> a + b } | '<empty>'
        [a: '1', b: '2', c: '3'] | { '<empty>' } | { a, b -> a + b } | '123'
    }

    def "Reduce Map with default value and observable reducer"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        items.putAll(elements)

        when:
        Binding binding = items.reduce(defaultValue, observableReducer)

        then:
        value1 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        value2 == binding.get()

        where:
        elements                 | defaultValue | reducer1          | value1    | reducer2                | value2
        [:]                      | '<empty>'    | { a, b -> a + b } | '<empty>' | { a, b -> a + ',' + b } | '<empty>'
        [a: '1', b: '2', c: '3'] | '<empty>'    | { a, b -> a + b } | '123'     | { a, b -> a + ',' + b } | '1,2,3'
    }

    def "Reduce Map with supplier and observable reducer"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        items.putAll(elements)

        when:
        Binding binding = items.reduce(supplier as Supplier, observableReducer)

        then:
        value1 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        value2 == binding.get()

        where:
        elements                 | supplier      | reducer1          | value1    | reducer2                | value2
        [:]                      | { '<empty>' } | { a, b -> a + b } | '<empty>' | { a, b -> a + ',' + b } | '<empty>'
        [a: '1', b: '2', c: '3'] | { '<empty>' } | { a, b -> a + b } | '123'     | { a, b -> a + ',' + b } | '1,2,3'
    }

    def "ReduceThenMapTo#type list with functions and default value"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."reduceThenMapTo${type}"(defaultValue, reducer as BinaryOperator, mapper as Function)

        expect:
        mapper(defaultValue) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | 'true'       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | '4'          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | '4'          | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | '4'          | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | '4'          | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "ReduceThenMapTo#type list with functions and supplier"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."reduceThenMapTo${type}"(supplier as Supplier, reducer as BinaryOperator, mapper as Function)

        expect:
        mapper(supplier()) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | supplier   | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | { 'true' } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | { '4' }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | { '4' }    | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | { '4' }    | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | { '4' }    | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | { 'A' }    | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "ReduceThenMapTo#type list with observable functions and default value"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."reduceThenMapTo${type}"(defaultValue, observableReducer, observableMapper)

        expect:
        mapper1(defaultValue) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | defaultValue | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2           | mapper2                               | result3 | result4
        'Boolean' | 'true'       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> 'true' } | { i -> false }                        | true    | false
        'Integer' | '4'          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> '5' }    | { i -> 2 * parseInt(i) }              | 5       | 10
        'Long'    | '4'          | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> '5' }    | { i -> 2 * parseLong(i) }             | 5L      | 10L
        'Float'   | '4'          | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> '5' }    | { i -> (2 * parseFloat(i)) as float } | 5f      | 10f
        'Double'  | '4'          | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> '5' }    | { i -> 2 * parseDouble(i) }           | 5d      | 10d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> 'D' }    | { i -> i * 3 }                        | 'DD'    | 'DDD'
    }

    def "ReduceThenMapTo#type list with observable functions and supplier"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."reduceThenMapTo${type}"(supplier as Supplier, observableReducer, observableMapper)

        expect:
        mapper1(supplier()) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | supplier  | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2           | mapper2                               | result3 | result4
        'Boolean' | {'true' } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> 'true' } | { i -> false }                        | true    | false
        'Integer' | {'4' }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> '5' }    | { i -> 2 * parseInt(i) }              | 5       | 10
        'Long'    | {'4' }    | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> '5' }    | { i -> 2 * parseLong(i) }             | 5L      | 10L
        'Float'   | {'4' }    | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> '5' }    | { i -> (2 * parseFloat(i)) as float } | 5f      | 10f
        'Double'  | {'4' }    | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> '5' }    | { i -> 2 * parseDouble(i) }           | 5d      | 10d
        'String'  | {'A' }    | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> 'D' }    | { i -> i * 3 }                        | 'DD'    | 'DDD'
    }

    def "ReduceThenMapTo#type set with functions and default value"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."reduceThenMapTo${type}"(defaultValue, reducer as BinaryOperator, mapper as Function)

        expect:
        mapper(defaultValue) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | 'true'       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | '4'          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | '4'          | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | '4'          | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | '4'          | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "ReduceThenMapTo#type set with functions and supplier"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."reduceThenMapTo${type}"(supplier as Supplier, reducer as BinaryOperator, mapper as Function)

        expect:
        mapper(supplier()) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | supplier   | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | { 'true' } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | { '4' }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | { '4' }    | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | { '4' }    | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | { '4' }    | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | { 'A' }    | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "ReduceThenMapTo#type set with observable functions and default value"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."reduceThenMapTo${type}"(defaultValue, observableReducer, observableMapper)

        expect:
        mapper1(defaultValue) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | defaultValue | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2           | mapper2                               | result3 | result4
        'Boolean' | 'true'       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> 'true' } | { i -> false }                        | true    | false
        'Integer' | '4'          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> '5' }    | { i -> 2 * parseInt(i) }              | 5       | 10
        'Long'    | '4'          | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> '5' }    | { i -> 2 * parseLong(i) }             | 5L      | 10L
        'Float'   | '4'          | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> '5' }    | { i -> (2 * parseFloat(i)) as float } | 5f      | 10f
        'Double'  | '4'          | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> '5' }    | { i -> 2 * parseDouble(i) }           | 5d      | 10d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> 'D' }    | { i -> i * 3 }                        | 'DD'    | 'DDD'
    }

    def "ReduceThenMapTo#type set with observable functions and supplier"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."reduceThenMapTo${type}"(supplier as Supplier, observableReducer, observableMapper)

        expect:
        mapper1(supplier()) == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | supplier  | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2           | mapper2                               | result3 | result4
        'Boolean' | {'true' } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> 'true' } | { i -> false }                        | true    | false
        'Integer' | {'4' }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> '5' }    | { i -> 2 * parseInt(i) }              | 5       | 10
        'Long'    | {'4' }    | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> '5' }    | { i -> 2 * parseLong(i) }             | 5L      | 10L
        'Float'   | {'4' }    | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> '5' }    | { i -> (2 * parseFloat(i)) as float } | 5f      | 10f
        'Double'  | {'4' }    | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> '5' }    | { i -> 2 * parseDouble(i) }           | 5d      | 10d
        'String'  | {'A' }    | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> 'D' }    | { i -> i * 3 }                        | 'DD'    | 'DDD'
    }

    def "ReduceThenMapTo#type map with functions and default value"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."reduceThenMapTo${type}"(defaultValue, reducer as BinaryOperator, mapper as Function)

        expect:
        mapper(defaultValue) == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | 'true'       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | '4'          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | '4'          | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | '4'          | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | '4'          | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "ReduceThenMapTo#type map with functions and supplier"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."reduceThenMapTo${type}"(supplier as Supplier, reducer as BinaryOperator, mapper as Function)

        expect:
        mapper(supplier()) == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        where:
        type      | supplier   | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | { 'true' } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | { '4' }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | { '4' }    | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | { '4' }    | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | { '4' }    | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | { 'A' }    | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "ReduceThenMapTo#type map with observable functions and default value"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."reduceThenMapTo${type}"(defaultValue, observableReducer, observableMapper)

        expect:
        mapper1(defaultValue) == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | defaultValue | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2           | mapper2                               | result3 | result4
        'Boolean' | 'true'       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> 'true' } | { i -> false }                        | true    | false
        'Integer' | '4'          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> '5' }    | { i -> 2 * parseInt(i) }              | 5       | 10
        'Long'    | '4'          | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> '5' }    | { i -> 2 * parseLong(i) }             | 5L      | 10L
        'Float'   | '4'          | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> '5' }    | { i -> (2 * parseFloat(i)) as float } | 5f      | 10f
        'Double'  | '4'          | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> '5' }    | { i -> 2 * parseDouble(i) }           | 5d      | 10d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> 'D' }    | { i -> i * 3 }                        | 'DD'    | 'DDD'
    }

    def "ReduceThenMapTo#type map with observable functions and supplier"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."reduceThenMapTo${type}"(supplier as Supplier, observableReducer, observableMapper)

        expect:
        mapper1(supplier()) == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | supplier  | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2           | mapper2                               | result3 | result4
        'Boolean' | {'true' } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> 'true' } | { i -> false }                        | true    | false
        'Integer' | {'4' }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> '5' }    | { i -> 2 * parseInt(i) }              | 5       | 10
        'Long'    | {'4' }    | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> '5' }    | { i -> 2 * parseLong(i) }             | 5L      | 10L
        'Float'   | {'4' }    | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> '5' }    | { i -> (2 * parseFloat(i)) as float } | 5f      | 10f
        'Double'  | {'4' }    | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> '5' }    | { i -> 2 * parseDouble(i) }           | 5d      | 10d
        'String'  | {'A' }    | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> 'D' }    | { i -> i * 3 }                        | 'DD'    | 'DDD'
    }

    def "MapTo#type then reduce list with functions and default value"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."mapTo${type}ThenReduce"(defaultValue, mapper as Function, reducer as BinaryOperator)

        expect:
        defaultValue == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | true         | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | 4            | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | 4L           | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | 4f           | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | 4d           | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "MapTo#type then reduce list with functions and supplier"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."mapTo${type}ThenReduce"(supplier as Supplier, mapper as Function, reducer as BinaryOperator)

        expect:
        supplier() == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | supplier | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | { true } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | { 4 }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | { 4L }   | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | { 4f }   | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | { 4d }   | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | { 'A' }  | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "MapTo#type then reduce list with observable functions and default value"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."mapTo${type}ThenReduce"(defaultValue, observableMapper, observableReducer)

        expect:
        defaultValue == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | defaultValue | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2                     | mapper2        | result3  | result4
        'Boolean' | true         | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> true }             | { i -> false } | true     | true
        'Integer' | 3            | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> b * 2 }            | { i -> 5 }     | 4        | 10
        'Long'    | 3L           | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> b * 2 }            | { i -> 5L }    | 4L       | 10L
        'Float'   | 3f           | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> (b * 2) as float } | { i -> 5f }    | 4f       | 10f
        'Double'  | 3d           | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> b * 2 }            | { i -> 5d }    | 4d       | 10d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> b * 3 }            | { i -> 'D' }   | 'CCCCCC' | 'DDD'
    }

    def "MapTo#type then reduce list with observable functions and supplier"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."mapTo${type}ThenReduce"(supplier as Supplier, observableMapper, observableReducer)

        expect:
        supplier() == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | supplier | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2                     | mapper2        | result3  | result4
        'Boolean' | { true } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> true }             | { i -> false } | true     | true
        'Integer' | { 3 }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> b * 2 }            | { i -> 5 }     | 4        | 10
        'Long'    | { 3L }   | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> b * 2 }            | { i -> 5L }    | 4L       | 10L
        'Float'   | { 3f }   | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> (b * 2) as float } | { i -> 5f }    | 4f       | 10f
        'Double'  | { 3d }   | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> b * 2 }            | { i -> 5d }    | 4d       | 10d
        'String'  | { 'A' }  | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> b * 3 }            | { i -> 'D' }   | 'CCCCCC' | 'DDD'
    }

    def "MapTo#type then reduce set with functions and default value"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."mapTo${type}ThenReduce"(defaultValue, mapper as Function, reducer as BinaryOperator)

        expect:
        defaultValue == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | true         | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | 3            | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | 3L           | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | 3f           | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | 3d           | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "MapTo#type then reduce set with functions and supplier"() {
        given:
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."mapTo${type}ThenReduce"(supplier as Supplier, mapper as Function, reducer as BinaryOperator)

        expect:
        supplier() == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        where:
        type      | supplier | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | { true } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | { 3 }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | { 3L }   | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | { 3f }   | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | { 3d }   | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | { 'A' }  | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "MapTo#type then reduce set with observable functions and default value"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."mapTo${type}ThenReduce"(defaultValue, observableMapper, observableReducer)

        expect:
        defaultValue == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | defaultValue | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2                     | mapper2        | result3  | result4
        'Boolean' | true         | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> true }             | { i -> false } | true     | true
        'Integer' | 3            | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> b * 2 }            | { i -> 5 }     | 4        | 10
        'Long'    | 3L           | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> b * 2 }            | { i -> 5L }    | 4L       | 10L
        'Float'   | 3f           | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> (b * 2) as float } | { i -> 5f }    | 4f       | 10f
        'Double'  | 3d           | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> b * 2 }            | { i -> 5d }    | 4d       | 10d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> b * 3 }            | { i -> 'D' }   | 'CCCCCC' | 'DDD'
    }

    def "MapTo#type then reduce set with observable functions and supplier"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableSet items = FXCollections.observableSet()
        Binding binding = items."mapTo${type}ThenReduce"(supplier as Supplier, observableMapper, observableReducer)

        expect:
        supplier() == binding.get()

        when:
        items << value1

        then:
        result1 == binding.get()

        when:
        items << value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | supplier | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2                     | mapper2        | result3  | result4
        'Boolean' | { true } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> true }             | { i -> false } | true     | true
        'Integer' | { 3 }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> b * 2 }            | { i -> 5 }     | 4        | 10
        'Long'    | { 3L }   | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> b * 2 }            | { i -> 5L }    | 4L       | 10L
        'Float'   | { 3f }   | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> (b * 2) as float } | { i -> 5f }    | 4f       | 10f
        'Double'  | { 3d }   | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> b * 2 }            | { i -> 5d }    | 4d       | 10d
        'String'  | { 'A' }  | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> b * 3 }            | { i -> 'D' }   | 'CCCCCC' | 'DDD'
    }

    def "MapTo#type then reduce map with functions and default value"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."mapTo${type}ThenReduce"(defaultValue, mapper as Function, reducer as BinaryOperator)

        expect:
        defaultValue == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | true       | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | 3          | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | 3L         | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | 3f         | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | 3d         | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | 'A'        | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "MapTo#type then reduce map with functions and supplier"() {
        given:
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."mapTo${type}ThenReduce"(supplier as Supplier, mapper as Function, reducer as BinaryOperator)

        expect:
        supplier() == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        where:
        type      | supplier | reducer       | mapper                   | value1 | value2  | result1 | result2
        'Boolean' | { true } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false
        'Integer' | { 3 }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2
        'Long'    | { 3L }   | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L
        'Float'   | { 3f }   | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f
        'Double'  | { 3d }   | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d
        'String'  | { 'A' }  | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'
    }

    def "MapTo#type then reduce map with observable functions and default value"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."mapTo${type}ThenReduce"(defaultValue, observableMapper, observableReducer)

        expect:
        defaultValue == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | defaultValue | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2                     | mapper2        | result3  | result4
        'Boolean' | true         | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> true }             | { i -> false } | true     | true
        'Integer' | 3            | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> b * 2 }            | { i -> 5 }     | 4        | 10
        'Long'    | 3L           | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> b * 2 }            | { i -> 5L }    | 4L       | 10L
        'Float'   | 3f           | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> (b * 2) as float } | { i -> 5f }    | 4f       | 10f
        'Double'  | 3d           | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> b * 2 }            | { i -> 5d }    | 4d       | 10d
        'String'  | 'A'          | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> b * 3 }            | { i -> 'D' }   | 'CCCCCC' | 'DDD'
    }

    def "MapTo#type then reduce map with observable functions and supplier"() {
        given:
        ObjectProperty observableReducer = new SimpleObjectProperty(reducer1 as BinaryOperator)
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as Function)
        ObservableMap items = FXCollections.observableHashMap()
        Binding binding = items."mapTo${type}ThenReduce"(supplier as Supplier, observableMapper, observableReducer)

        expect:
        supplier() == binding.get()

        when:
        items.key1 = value1

        then:
        result1 == binding.get()

        when:
        items.key2 = value2

        then:
        result2 == binding.get()

        when:
        observableReducer.set(reducer2 as BinaryOperator)

        then:
        result3 == binding.get()

        when:
        observableMapper.set(mapper2 as Function)

        then:
        result4 == binding.get()

        where:
        type      | supplier | reducer1      | mapper1                  | value1 | value2  | result1 | result2 | reducer2                     | mapper2        | result3  | result4
        'Boolean' | { true } | { a, b -> b } | { i -> parseBoolean(i) } | 'true' | 'false' | true    | false   | { a, b -> true }             | { i -> false } | true     | true
        'Integer' | { 3 }    | { a, b -> b } | { i -> parseInt(i) }     | '1'    | '2'     | 1       | 2       | { a, b -> b * 2 }            | { i -> 5 }     | 4        | 10
        'Long'    | { 3L }   | { a, b -> b } | { i -> parseLong(i) }    | '1'    | '2'     | 1L      | 2L      | { a, b -> b * 2 }            | { i -> 5L }    | 4L       | 10L
        'Float'   | { 3f }   | { a, b -> b } | { i -> parseFloat(i) }   | '1'    | '2'     | 1f      | 2f      | { a, b -> (b * 2) as float } | { i -> 5f }    | 4f       | 10f
        'Double'  | { 3d }   | { a, b -> b } | { i -> parseDouble(i) }  | '1'    | '2'     | 1d      | 2d      | { a, b -> b * 2 }            | { i -> 5d }    | 4d       | 10d
        'String'  | { 'A' }  | { a, b -> b } | { i -> i + i }           | 'B'    | 'C'     | 'BB'    | 'CC'    | { a, b -> b * 3 }            | { i -> 'D' }   | 'CCCCCC' | 'DDD'
    }

    @Canonical
    @Sortable
    private static class Box {
        int id
    }
}
