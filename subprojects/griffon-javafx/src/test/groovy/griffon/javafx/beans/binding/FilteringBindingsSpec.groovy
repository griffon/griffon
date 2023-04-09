/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

@Unroll
class FilteringBindingsSpec extends Specification {
    def "Filter then findFirst in list with default value"() {
        given:
        Box def_value_01 = new Box(6)
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = FilteringBindings.filterThenFindFirst(items, def_value_01, filter)

        expect:
        def_value_01 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in list with supplier"() {
        given:
        Supplier<Box> supplier = { new Box(6) } as Supplier
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenFindFirst(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in list with default value (observable)"() {
        given:
        Box def_value_02 = new Box(6)
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> predicate = { it.id % 2 == 0 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenFindFirst(items, def_value_02, filter)

        expect:
        def_value_02 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()

        when:
        filter.set({ it.id % 2 != 0 } as Predicate)

        then:
        new Box(1) == binding.get()
    }

    def "Filter then findFirst in list with supplier (observable)"() {
        given:
        Supplier<Box> supplier = { new Box(6) } as Supplier
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> predicate = { it.id % 2 == 0 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenFindFirst(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()

        when:
        filter.set({ it.id % 2 != 0 } as Predicate)

        then:
        new Box(1) == binding.get()
    }

    def "Filter then findFirst #type in list with default value"(String type, Object def_value_03, Predicate predicate, List values, Object result) {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, def_value_03, predicate)

        expect:
        def_value_03 == binding.get()

        when:
        items.addAll(values)

        then:
        result == binding.get()

        where:
        type      | def_value_03 | predicate                   | values                    | result
        'Boolean' | true         | { it }                      | [false, true, false]      | true
        'Integer' | 6            | { it % 2 == 0 }             | [1, 2, 3, 4, 5]           | 2
        'Long'    | 6L           | { it % 2 == 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L
        'Float'   | 6f           | { it % 2 == 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f
        'Double'  | 6d           | { it % 2 == 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d
        'String'  | '6'          | { it.toInteger() % 2 == 0 } | ['1', '2', '3', '4', '5'] | '2'
    }

    def "Filter then findFirst #type in list with supplier"(String type, Supplier supplier, Predicate predicate, List values, Object result) {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, supplier, predicate)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll(values)

        then:
        result == binding.get()

        where:
        type      | supplier | predicate                   | values                    | result
        'Boolean' | { true } | { it }                      | [false, true, false]      | true
        'Integer' | { 6 }    | { it % 2 == 0 }             | [1, 2, 3, 4, 5]           | 2
        'Long'    | { 6L }   | { it % 2 == 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L
        'Float'   | { 6f }   | { it % 2 == 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f
        'Double'  | { 6d }   | { it % 2 == 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d
        'String'  | { '6' }  | { it.toInteger() % 2 == 0 } | ['1', '2', '3', '4', '5'] | '2'
    }

    def "Filter then findFirst #type in list with default value (observables)"(String type, Object def_value_04, Predicate predicate1, Predicate predicate2, List values, Object result1, Object result2) {
        given:
        ObservableList items = FXCollections.observableArrayList()
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, def_value_04, filter)

        expect:
        def_value_04 == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        where:
        type      | def_value_04 | predicate1                 | predicate2                  | values                    | result1 | result2
        'Boolean' | true         | { it }                     | { !it }                     | [false, true, false]      | true    | false
        'Integer' | 6            | { it % 2 == 0 }            | { it % 2 != 0 }             | [1, 2, 3, 4, 5]           | 2       | 1
        'Long'    | 6L           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L      | 1L
        'Float'   | 6f           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f      | 1f
        'Double'  | 6d           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d      | 1d
        'String'  | '6'          | { it.toInteger() % 2 == 0 }| { it.toInteger() % 2 != 0 } | ['1', '2', '3', '4', '5'] | '2'     | '1'
    }

    def "Filter then findFirst #type in list with supplier (observables)"(String type, Supplier supplier, Predicate predicate1, Predicate predicate2, List values, Object result1, Object result2) {
        given:
        ObservableList items = FXCollections.observableArrayList()
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        where:
        type      | supplier | predicate1                  | predicate2                 | values                    | result1 | result2
        'Boolean' | { true } | { it }                      | { !it }                    | [false, true, false]      | true    | false
        'Integer' | { 6 }    | { it % 2 == 0 }             | { it % 2 != 0 }            | [1, 2, 3, 4, 5]           | 2       | 1
        'Long'    | { 6L }   | { it % 2 == 0 }             | { it % 2 != 0 }            | [1L, 2L, 3L, 4L, 5L]      | 2L      | 1L
        'Float'   | { 6f }   | { it % 2 == 0 }             | { it % 2 != 0 }            | [1f, 2f, 3f, 4f, 5f]      | 2f      | 1f
        'Double'  | { 6d }   | { it % 2 == 0 }             | { it % 2 != 0 }            | [1d, 2d, 3d, 4d, 5d]      | 2d      | 1d
        'String'  | { '6' }  | { it.toInteger() % 2 == 0 } | { it.toInteger() % 2 != 0 }| ['1', '2', '3', '4', '5'] | '2'     | '1'
    }

    def "Filter then findFirst in set with default value"() {
        given:
        Box def_value_05 = new Box(6)
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenFindFirst(items, def_value_05, filter)

        expect:
        def_value_05 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in set with supplier"() {
        given:
        Supplier<Box> supplier = { new Box(6) } as Supplier
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenFindFirst(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in set with default value (observable)"() {
        given:
        Box def_value_06 = new Box(6)
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> predicate = { it.id % 2 == 0 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenFindFirst(items, def_value_06, filter)

        expect:
        def_value_06 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()

        when:
        filter.set({ it.id % 2 != 0 } as Predicate)

        then:
        new Box(1) == binding.get()
    }

    def "Filter then findFirst in set with supplier (observable)"() {
        given:
        Supplier<Box> supplier = { new Box(6) } as Supplier
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> predicate = { it.id % 2 == 0 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenFindFirst(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()

        when:
        filter.set({ it.id % 2 != 0 } as Predicate)

        then:
        new Box(1) == binding.get()
    }

    def "Filter then findFirst #type in set with default value"(String type, Object def_value_07, Predicate predicate, List values, Object result) {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, def_value_07, predicate)

        expect:
        def_value_07 == binding.get()

        when:
        items.addAll(values)

        then:
        result == binding.get()

        where:
        type      | def_value_07 | predicate                   | values                    | result
        'Boolean' | true         | { it }                      | [false, true, false]      | true
        'Integer' | 6            | { it % 2 == 0 }             | [1, 2, 3, 4, 5]           | 2
        'Long'    | 6L           | { it % 2 == 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L
        'Float'   | 6f           | { it % 2 == 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f
        'Double'  | 6d           | { it % 2 == 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d
        'String'  | '6'          | { it.toInteger() % 2 == 0 } | ['1', '2', '3', '4', '5'] | '2'
    }

    def "Filter then findFirst #type in set with supplier"(String type, Supplier supplier, Predicate predicate, List values, Object result) {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, supplier, predicate)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll(values)

        then:
        result == binding.get()

        where:
        type      | supplier | predicate                   | values                    | result
        'Boolean' | { true } | { it }                      | [false, true, false]      | true
        'Integer' | { 6 }    | { it % 2 == 0 }             | [1, 2, 3, 4, 5]           | 2
        'Long'    | { 6L }   | { it % 2 == 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L
        'Float'   | { 6f }   | { it % 2 == 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f
        'Double'  | { 6d }   | { it % 2 == 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d
        'String'  | { '6' }  | { it.toInteger() % 2 == 0 } | ['1', '2', '3', '4', '5'] | '2'
    }

    def "Filter then findFirst #type in set with default value (observables)"(String type, Object def_value_08, Predicate predicate1, Predicate predicate2, List values, Object result1, Object result2) {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, def_value_08, filter)

        expect:
        def_value_08 == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        where:
        type      | def_value_08 | predicate1                 | predicate2                  | values                    | result1 | result2
        'Boolean' | true         | { it }                     | { !it }                     | [false, true, false]      | true    | false
        'Integer' | 6            | { it % 2 == 0 }            | { it % 2 != 0 }             | [1, 2, 3, 4, 5]           | 2       | 1
        'Long'    | 6L           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L      | 1L
        'Float'   | 6f           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f      | 1f
        'Double'  | 6d           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d      | 1d
        'String'  | '6'          | { it.toInteger() % 2 == 0 }| { it.toInteger() % 2 != 0 } | ['1', '2', '3', '4', '5'] | '2'     | '1'
    }

    def "Filter then findFirst #type in set with supplier (observables)"(String type, Supplier supplier, Predicate predicate1, Predicate predicate2, List values, Object result1, Object result2) {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        where:
        type      | supplier | predicate1                  | predicate2                 | values                    | result1 | result2
        'Boolean' | { true } | { it }                      | { !it }                    | [false, true, false]      | true    | false
        'Integer' | { 6 }    | { it % 2 == 0 }             | { it % 2 != 0 }            | [1, 2, 3, 4, 5]           | 2       | 1
        'Long'    | { 6L }   | { it % 2 == 0 }             | { it % 2 != 0 }            | [1L, 2L, 3L, 4L, 5L]      | 2L      | 1L
        'Float'   | { 6f }   | { it % 2 == 0 }             | { it % 2 != 0 }            | [1f, 2f, 3f, 4f, 5f]      | 2f      | 1f
        'Double'  | { 6d }   | { it % 2 == 0 }             | { it % 2 != 0 }            | [1d, 2d, 3d, 4d, 5d]      | 2d      | 1d
        'String'  | { '6' }  | { it.toInteger() % 2 == 0 } | { it.toInteger() % 2 != 0 }| ['1', '2', '3', '4', '5'] | '2'     | '1'
    }

    def "Filter then findFirst in map with default value"() {
        given:
        Box def_value_09 = new Box(6)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenFindFirst(items, def_value_09, filter)

        expect:
        def_value_09 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in map with supplier"() {
        given:
        Supplier<Box> supplier = { new Box(6) } as Supplier
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenFindFirst(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in map with default value (observable)"() {
        given:
        Box def_value_10 = new Box(6)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> predicate = { it.id % 2 == 0 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenFindFirst(items, def_value_10, filter)

        expect:
        def_value_10 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        new Box(2) == binding.get()

        when:
        filter.set({ it.id % 2 != 0 } as Predicate)

        then:
        new Box(1) == binding.get()
    }

    def "Filter then findFirst in map with supplier (observable)"() {
        given:
        Supplier<Box> supplier = { new Box(6) } as Supplier
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> predicate = { it.id % 2 == 0 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenFindFirst(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        new Box(2) == binding.get()

        when:
        filter.set({ it.id % 2 != 0 } as Predicate)

        then:
        new Box(1) == binding.get()
    }

    def "Filter then findFirst #type in map with default value"(String type, Object def_value_11, Predicate predicate, Map values, Object result) {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, def_value_11, predicate)

        expect:
        def_value_11 == binding.get()

        when:
        items.putAll(values)

        then:
        result == binding.get()

        where:
        type      | def_value_11 | predicate                   | values                           | result
        'Boolean' | true         | { it }                      | toMap([false, true, false])      | true
        'Integer' | 6            | { it % 2 == 0 }             | toMap([1, 2, 3, 4, 5])           | 2
        'Long'    | 6L           | { it % 2 == 0 }             | toMap([1L, 2L, 3L, 4L, 5L])      | 2L
        'Float'   | 6f           | { it % 2 == 0 }             | toMap([1f, 2f, 3f, 4f, 5f])      | 2f
        'Double'  | 6d           | { it % 2 == 0 }             | toMap([1d, 2d, 3d, 4d, 5d])      | 2d
        'String'  | '6'          | { it.toInteger() % 2 == 0 } | toMap(['1', '2', '3', '4', '5']) | '2'
    }

    def "Filter then findFirst #type in map with supplier"(String type, Supplier supplier, Predicate predicate, Map values, Object result) {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, supplier, predicate)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(values)

        then:
        result == binding.get()

        where:
        type      | supplier | predicate                   | values                           | result
        'Boolean' | { true } | { it }                      | toMap([false, true, false])      | true
        'Integer' | { 6 }    | { it % 2 == 0 }             | toMap([1, 2, 3, 4, 5])           | 2
        'Long'    | { 6L }   | { it % 2 == 0 }             | toMap([1L, 2L, 3L, 4L, 5L])      | 2L
        'Float'   | { 6f }   | { it % 2 == 0 }             | toMap([1f, 2f, 3f, 4f, 5f])      | 2f
        'Double'  | { 6d }   | { it % 2 == 0 }             | toMap([1d, 2d, 3d, 4d, 5d])      | 2d
        'String'  | { '6' }  | { it.toInteger() % 2 == 0 } | toMap(['1', '2', '3', '4', '5']) | '2'
    }

    def "Filter then findFirst #type in map with default value (observables)"(String type, Object def_value_12, Predicate predicate1, Predicate predicate2, Map values, Object result1, Object result2) {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, def_value_12, filter)

        expect:
        def_value_12 == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        where:
        type      | def_value_12 | predicate1                 | predicate2                  | values                           | result1 | result2
        'Boolean' | true         | { it }                     | { !it }                     | toMap([false, true, false])      | true    | false
        'Integer' | 6            | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1, 2, 3, 4, 5])           | 2       | 1
        'Long'    | 6L           | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1L, 2L, 3L, 4L, 5L])      | 2L      | 1L
        'Float'   | 6f           | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1f, 2f, 3f, 4f, 5f])      | 2f      | 1f
        'Double'  | 6d           | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1d, 2d, 3d, 4d, 5d])      | 2d      | 1d
        'String'  | '6'          | { it.toInteger() % 2 == 0 }| { it.toInteger() % 2 != 0 } | toMap(['1', '2', '3', '4', '5']) | '2'     | '1'
    }

    def "Filter then findFirst #type in map with supplier (observables)"(String type, Supplier supplier, Predicate predicate1, Predicate predicate2, Map values, Object result1, Object result2) {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenFindFirst${type}"(items, supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        where:
        type      | supplier | predicate1                  | predicate2                 | values                           | result1 | result2
        'Boolean' | { true } | { it }                      | { !it }                    | toMap([false, true, false] )     | true    | false
        'Integer' | { 6 }    | { it % 2 == 0 }             | { it % 2 != 0 }            | toMap([1, 2, 3, 4, 5] )          | 2       | 1
        'Long'    | { 6L }   | { it % 2 == 0 }             | { it % 2 != 0 }            | toMap([1L, 2L, 3L, 4L, 5L])      | 2L      | 1L
        'Float'   | { 6f }   | { it % 2 == 0 }             | { it % 2 != 0 }            | toMap([1f, 2f, 3f, 4f, 5f])      | 2f      | 1f
        'Double'  | { 6d }   | { it % 2 == 0 }             | { it % 2 != 0 }            | toMap([1d, 2d, 3d, 4d, 5d])      | 2d      | 1d
        'String'  | { '6' }  | { it.toInteger() % 2 == 0 } | { it.toInteger() % 2 != 0 }| toMap(['1', '2', '3', '4', '5']) | '2'     | '1'
    }

    def "Map then filter then findFirst in list with default value"() {
        given:
        Integer def_value_13 = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Integer> filter = { it % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, def_value_13, mapper, filter)

        expect:
        def_value_13 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in list with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Integer> filter = { it % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in list with default value (observable)"() {
        given:
        Integer def_value_14 = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 } as Predicate
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, def_value_14, mapper, filter)

        expect:
        def_value_14 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()

        when:
        filter.set({ it % 2 == 0 } as Predicate)

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in list with supplier (observable)"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 } as Predicate
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()

        when:
        filter.set({ it % 2 == 0 } as Predicate)

        then:
        2 == binding.get()
    }

    def "Map then findFirst #type in list with default value"(String type, Object def_value_15, Function mapper_15, Predicate predicate_15, Object result) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, def_value_15, mapper_15, predicate_15)

        expect:
        def_value_15 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | def_value_15 | mapper_15            | predicate_15                | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | 6            | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | 6L           | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | 6f           | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | 6d           | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | '6'          | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in list with supplier"(String type, Supplier supplier_x01, Function mapper_x01, Predicate predicate_x01, Object result) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, supplier_x01, mapper_x01, predicate_x01)

        expect:
        supplier_x01.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier_x01 | mapper_x01           | predicate_x01               | result
        'Boolean' | { true }     | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | { 6 }        | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | { 6L }       | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | { 6f }       | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | { 6d }       | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | { '6' }      | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in list with default value (observables)"(String type, Object def_value_16, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, def_value_16, mapper, filter)

        expect:
        def_value_16 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result3 == binding.get()

        where:
        type      | def_value_16 | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | 6            | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then findFirst #type in list with supplier (observables)"(String type, Supplier supplier, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result3 == binding.get()

        where:
        type      | supplier | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | { 6 }    | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | { 6L }   | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | { 6f }   | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | { 6d }   | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        'String'  | { '6' }  | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then filter then findFirst in set with default value"() {
        given:
        Integer def_value_17 = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Integer> filter = { it % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, def_value_17, mapper, filter)

        expect:
        def_value_17 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in set with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Integer> filter = { it % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in set with default value (observable)"() {
        given:
        Integer def_value_01 = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 } as Predicate
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, def_value_01, mapper, filter)

        expect:
        def_value_01 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()

        when:
        filter.set({ it % 2 == 0 } as Predicate)

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in set with supplier (observable)"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 } as Predicate
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()

        when:
        filter.set({ it % 2 == 0 } as Predicate)

        then:
        2 == binding.get()
    }

    def "Map then findFirst #type in set with default value"(String type, Object def_value_18, Function mapper_18, Predicate predicate_18, Object result) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, def_value_18, mapper_18, predicate_18)

        expect:
        def_value_18 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | def_value_18 | mapper_18            | predicate_18                | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | 6            | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | 6L           | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | 6f           | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | 6d           | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | '6'          | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in set with supplier"(String type, Supplier supplier_x02, Function mapper_x02, Predicate predicate_x02, Object result) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, supplier_x02, mapper_x02, predicate_x02)

        expect:
        supplier_x02.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier_x02 | mapper_x02           | predicate_x02               | result
        'Boolean' | { true }     | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | { 6 }        | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | { 6L }       | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | { 6f }       | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | { 6d }       | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | { '6' }      | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in set with default value (observables)"(String type, Object def_value_19, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, def_value_19, mapper, filter)

        expect:
        def_value_19 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result3 == binding.get()

        where:
        type      | def_value_19 | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | 6            | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        // 'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then findFirst #type in set with supplier (observables)"(String type, Supplier supplier, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result3 == binding.get()

        where:
        type      | supplier | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | { 6 }    | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | { 6L }   | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | { 6f }   | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | { 6d }   | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        'String'  | { '6' }  | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then filter then findFirst in map with default value"() {
        given:
        Integer def_value_20 = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Integer> filter = { it % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, def_value_20, mapper, filter)

        expect:
        def_value_20 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in map with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Integer> filter = { it % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in map with default value (observable)"() {
        given:
        Integer def_value_21 = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 } as Predicate
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, def_value_21, mapper, filter)

        expect:
        def_value_21 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        3 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()

        when:
        filter.set({ it % 2 == 0 } as Predicate)

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in map with supplier (observable)"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 } as Predicate
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.mapThenFilterThenFindFirst(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        3 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()

        when:
        filter.set({ it % 2 == 0 } as Predicate)

        then:
        2 == binding.get()
    }

    def "Map then findFirst #type in map with default value"(String type, Object def_value_22, Function mapper_22, Predicate predicate_22, Object result) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, def_value_22, mapper_22, predicate_22)

