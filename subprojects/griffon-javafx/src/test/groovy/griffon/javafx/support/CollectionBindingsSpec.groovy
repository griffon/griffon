/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.javafx.support

import javafx.beans.binding.Binding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Supplier

import static java.lang.Double.MIN_VALUE

@Unroll
class CollectionBindingsSpec extends Specification {
    def "MinInList of #type with default value"() {
        given:
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableList<Number> items = FXCollections.observableArrayList()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableSet<Number> items = FXCollections.observableSet()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
        ObservableMap<String, Number> items = FXCollections.observableHashMap()
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
