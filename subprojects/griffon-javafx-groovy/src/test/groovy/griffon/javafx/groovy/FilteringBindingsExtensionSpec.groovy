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

import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

@Unroll
class FilteringBindingsExtensionSpec extends Specification {
    def "Filter then findFirst in list with defaultValue"() {
        given:
        Box defaultValue = new Box(6)
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenFindFirst(defaultValue, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in list with supplier"() {
        given:
        Supplier<Box> supplier = { new Box(6) }
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenFindFirst(supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in list with defaultValue (observable)"() {
        given:
        Box defaultValue = new Box(6)
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> predicate = { it.id % 2 == 0 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenFindFirst(defaultValue, filter)

        expect:
        defaultValue == binding.get()

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
        Supplier<Box> supplier = { new Box(6) }
        ObservableList<Box> items = FXCollections.observableArrayList()
        Predicate<Box> predicate = { it.id % 2 == 0 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenFindFirst(supplier, filter)

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

    def "Filter then findFirst #type in list with defaultValue"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."filterThenFindFirst${type}"(defaultValue, predicate as Predicate)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result == binding.get()

        where:
        type      | defaultValue | predicate                   | values                    | result
        'Boolean' | true         | { it }                      | [false, true, false]      | true
        'Integer' | 6            | { it % 2 == 0 }             | [1, 2, 3, 4, 5]           | 2
        'Long'    | 6L           | { it % 2 == 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L
        'Float'   | 6f           | { it % 2 == 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f
        'Double'  | 6d           | { it % 2 == 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d
        'String'  | '6'          | { it.toInteger() % 2 == 0 } | ['1', '2', '3', '4', '5'] | '2'
    }

    def "Filter then findFirst #type in list with supplier"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        Binding binding = items."filterThenFindFirst${type}"( supplier as Supplier, predicate)

        expect:
        ((Supplier) supplier).get() == binding.get()

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

    def "Filter then findFirst #type in list with defaultValue (observables)"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenFindFirst${type}"(defaultValue, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | predicate1                 | predicate2                  | values                    | result1 | result2
        'Boolean' | true         | { it }                     | { !it }                     | [false, true, false]      | true    | false
        'Integer' | 6            | { it % 2 == 0 }            | { it % 2 != 0 }             | [1, 2, 3, 4, 5]           | 2       | 1
        'Long'    | 6L           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L      | 1L
        'Float'   | 6f           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f      | 1f
        'Double'  | 6d           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d      | 1d
        'String'  | '6'          | { it.toInteger() % 2 == 0 }| { it.toInteger() % 2 != 0 } | ['1', '2', '3', '4', '5'] | '2'     | '1'
    }

    def "Filter then findFirst #type in list with supplier (observables)"() {
        given:
        ObservableList items = FXCollections.observableArrayList()
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenFindFirst${type}"(supplier as Supplier, filter)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

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

    def "Filter then findFirst in set with defaultValue"() {
        given:
        Box defaultValue = new Box(6)
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenFindFirst(defaultValue, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in set with supplier"() {
        given:
        Supplier<Box> supplier = { new Box(6) }
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenFindFirst(supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in set with defaultValue (observable)"() {
        given:
        Box defaultValue = new Box(6)
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> predicate = { it.id % 2 == 0 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenFindFirst(defaultValue, filter)

        expect:
        defaultValue == binding.get()

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
        Supplier<Box> supplier = { new Box(6) }
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Predicate<Box> predicate = { it.id % 2 == 0 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenFindFirst(supplier, filter)

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

    def "Filter then findFirst #type in set with defaultValue"() {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = items."filterThenFindFirst${type}"(defaultValue, predicate as Predicate)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result == binding.get()

        where:
        type      | defaultValue | predicate                   | values                    | result
        'Boolean' | true         | { it }                      | [false, true, false]      | true
        'Integer' | 6            | { it % 2 == 0 }             | [1, 2, 3, 4, 5]           | 2
        'Long'    | 6L           | { it % 2 == 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L
        'Float'   | 6f           | { it % 2 == 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f
        'Double'  | 6d           | { it % 2 == 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d
        'String'  | '6'          | { it.toInteger() % 2 == 0 } | ['1', '2', '3', '4', '5'] | '2'
    }

    def "Filter then findFirst #type in set with supplier"() {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = items."filterThenFindFirst${type}"(supplier as Supplier, predicate as Predicate)

        expect:
        ((Supplier) supplier).get() == binding.get()

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

    def "Filter then findFirst #type in set with defaultValue (observables)"() {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenFindFirst${type}"(defaultValue, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | predicate1                 | predicate2                  | values                    | result1 | result2
        'Boolean' | true         | { it }                     | { !it }                     | [false, true, false]      | true    | false
        'Integer' | 6            | { it % 2 == 0 }            | { it % 2 != 0 }             | [1, 2, 3, 4, 5]           | 2       | 1
        'Long'    | 6L           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1L, 2L, 3L, 4L, 5L]      | 2L      | 1L
        'Float'   | 6f           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1f, 2f, 3f, 4f, 5f]      | 2f      | 1f
        'Double'  | 6d           | { it % 2 == 0 }            | { it % 2 != 0 }             | [1d, 2d, 3d, 4d, 5d]      | 2d      | 1d
        'String'  | '6'          | { it.toInteger() % 2 == 0 }| { it.toInteger() % 2 != 0 } | ['1', '2', '3', '4', '5'] | '2'     | '1'
    }

    def "Filter then findFirst #type in set with supplier (observables)"() {
        given:
        ObservableSet items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenFindFirst${type}"(supplier as Supplier, filter)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

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

    def "Filter then findFirst in map with defaultValue"() {
        given:
        Box defaultValue = new Box(6)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenFindFirst(defaultValue, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in map with supplier"() {
        given:
        Supplier<Box> supplier = { new Box(6) }
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenFindFirst(supplier, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        new Box(2) == binding.get()
    }

    def "Filter then findFirst in map with defaultValue (observable)"() {
        given:
        Box defaultValue = new Box(6)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> predicate = { it.id % 2 == 0 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenFindFirst(defaultValue, filter)

        expect:
        defaultValue == binding.get()

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
        Supplier<Box> supplier = { new Box(6) }
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Predicate<Box> predicate = { it.id % 2 == 0 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenFindFirst(supplier, filter)

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

    def "Filter then findFirst #type in map with defaultValue"() {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        Binding binding = items."filterThenFindFirst${type}"(defaultValue, predicate)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(values)

        then:
        result == binding.get()

        where:
        type      | defaultValue | predicate                   | values                           | result
        'Boolean' | true         | { it }                      | toMap([false, true, false])      | true
        'Integer' | 6            | { it % 2 == 0 }             | toMap([1, 2, 3, 4, 5])           | 2
        'Long'    | 6L           | { it % 2 == 0 }             | toMap([1L, 2L, 3L, 4L, 5L])      | 2L
        'Float'   | 6f           | { it % 2 == 0 }             | toMap([1f, 2f, 3f, 4f, 5f])      | 2f
        'Double'  | 6d           | { it % 2 == 0 }             | toMap([1d, 2d, 3d, 4d, 5d])      | 2d
        'String'  | '6'          | { it.toInteger() % 2 == 0 } | toMap(['1', '2', '3', '4', '5']) | '2'
    }

    def "Filter then findFirst #type in map with supplier"() {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        Binding binding = items."filterThenFindFirst${type}"(supplier as Supplier, predicate)

        expect:
        ((Supplier) supplier).get() == binding.get()

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

    def "Filter then findFirst #type in map with defaultValue (observables)"() {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenFindFirst${type}"(defaultValue, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        where:
        type      | defaultValue | predicate1                 | predicate2                  | values                           | result1 | result2
        'Boolean' | true         | { it }                     | { !it }                     | toMap([false, true, false])      | true    | false
        'Integer' | 6            | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1, 2, 3, 4, 5])           | 2       | 1
        'Long'    | 6L           | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1L, 2L, 3L, 4L, 5L])      | 2L      | 1L
        'Float'   | 6f           | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1f, 2f, 3f, 4f, 5f])      | 2f      | 1f
        'Double'  | 6d           | { it % 2 == 0 }            | { it % 2 != 0 }             | toMap([1d, 2d, 3d, 4d, 5d])      | 2d      | 1d
        'String'  | '6'          | { it.toInteger() % 2 == 0 }| { it.toInteger() % 2 != 0 } | toMap(['1', '2', '3', '4', '5']) | '2'     | '1'
    }

    def "Filter then findFirst #type in map with supplier (observables)"() {
        given:
        ObservableMap items = FXCollections.observableMap([:])
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenFindFirst${type}"(supplier as Supplier, filter)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.putAll(values)

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

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

    def "Map then filter then findFirst in list with defaultValue"() {
        given:
        Integer defaultValue = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id }
        Predicate<Integer> filter = { it % 2 == 0 }
        Binding binding = items.mapThenFilterThenFindFirst(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in list with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 }
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id }
        Predicate<Integer> filter = { it % 2 == 0 }
        Binding binding = items.mapThenFilterThenFindFirst(supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in list with defaultValue (observable)"() {
        given:
        Integer defaultValue = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 }
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.mapThenFilterThenFindFirst(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

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
        Supplier<Integer> supplier = { 6 }
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 }
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.mapThenFilterThenFindFirst(supplier, mapper, filter)

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

    def "Map then findFirst #type in list with defaultValue"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(defaultValue, mapper, predicate)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | defaultValue | mapper               | predicate                   | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | 6            | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | 6L           | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | 6f           | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | 6d           | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | '6'          | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in list with supplier"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(supplier, mapper, predicate)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier | mapper               | predicate                   | result
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | { 6 }    | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | { 6L }   | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | { 6f }   | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | { 6d }   | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | { '6' }  | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in list with defaultValue (observables)"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result3 == binding.get()

        where:
        type      | defaultValue | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | 6            | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then findFirst #type in list with supplier (observables)"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(supplier, mapper, filter)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

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

    def "Map then filter then findFirst in set with defaultValue"() {
        given:
        Integer defaultValue = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id }
        Predicate<Integer> filter = { it % 2 == 0 }
        Binding binding = items.mapThenFilterThenFindFirst(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in set with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 }
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id }
        Predicate<Integer> filter = { it % 2 == 0 }
        Binding binding = items.mapThenFilterThenFindFirst(supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in set with defaultValue (observable)"() {
        given:
        Integer defaultValue = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 }
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.mapThenFilterThenFindFirst(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

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
        Supplier<Integer> supplier = { 6 }
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 }
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.mapThenFilterThenFindFirst(supplier, mapper, filter)

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

    def "Map then findFirst #type in set with defaultValue"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(defaultValue, mapper, predicate)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | defaultValue | mapper               | predicate                   | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | 6            | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | 6L           | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | 6f           | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | 6d           | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | '6'          | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in set with supplier"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(supplier, mapper, predicate)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier | mapper               | predicate                   | result
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | { 6 }    | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | { 6L }   | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | { 6f }   | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | { 6d }   | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | { '6' }  | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in set with defaultValue (observables)"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result3 == binding.get()

        where:
        type      | defaultValue | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | 6            | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        // 'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then findFirst #type in set with supplier (observables)"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(supplier, mapper, filter)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

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

    def "Map then filter then findFirst in map with defaultValue"() {
        given:
        Integer defaultValue = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id }
        Predicate<Integer> filter = { it % 2 == 0 }
        Binding binding = items.mapThenFilterThenFindFirst(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in map with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 }
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id }
        Predicate<Integer> filter = { it % 2 == 0 }
        Binding binding = items.mapThenFilterThenFindFirst(supplier, mapper, filter)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Map then filter then findFirst in map with defaultValue (observable)"() {
        given:
        Integer defaultValue = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 }
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.mapThenFilterThenFindFirst(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

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
        Supplier<Integer> supplier = { 6 }
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Integer> predicate = { it > 2 }
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.mapThenFilterThenFindFirst(supplier, mapper, filter)

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

    def "Map then findFirst #type in map with defaultValue"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(defaultValue, mapper, predicate)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | defaultValue | mapper               | predicate                   | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | 6            | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | 6L           | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | 6f           | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | 6d           | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | '6'          | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in map with supplier"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(supplier, mapper, predicate)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | supplier | mapper               | predicate                   | result
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it }                      | true
        'Integer' | { 6 }    | { it.id }            | { it % 2 == 0 }             | 2
        'Long'    | { 6L }   | { it.id as long }    | { it % 2 == 0 }             | 2L
        'Float'   | { 6f }   | { it.id as float }   | { it % 2 == 0 }             | 2f
        'Double'  | { 6d }   | { it.id as double }  | { it % 2 == 0 }             | 2d
        'String'  | { '6' }  | { it.id.toString() } | { it.toInteger() % 2 == 0 } | '2'
    }

    def "Map then findFirst #type in map with defaultValue (observables)"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(defaultValue, mapper, filter)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result3 == binding.get()

        where:
        type      | defaultValue | function1            | function2                  | predicate1             | predicate2                  | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it }                 | { !it }                     | true    | true    | false
        'Integer' | 6            | { it.id }            | { it.id * 2 }              | { it > 2 }             | { it % 2 == 0 }             | 3       | 4       | 2
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it > 2 }             | { it % 2 == 0 }             | 3L      | 4L      | 2L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it > 2 }             | { it % 2 == 0 }             | 3f      | 4f      | 2f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it > 2 }             | { it % 2 == 0 }             | 3d      | 4d      | 2d
        // 'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.toInteger() > 2 } | { it.toInteger() % 2 == 0 } | '3'     | '4'     | '2'
    }

    def "Map then findFirst #type in map with supplier (observables)"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Integer>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."mapTo${type}ThenFilterThenFindFirst"(supplier, mapper, filter)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result2 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

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

    def "Filter then map then findFirst in list with defaultValue"() {
        given:
        Integer defaultValue = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id }
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenMapThenFindFirst(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then findFirst in list with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 }
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> mapper = { it.id }
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenMapThenFindFirst(supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then filter then findFirst in list with defaultValue (observable)"() {
        given:
        Integer defaultValue = 6
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenMapThenFindFirst(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

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
        Supplier<Integer> supplier = { 6 }
        ObservableList<Box> items = FXCollections.observableArrayList()
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenMapThenFindFirst(supplier, filter, mapper)

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

    def "Filter then map then findFirst #type in list with defaultValue"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(defaultValue, filter as Predicate, mapper as Function)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | defaultValue | mapper               | filter             | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | 6            | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | 6L           | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | 6f           | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | 6d           | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | '6'          | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in list with supplier"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(supplier, filter as Predicate, mapper as Function)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier | mapper               | filter             | result
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | { 6 }    | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | { 6L }   | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | { 6f }   | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | { 6d }   | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | { '6' }  | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in list with defaultValue (observables)"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result3 == binding.get()

        where:
        type      | defaultValue | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | 6            | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst #type in list with supplier (observables)"() {
        given:
        ObservableList<Box> items = FXCollections.observableArrayList()
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(supplier, filter, mapper)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2 as Function)

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

    def "Filter then map then findFirst in set with defaultValue"() {
        given:
        Integer defaultValue = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id }
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenMapThenFindFirst(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then findFirst in set with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 }
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> mapper = { it.id }
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenMapThenFindFirst(supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        2 == binding.get()
    }

    def "Filter then map then filter then findFirst in set with defaultValue (observable)"() {
        given:
        Integer defaultValue = 6
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenMapThenFindFirst(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

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
        Supplier<Integer> supplier = { 6 }
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenMapThenFindFirst(supplier, filter, mapper)

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

    def "Filter then map then findFirst #type in set with defaultValue"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(defaultValue, filter as Predicate, mapper as Function)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | defaultValue | mapper               | filter             | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | 6            | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | 6L           | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | 6f           | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | 6d           | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | '6'          | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in set with supplier"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(supplier, filter as Predicate, mapper as Function)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result == binding.get()

        where:
        type      | supplier | mapper               | filter             | result
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | { 6 }    | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | { 6L }   | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | { 6f }   | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | { 6d }   | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | { '6' }  | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in set with defaultValue (observables)"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result3 == binding.get()

        where:
        type      | defaultValue | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | 6            | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst #type in set with supplier (observables)"() {
        given:
        ObservableSet<Box> items = FXCollections.observableSet(new TreeSet<>())
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(supplier, filter, mapper)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(3), new Box(4)])

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2 as Function)

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

    def "Filter then map then findFirst in map with defaultValue"() {
        given:
        Integer defaultValue = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id }
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenMapThenFindFirst(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Filter then map then findFirst in map with supplier"() {
        given:
        Supplier<Integer> supplier = { 6 }
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> mapper = { it.id }
        Predicate<Box> filter = { it.id % 2 == 0 }
        Binding binding = items.filterThenMapThenFindFirst(supplier, filter, mapper)

        expect:
        supplier.get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        2 == binding.get()
    }

    def "Filter then map then filter then findFirst in map with defaultValue (observable)"() {
        given:
        Integer defaultValue = 6
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenMapThenFindFirst(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

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
        Supplier<Integer> supplier = { 6 }
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Function<Box, Integer> function = { it.id }
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function)
        Predicate<Box> predicate = { it.id > 2 }
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate)
        Binding binding = items.filterThenMapThenFindFirst(supplier, filter, mapper)

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

    def "Filter then map then findFirst #type in map with defaultValue"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(defaultValue, filter as Predicate, mapper as Function)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | defaultValue | mapper               | filter             | result
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | 6            | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | 6L           | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | 6f           | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | 6d           | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | '6'          | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in map with supplier"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(supplier, filter as Predicate, mapper as Function)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result == binding.get()

        where:
        type      | supplier | mapper               | filter             | result
        'Boolean' | { true } | { it.id % 2 == 0 }   | { it.id % 2 == 0 } | true
        'Integer' | { 6 }    | { it.id }            | { it.id % 2 == 0 } | 2
        'Long'    | { 6L }   | { it.id as long }    | { it.id % 2 == 0 } | 2L
        'Float'   | { 6f }   | { it.id as float }   | { it.id % 2 == 0 } | 2f
        'Double'  | { 6d }   | { it.id as double }  | { it.id % 2 == 0 } | 2d
        'String'  | { '6' }  | { it.id.toString() } | { it.id % 2 == 0 } | '2'
    }

    def "Filter then map then findFirst #type in map with defaultValue (observables)"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(defaultValue, filter, mapper)

        expect:
        defaultValue == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2 as Function)

        then:
        result3 == binding.get()

        where:
        type      | defaultValue | function1            | function2                  | predicate1    | predicate2         | result1 | result2 | result3
        'Boolean' | true         | { it.id % 2 == 0 }   | { it.id % 2 != 0 }         | { it.id > 2 } | { it.id % 2 == 0 } | false   | true    | false
        'Integer' | 6            | { it.id }            | { (it.id * 2) }            | { it.id > 2 } | { it.id % 2 == 0 } | 3       | 2       | 4
        'Long'    | 6L           | { it.id as long }    | { (it.id * 2) as long }    | { it.id > 2 } | { it.id % 2 == 0 } | 3L      | 2L      | 4L
        'Float'   | 6f           | { it.id as float }   | { (it.id * 2) as float }   | { it.id > 2 } | { it.id % 2 == 0 } | 3f      | 2f      | 4f
        'Double'  | 6d           | { it.id as double }  | { (it.id * 2) as double }  | { it.id > 2 } | { it.id % 2 == 0 } | 3d      | 2d      | 4d
        'String'  | '6'          | { it.id.toString() } | { (it.id * 2).toString() } | { it.id > 2 } | { it.id % 2 == 0 } | '3'     | '2'     | '4'
    }

    def "Filter then map then findFirst #type in map with supplier (observables)"() {
        given:
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(function1 as Function)
        ObjectProperty<Predicate<Box>> filter = new SimpleObjectProperty<>(predicate1 as Predicate)
        Binding binding = items."filterThenMapTo${type}ThenFindFirst"(supplier, filter, mapper)

        expect:
        ((Supplier) supplier).get() == binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(3), new Box(4)]))

        then:
        result1 == binding.get()

        when:
        filter.set(predicate2 as Predicate)

        then:
        result2 == binding.get()

        when:
        mapper.set(function2 as Function)

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