        expect:
        def_value_22 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | def_value_22 | mapper_22            | predicate_22                | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | 6            | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | 6L           | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | 6f           | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | 6d           | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | '6'          | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in map with supplier"(String type, Supplier supplier_x03, Function mapper_x03, Predicate predicate_x03, Object result) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, supplier_x03, mapper_x03, predicate_x03)

        expect:
        supplier_x03.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | supplier_x03 | mapper_x03           | predicate_x03               | result
        'Boolean' | { true }     | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | { 6 }        | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | { 6L }       | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | { 6f }       | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | { 6d }       | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | { '6' }      | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in map with default value (observables)"(String type, Object def_value_23, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, def_value_23, mapper, filter)

        expect:
        def_value_23 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        mapper.set(function2)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result3 == binding.get()

        where:
        type      | def_value_23 | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | 6            | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then findFirst #type in map with supplier (observables)"(String type, Supplier supplier, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."mapTo${type}ThenFilterThenFindFirst"(items, supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        mapper.set(function2)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result3 == binding.get()

        where:
        type      | supplier | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | { 6 }    | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | { 6L }   | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | { 6f }   | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | { 6d }   | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        'String'  | { '6' }  | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Filter then map then findFirst in list with default value"() {
        given:
        Integer def_value_24 = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, def_value_24, filter, mapper)

        expect:
        def_value_24 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then findFirst in list with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then filter then findFirst in list with default value (observable)"() {
        given:
        Integer def_value_25 = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, def_value_25, filter, mapper)

        expect:
        def_value_25 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        filter.set({ it.id % 2 == 0 } as Predicate)

        then:
        2 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()
    }

    def "Filter then map then filter then findFirst in list with supplier (observable)"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        filter.set({ it.id % 2 == 0 } as Predicate)

        then:
        2 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()
    }

    def "Filter then map then findFirst #type in list with default value"(String type, Object def_value_26, Function mapper_26, Predicate filter_26, Object result) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, def_value_26, filter_26, mapper_26)

        expect:
        def_value_26 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | def_value_26 | mapper_26            | filter_26          | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | 6            | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | 6L           | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | 6f           | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | 6d           | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | '6'          | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in list with supplier"(String type, Supplier supplier_x04, Function mapper_x04, Predicate filter_x04, Object result) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, supplier_x04, filter_x04, mapper_x04)

        expect:
        supplier_x04.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier_x04 | mapper_x04           | filter_x04         | result
        'Boolean' | { true }     | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | { 6 }        | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | { 6L }       | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | { 6f }       | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | { 6d }       | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | { '6' }      | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in list with default value (observables)"(String type, Object def_value_27, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, def_value_27, filter, mapper)

        expect:
        def_value_27 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2)

        then:
        result3 == binding.get()

        where:
        type      | def_value_27 | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | 6            | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst #type in list with supplier (observables)"(String type, Supplier supplier, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2)

        then:
        result3 == binding.get()

        where:
        type      | supplier | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | { 6 }    | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | { 6L }   | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | { 6f }   | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | { 6d }   | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | { '6' }  | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst in set with default value"() {
        given:
        Integer def_value_28 = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, def_value_28, filter, mapper)

        expect:
        def_value_28 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then findFirst in set with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then filter then findFirst in set with default value (observable)"() {
        given:
        Integer def_value_29 = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, def_value_29, filter, mapper)

        expect:
        def_value_29 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        filter.set({ it.id % 2 == 0 } as Predicate)

        then:
        2 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()
    }

    def "Filter then map then filter then findFirst in set with supplier (observable)"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        3 == binding.get()

        when:
        filter.set({ it.id % 2 == 0 } as Predicate)

        then:
        2 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()
    }

    def "Filter then map then findFirst #type in set with default value"(String type, Object def_value_30, Function mapper_30, Predicate filter_30, Object result) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, def_value_30, filter_30, mapper_30)

        expect:
        def_value_30 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | def_value_30 | mapper_30            | filter_30          | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | 6            | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | 6L           | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | 6f           | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | 6d           | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | '6'          | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in set with supplier"(String type, Supplier supplier_x05, Function mapper_x05, Predicate filter_x05, Object result) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, supplier_x05, filter_x05, mapper_x05)

        expect:
        supplier_x05.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier_x05 | mapper_x05           | filter_x05         | result
        'Boolean' | { true }     | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | { 6 }        | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | { 6L }       | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | { 6f }       | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | { 6d }       | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | { '6' }      | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in set with default value (observables)"(String type, Object def_value_31, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, def_value_31, filter, mapper)

        expect:
        def_value_31 == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2)

        then:
        result3 == binding.get()

        where:
        type      | def_value_31 | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | 6            | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst #type in set with supplier (observables)"(String type, Supplier supplier, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2)

        then:
        result3 == binding.get()

        where:
        type      | supplier | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | { 6 }    | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | { 6L }   | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | { 6f }   | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | { 6d }   | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | { '6' }  | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst in map with default value"() {
        given:
        Integer def_value_32 = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, def_value_32, filter, mapper)

        expect:
        def_value_32 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Filter then map then findFirst in map with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id } as Function
        Predicate<Box> filter = { it.id % 2 == 0 } as Predicate
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Filter then map then filter then findFirst in map with default value (observable)"() {
        given:
        Integer def_value_33 = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, def_value_33, filter, mapper)

        expect:
        def_value_33 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        3 == binding.get()

        when:
        filter.set({ it.id % 2 == 0 } as Predicate)

        then:
        2 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()
    }

    def "Filter then map then filter then findFirst in map with supplier (observable)"() {
        given:
        Supplier<Integer> supplier = { 6 } as Supplier
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id } as Function
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 } as Predicate
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = FilteringBindings.filterThenMapThenFindFirst(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        3 == binding.get()

        when:
        filter.set({ it.id % 2 == 0 } as Predicate)

        then:
        2 == binding.get()

        when:
        mapper.set({ it.id * 2 } as Function)

        then:
        4 == binding.get()
    }

    def "Filter then map then findFirst #type in map with default value"(String type, Object def_value_34, Function mapper_34, Predicate filter_34, Object result) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, def_value_34, filter_34, mapper_34)

        expect:
        def_value_34 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | def_value_34 | mapper_34            | filter_34          | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | 6            | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | 6L           | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | 6f           | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | 6d           | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | '6'          | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in map with supplier"(String type, Supplier supplier_x06, Function mapper_x06, Predicate filter_x06, Object result) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, supplier_x06, filter_x06, mapper_x06)

        expect:
        supplier_x06.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | supplier_x06 | mapper_x06           | filter_x06         | result
        'Boolean' | { true }     | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | { 6 }        | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | { 6L }       | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | { 6f }       | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | { 6d }       | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | { '6' }      | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in map with default value (observables)"(String type, Object def_value_35, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, def_value_35, filter, mapper)

        expect:
        def_value_35 == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2)

        then:
        result3 == binding.get()

        where:
        type      | def_value_35 | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | 6            | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst #type in map with supplier (observables)"(String type, Supplier supplier, Function function1, Function function2, Predicate predicate1, Predicate predicate2, Object result1, Object result2, Object result3) {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1)
        Binding binding = FilteringBindings."filterThenMapTo${type}ThenFindFirst"(items, supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2)

        then:
        result3 == binding.get()

        where:
        type      | supplier | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | { 6 }    | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | { 6L }   | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | { 6f }   | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | { 6d }   | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | { '6' }  | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    @Canonical
    @Sortable
    private static class Box {
        int id
    }

    private static toMap(list) {
        Map m = [:]
        list.eachWithIndex { e, i -> m.put("key${i}".toString(), e) }
        m
    }
}
