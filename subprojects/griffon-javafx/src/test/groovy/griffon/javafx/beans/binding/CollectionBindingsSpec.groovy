/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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

import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Supplier
import java.util.function.ToDoubleFunction

import static java.lang.Double.MAX_VALUE
import static java.lang.Double.MIN_VALUE
import static java.lang.Double.parseDouble
import static javafx.collections.FXCollections.observableArrayList
import static javafx.collections.FXCollections.observableHashMap
import static javafx.collections.FXCollections.observableMap
import static javafx.collections.FXCollections.observableSet

@Unroll
class CollectionBindingsSpec extends Specification {
    def "#method with defaultValue"() {
        when:
        Binding binding = CollectionBindings."${method}"(items, defaultValue, mapper as ToDoubleFunction)

        then:
        value == binding.get()

        where:
        items                                 | method      | mapper                     | defaultValue | value
        observableArrayList()                 | 'minInList' | { parseDouble(it) } | MIN_VALUE    | MIN_VALUE
        observableArrayList('1', '2', '3')    | 'minInList' | { parseDouble(it) } | MIN_VALUE    | 1d
        observableSet()                       | 'minInSet'  | { parseDouble(it) } | MIN_VALUE    | MIN_VALUE
        observableSet('1', '2', '3')          | 'minInSet'  | { parseDouble(it) } | MIN_VALUE    | 1d
        observableHashMap()                   | 'minInMap'  | { parseDouble(it) } | MIN_VALUE    | MIN_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'minInMap'  | { parseDouble(it) } | MIN_VALUE    | 1d
        observableArrayList()                 | 'maxInList' | { parseDouble(it) } | MAX_VALUE    | MAX_VALUE
        observableArrayList('1', '2', '3')    | 'maxInList' | { parseDouble(it) } | MAX_VALUE    | 3d
        observableSet()                       | 'maxInSet'  | { parseDouble(it) } | MAX_VALUE    | MAX_VALUE
        observableSet('1', '2', '3')          | 'maxInSet'  | { parseDouble(it) } | MAX_VALUE    | 3d
        observableHashMap()                   | 'maxInMap'  | { parseDouble(it) } | MAX_VALUE    | MAX_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'maxInMap'  | { parseDouble(it) } | MAX_VALUE    | 3d
    }

    def "#method with supplier"() {
        when:
        Binding binding = CollectionBindings."${method}"(items, supplier as Supplier, mapper as ToDoubleFunction)

        then:
        value == binding.get()

        where:
        items                                 | method      | mapper                     | supplier      | value
        observableArrayList()                 | 'minInList' | { parseDouble(it) } | { MIN_VALUE } | MIN_VALUE
        observableArrayList('1', '2', '3')    | 'minInList' | { parseDouble(it) } | { MIN_VALUE } | 1d
        observableSet()                       | 'minInSet'  | { parseDouble(it) } | { MIN_VALUE } | MIN_VALUE
        observableSet('1', '2', '3')          | 'minInSet'  | { parseDouble(it) } | { MIN_VALUE } | 1d
        observableHashMap()                   | 'minInMap'  | { parseDouble(it) } | { MIN_VALUE } | MIN_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'minInMap'  | { parseDouble(it) } | { MIN_VALUE } | 1d
        observableArrayList()                 | 'maxInList' | { parseDouble(it) } | { MAX_VALUE } | MAX_VALUE
        observableArrayList('1', '2', '3')    | 'maxInList' | { parseDouble(it) } | { MAX_VALUE } | 3d
        observableSet()                       | 'maxInSet'  | { parseDouble(it) } | { MAX_VALUE } | MAX_VALUE
        observableSet('1', '2', '3')          | 'maxInSet'  | { parseDouble(it) } | { MAX_VALUE } | 3d
        observableHashMap()                   | 'maxInMap'  | { parseDouble(it) } | { MAX_VALUE } | MAX_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'maxInMap'  | { parseDouble(it) } | { MAX_VALUE } | 3d
    }

    def "#method with defaultValue an observable mapper"() {
        given:
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as ToDoubleFunction)

        when:
        Binding binding = CollectionBindings."${method}"(items, defaultValue, observableMapper)

        then:
        value1 == binding.get()

        when:
        observableMapper.set(mapper2 as ToDoubleFunction)

        then:
        value2 == binding.get()

        where:
        items                                 | method      | mapper1             | mapper2                  | defaultValue | value1    | value2
        observableArrayList()                 | 'minInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | MIN_VALUE    | MIN_VALUE | MIN_VALUE
        observableArrayList('1', '2', '3')    | 'minInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | MIN_VALUE    | 1d        | 5d
        observableSet()                       | 'minInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MIN_VALUE    | MIN_VALUE | MIN_VALUE
        observableSet('1', '2', '3')          | 'minInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MIN_VALUE    | 1d        | 5d
        observableHashMap()                   | 'minInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MIN_VALUE    | MIN_VALUE | MIN_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'minInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MIN_VALUE    | 1d        | 5d
        observableArrayList()                 | 'maxInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | MAX_VALUE    | MAX_VALUE | MAX_VALUE
        observableArrayList('1', '2', '3')    | 'maxInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | MAX_VALUE    | 3d        | 15d
        observableSet()                       | 'maxInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MAX_VALUE    | MAX_VALUE | MAX_VALUE
        observableSet('1', '2', '3')          | 'maxInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MAX_VALUE    | 3d        | 15d
        observableHashMap()                   | 'maxInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MAX_VALUE    | MAX_VALUE | MAX_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'maxInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | MAX_VALUE    | 3d        | 15d
    }

    def "#method with supplier an observable mapper"() {
        given:
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as ToDoubleFunction)

        when:
        Binding binding = CollectionBindings."${method}"(items, supplier as Supplier, observableMapper)

        then:
        value1 == binding.get()

        when:
        observableMapper.set(mapper2 as ToDoubleFunction)

        then:
        value2 == binding.get()

        where:
        items                                 | method      | mapper1             | mapper2                  | supplier      | value1    | value2
        observableArrayList()                 | 'minInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | { MIN_VALUE } | MIN_VALUE | MIN_VALUE
        observableArrayList('1', '2', '3')    | 'minInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | { MIN_VALUE } | 1d        | 5d
        observableSet()                       | 'minInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MIN_VALUE } | MIN_VALUE | MIN_VALUE
        observableSet('1', '2', '3')          | 'minInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MIN_VALUE } | 1d        | 5d
        observableHashMap()                   | 'minInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MIN_VALUE } | MIN_VALUE | MIN_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'minInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MIN_VALUE } | 1d        | 5d
        observableArrayList()                 | 'maxInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | { MAX_VALUE } | MAX_VALUE | MAX_VALUE
        observableArrayList('1', '2', '3')    | 'maxInList' | { parseDouble(it) } | { parseDouble(it) * 5d } | { MAX_VALUE } | 3d        | 15d
        observableSet()                       | 'maxInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MAX_VALUE } | MAX_VALUE | MAX_VALUE
        observableSet('1', '2', '3')          | 'maxInSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MAX_VALUE } | 3d        | 15d
        observableHashMap()                   | 'maxInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MAX_VALUE } | MAX_VALUE | MAX_VALUE
        observableMap(a: '1', b: '2', c: '3') | 'maxInMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | { MAX_VALUE } | 3d        | 15d
    }

    def "#method with mapper"() {
        when:
        Binding binding = CollectionBindings."${method}"(items, mapper as ToDoubleFunction)

        then:
        value == binding.get()

        where:
        items                                 | method      | mapper              | value
        observableArrayList()                 | 'sumOfList' | { parseDouble(it) } | 0d
        observableArrayList('1', '2', '3')    | 'sumOfList' | { parseDouble(it) } | 6d
        observableSet()                       | 'sumOfSet'  | { parseDouble(it) } | 0d
        observableSet('1', '2', '3')          | 'sumOfSet'  | { parseDouble(it) } | 6d
        observableHashMap()                   | 'sumOfMap'  | { parseDouble(it) } | 0d
        observableMap(a: '1', b: '2', c: '3') | 'sumOfMap'  | { parseDouble(it) } | 6d
    }

    def "#method with observable mapper"() {
        given:
        ObjectProperty observableMapper = new SimpleObjectProperty(mapper1 as ToDoubleFunction)

        when:
        Binding binding = CollectionBindings."${method}"(items, observableMapper)

        then:
        value1 == binding.get()

        when:
        observableMapper.set(mapper2 as ToDoubleFunction)

        then:
        value2 == binding.get()

        where:
        items                                 | method      | mapper1             | mapper2                  | value1    | value2
        observableArrayList()                 | 'sumOfList' | { parseDouble(it) } | { parseDouble(it) * 5d } | 0d        | 0d
        observableArrayList('1', '2', '3')    | 'sumOfList' | { parseDouble(it) } | { parseDouble(it) * 5d } | 6d        | 30d
        observableSet()                       | 'sumOfSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | 0d        | 0d
        observableSet('1', '2', '3')          | 'sumOfSet'  | { parseDouble(it) } | { parseDouble(it) * 5d } | 6d        | 30d
        observableHashMap()                   | 'sumOfMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | 0d        | 0d
        observableMap(a: '1', b: '2', c: '3') | 'sumOfMap'  | { parseDouble(it) } | { parseDouble(it) * 5d } | 6d        | 30d
    }

    def "MinInList of #type with default value"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.minInList(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.remove(0)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values               | result1 | result2
        'Integer' | MIN_VALUE    | [1, 2, 3, 4, 5]      | 1d      | 2d
        'Long'    | MIN_VALUE    | [1L, 2L, 3L, 4L, 5L] | 1d      | 2d
        'Float'   | MIN_VALUE    | [1f, 2f, 3f, 4f, 5f] | 1d      | 2d
        'Double'  | MIN_VALUE    | [1d, 2d, 3d, 4d, 5d] | 1d      | 2d
    }

    def "MinInList of #type with supplier"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.minInList(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.remove(0)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values               | result1 | result2
        'Integer' | { MIN_VALUE } | [1, 2, 3, 4, 5]      | 1d      | 2d
        'Long'    | { MIN_VALUE } | [1L, 2L, 3L, 4L, 5L] | 1d      | 2d
        'Float'   | { MIN_VALUE } | [1f, 2f, 3f, 4f, 5f] | 1d      | 2d
        'Double'  | { MIN_VALUE } | [1d, 2d, 3d, 4d, 5d] | 1d      | 2d
    }

    def "MaxInList of #type with default value"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.maxInList(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values               | value | result1 | result2
        'Integer' | MIN_VALUE    | [1, 2, 3, 4, 5]      | 6     | 5d      | 6d
        'Long'    | MIN_VALUE    | [1L, 2L, 3L, 4L, 5L] | 6L    | 5d      | 6d
        'Float'   | MIN_VALUE    | [1f, 2f, 3f, 4f, 5f] | 6f    | 5d      | 6d
        'Double'  | MIN_VALUE    | [1d, 2d, 3d, 4d, 5d] | 6d    | 5d      | 6d
    }

    def "MaxInList of #type with supplier"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.maxInList(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values               | value | result1 | result2
        'Integer' | { MIN_VALUE } | [1, 2, 3, 4, 5]      | 6     | 5d      | 6d
        'Long'    | { MIN_VALUE } | [1L, 2L, 3L, 4L, 5L] | 6L    | 5d      | 6d
        'Float'   | { MIN_VALUE } | [1f, 2f, 3f, 4f, 5f] | 6f    | 5d      | 6d
        'Double'  | { MIN_VALUE } | [1d, 2d, 3d, 4d, 5d] | 6d    | 5d      | 6d
    }

    def "AverageInList of #type with default value"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.averageInList(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values               | value | result1 | result2
        'Integer' | MIN_VALUE    | [1, 2, 3, 4, 5]      | 6     | 3d      | 3.5d
        'Long'    | MIN_VALUE    | [1L, 2L, 3L, 4L, 5L] | 6L    | 3d      | 3.5d
        'Float'   | MIN_VALUE    | [1f, 2f, 3f, 4f, 5f] | 6f    | 3d      | 3.5d
        'Double'  | MIN_VALUE    | [1d, 2d, 3d, 4d, 5d] | 6d    | 3d      | 3.5d
    }

    def "AverageInList of #type with supplier"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.averageInList(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values               | value | result1 | result2
        'Integer' | { MIN_VALUE } | [1, 2, 3, 4, 5]      | 6     | 3d      | 3.5d
        'Long'    | { MIN_VALUE } | [1L, 2L, 3L, 4L, 5L] | 6L    | 3d      | 3.5d
        'Float'   | { MIN_VALUE } | [1f, 2f, 3f, 4f, 5f] | 6f    | 3d      | 3.5d
        'Double'  | { MIN_VALUE } | [1d, 2d, 3d, 4d, 5d] | 6d    | 3d      | 3.5d
    }

    def "SumOfList of #type"() {
        given:
        ObservableList<Number> items = observableArrayList()
        Binding binding = CollectionBindings.sumOfList(items)

        expect:
        0d == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | values               | value | result1 | result2
        'Integer' | [1, 2, 3, 4, 5]      | 6     | 15d     | 21d
        'Long'    | [1L, 2L, 3L, 4L, 5L] | 6L    | 15d     | 21d
        'Float'   | [1f, 2f, 3f, 4f, 5f] | 6f    | 15d     | 21d
        'Double'  | [1d, 2d, 3d, 4d, 5d] | 6d    | 15d     | 21d
    }

    def "MinInSet of #type with default value"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.minInSet(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.remove(value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values               | value | result1 | result2
        'Integer' | MIN_VALUE    | [1, 2, 3, 4, 5]      | 1     | 1d      | 2d
        'Long'    | MIN_VALUE    | [1L, 2L, 3L, 4L, 5L] | 1L    | 1d      | 2d
        'Float'   | MIN_VALUE    | [1f, 2f, 3f, 4f, 5f] | 1f    | 1d      | 2d
        'Double'  | MIN_VALUE    | [1d, 2d, 3d, 4d, 5d] | 1d    | 1d      | 2d
    }

    def "MinInSet of #type with supplier"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.minInSet(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.remove(value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values               | value | result1 | result2
        'Integer' | { MIN_VALUE } | [1, 2, 3, 4, 5]      | 1     | 1d      | 2d
        'Long'    | { MIN_VALUE } | [1L, 2L, 3L, 4L, 5L] | 1L    | 1d      | 2d
        'Float'   | { MIN_VALUE } | [1f, 2f, 3f, 4f, 5f] | 1f    | 1d      | 2d
        'Double'  | { MIN_VALUE } | [1d, 2d, 3d, 4d, 5d] | 1d    | 1d      | 2d
    }

    def "MaxInSet of #type with default value"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.maxInSet(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values               | value | result1 | result2
        'Integer' | MIN_VALUE    | [1, 2, 3, 4, 5]      | 6     | 5d      | 6d
        'Long'    | MIN_VALUE    | [1L, 2L, 3L, 4L, 5L] | 6L    | 5d      | 6d
        'Float'   | MIN_VALUE    | [1f, 2f, 3f, 4f, 5f] | 6f    | 5d      | 6d
        'Double'  | MIN_VALUE    | [1d, 2d, 3d, 4d, 5d] | 6d    | 5d      | 6d
    }

    def "MaxInSet of #type with supplier"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.maxInSet(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values               | value | result1 | result2
        'Integer' | { MIN_VALUE } | [1, 2, 3, 4, 5]      | 6     | 5d      | 6d
        'Long'    | { MIN_VALUE } | [1L, 2L, 3L, 4L, 5L] | 6L    | 5d      | 6d
        'Float'   | { MIN_VALUE } | [1f, 2f, 3f, 4f, 5f] | 6f    | 5d      | 6d
        'Double'  | { MIN_VALUE } | [1d, 2d, 3d, 4d, 5d] | 6d    | 5d      | 6d
    }

    def "AverageInSet of #type with default value"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.averageInSet(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values               | value | result1 | result2
        'Integer' | MIN_VALUE    | [1, 2, 3, 4, 5]      | 6     | 3d      | 3.5d
        'Long'    | MIN_VALUE    | [1L, 2L, 3L, 4L, 5L] | 6L    | 3d      | 3.5d
        'Float'   | MIN_VALUE    | [1f, 2f, 3f, 4f, 5f] | 6f    | 3d      | 3.5d
        'Double'  | MIN_VALUE    | [1d, 2d, 3d, 4d, 5d] | 6d    | 3d      | 3.5d
    }

    def "AverageInSet of #type with supplier"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.averageInSet(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values               | value | result1 | result2
        'Integer' | { MIN_VALUE } | [1, 2, 3, 4, 5]      | 6     | 3d      | 3.5d
        'Long'    | { MIN_VALUE } | [1L, 2L, 3L, 4L, 5L] | 6L    | 3d      | 3.5d
        'Float'   | { MIN_VALUE } | [1f, 2f, 3f, 4f, 5f] | 6f    | 3d      | 3.5d
        'Double'  | { MIN_VALUE } | [1d, 2d, 3d, 4d, 5d] | 6d    | 3d      | 3.5d
    }

    def "SumOfSet of #type"() {
        given:
        ObservableSet<Number> items = observableSet()
        Binding binding = CollectionBindings.sumOfSet(items)

        expect:
        0d == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        items.add(value)

        then:
        result2 == binding.get()

        where:
        type      | values               | value | result1 | result2
        'Integer' | [1, 2, 3, 4, 5]      | 6     | 15d     | 21d
        'Long'    | [1L, 2L, 3L, 4L, 5L] | 6L    | 15d     | 21d
        'Float'   | [1f, 2f, 3f, 4f, 5f] | 6f    | 15d     | 21d
        'Double'  | [1d, 2d, 3d, 4d, 5d] | 6d    | 15d     | 21d
    }

    def "MinInMap of #type with default value"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.minInMap(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.remove('key1')

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values                      | result1 | result2
        'Integer' | MIN_VALUE    | toMap([1, 2, 3, 4, 5])      | 1d      | 2d
        'Long'    | MIN_VALUE    | toMap([1L, 2L, 3L, 4L, 5L]) | 1d      | 2d
        'Float'   | MIN_VALUE    | toMap([1f, 2f, 3f, 4f, 5f]) | 1d      | 2d
        'Double'  | MIN_VALUE    | toMap([1d, 2d, 3d, 4d, 5d]) | 1d      | 2d
    }

    def "MinInMap of #type with supplier"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.minInMap(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.remove('key1')

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values                      | result1 | result2
        'Integer' | { MIN_VALUE } | toMap([1, 2, 3, 4, 5])      | 1d      | 2d
        'Long'    | { MIN_VALUE } | toMap([1L, 2L, 3L, 4L, 5L]) | 1d      | 2d
        'Float'   | { MIN_VALUE } | toMap([1f, 2f, 3f, 4f, 5f]) | 1d      | 2d
        'Double'  | { MIN_VALUE } | toMap([1d, 2d, 3d, 4d, 5d]) | 1d      | 2d
    }

    def "MaxInMap of #type with default value"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.maxInMap(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.put('key', value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values                      | value | result1 | result2
        'Integer' | MIN_VALUE    | toMap([1, 2, 3, 4, 5])      | 6     | 5d      | 6d
        'Long'    | MIN_VALUE    | toMap([1L, 2L, 3L, 4L, 5L]) | 6L    | 5d      | 6d
        'Float'   | MIN_VALUE    | toMap([1f, 2f, 3f, 4f, 5f]) | 6f    | 5d      | 6d
        'Double'  | MIN_VALUE    | toMap([1d, 2d, 3d, 4d, 5d]) | 6d    | 5d      | 6d
    }

    def "MaxInMap of #type with supplier"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.maxInMap(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.put('key', value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values                      | value | result1 | result2
        'Integer' | { MIN_VALUE } | toMap([1, 2, 3, 4, 5])      | 6     | 5d      | 6d
        'Long'    | { MIN_VALUE } | toMap([1L, 2L, 3L, 4L, 5L]) | 6L    | 5d      | 6d
        'Float'   | { MIN_VALUE } | toMap([1f, 2f, 3f, 4f, 5f]) | 6f    | 5d      | 6d
        'Double'  | { MIN_VALUE } | toMap([1d, 2d, 3d, 4d, 5d]) | 6d    | 5d      | 6d
    }

    def "AverageInMap of #type with default value"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.averageInMap(items, defaultValue)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.put('key', value)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | values                      | value | result1 | result2
        'Integer' | MIN_VALUE    | toMap([1, 2, 3, 4, 5])      | 6     | 3d      | 3.5d
        'Long'    | MIN_VALUE    | toMap([1L, 2L, 3L, 4L, 5L]) | 6L    | 3d      | 3.5d
        'Float'   | MIN_VALUE    | toMap([1f, 2f, 3f, 4f, 5f]) | 6f    | 3d      | 3.5d
        'Double'  | MIN_VALUE    | toMap([1d, 2d, 3d, 4d, 5d]) | 6d    | 3d      | 3.5d
    }

    def "AverageInMap of #type with supplier"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.averageInMap(items, supplier as Supplier)

        expect:
        supplier() == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.put('key', value)

        then:
        result2 == binding.get()

        where:
        type      | supplier      | values                      | value | result1 | result2
        'Integer' | { MIN_VALUE } | toMap([1, 2, 3, 4, 5])      | 6     | 3d      | 3.5d
        'Long'    | { MIN_VALUE } | toMap([1L, 2L, 3L, 4L, 5L]) | 6L    | 3d      | 3.5d
        'Float'   | { MIN_VALUE } | toMap([1f, 2f, 3f, 4f, 5f]) | 6f    | 3d      | 3.5d
        'Double'  | { MIN_VALUE } | toMap([1d, 2d, 3d, 4d, 5d]) | 6d    | 3d      | 3.5d
    }

    def "SumOfMap of #type"() {
        given:
        ObservableMap<String, Number> items = observableHashMap()
        Binding binding = CollectionBindings.sumOfMap(items)

        expect:
        0d == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        items.put('key', value)

        then:
        result2 == binding.get()

        where:
        type      | values                      | value | result1 | result2
        'Integer' | toMap([1, 2, 3, 4, 5])      | 6     | 15d     | 21d
        'Long'    | toMap([1L, 2L, 3L, 4L, 5L]) | 6L    | 15d     | 21d
        'Float'   | toMap([1f, 2f, 3f, 4f, 5f]) | 6f    | 15d     | 21d
        'Double'  | toMap([1d, 2d, 3d, 4d, 5d]) | 6d    | 15d     | 21d
    }

    private static toMap(list) {
        Map m = [:]
        list.each { m.put("key${it.toInteger()}".toString(), it) }
        m
    }
}
