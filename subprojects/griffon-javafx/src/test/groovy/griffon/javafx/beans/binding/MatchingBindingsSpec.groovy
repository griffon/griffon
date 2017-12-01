/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
import javafx.beans.binding.BooleanBinding
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

@Unroll
class MatchingBindingsSpec extends Specification {
    def "AnyMatch in List"() {
        given:
        Predicate<Integer> predicate = { e -> e == 3 } as Predicate<Integer>
        ObservableList<Integer> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.anyMatch(items, predicate)

        expect:
        !binding.get()

        when:
        items.addAll(0, 1, 2, 4, 5)

        then:
        !binding.get()

        when:
        items.add(3)

        then:
        binding.get()
    }

    def "AnyMatch in List with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e == 3 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableList<Box> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.anyMatch(items, mapper, predicate)

        expect:
        !binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(4), new Box(5)])

        then:
        !binding.get()

        when:
        items.add(new Box(3))

        then:
        binding.get()
    }

    def "AnyMatch in List with observable predicate"() {
        given:
        Predicate<Integer> p = { e -> e == 3 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        ObservableList<Integer> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.anyMatch(items, predicate)

        expect:
        !binding.get()

        when:
        items.addAll(0, 1, 2, 4, 5)

        then:
        !binding.get()

        when:
        items.add(3)

        then:
        binding.get()

        when:
        predicate.set({ e -> e == 6 } as Predicate<Integer>)

        then:
        !binding.get()

        when:
        items.add(6)

        then:
        binding.get()
    }

    def "AnyMatch in List with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e == 3 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableList<Box> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.anyMatch(items, mapper, predicate)

        expect:
        !binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(4), new Box(5)])

        then:
        !binding.get()

        when:
        items.add(new Box(3))

        then:
        binding.get()

        when:
        predicate.set({ e -> e == 6 } as Predicate<Integer>)

        then:
        !binding.get()

        when:
        mapper.set({ e -> e.id * 2 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "AnyMatch in Set"() {
        given:
        Predicate<Integer> predicate = { e -> e == 3 } as Predicate<Integer>
        ObservableSet<Integer> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.anyMatch(items, predicate)

        expect:
        !binding.get()

        when:
        items.addAll(0, 1, 2, 4, 5)

        then:
        !binding.get()

        when:
        items.add(3)

        then:
        binding.get()
    }

    def "AnyMatch in Set with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e == 3 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableSet<Box> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.anyMatch(items, mapper, predicate)

        expect:
        !binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(4), new Box(5)])

        then:
        !binding.get()

        when:
        items.add(new Box(3))

        then:
        binding.get()
    }

    def "AnyMatch in Set with observable predicate"() {
        given:
        Predicate<Integer> p = { e -> e == 3 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        ObservableSet<Integer> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.anyMatch(items, predicate)

        expect:
        !binding.get()

        when:
        items.addAll(0, 1, 2, 4, 5)

        then:
        !binding.get()

        when:
        items.add(3)

        then:
        binding.get()

        when:
        predicate.set({ e -> e == 6 } as Predicate<Integer>)

        then:
        !binding.get()

        when:
        items.add(6)

        then:
        binding.get()
    }

    def "AnyMatch in Set with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e == 3 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableSet<Box> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.anyMatch(items, mapper, predicate)

        expect:
        !binding.get()

        when:
        items.addAll([new Box(1), new Box(2), new Box(4), new Box(5)])

        then:
        !binding.get()

        when:
        items.add(new Box(3))

        then:
        binding.get()

        when:
        predicate.set({ e -> e == 6 } as Predicate<Integer>)

        then:
        !binding.get()

        when:
        mapper.set({ e -> e.id * 2 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "AnyMatch in Map"() {
        given:
        Predicate<Box> predicate = { e -> e.id == 3 } as Predicate<Box>
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.anyMatch(items, predicate)

        expect:
        !binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(4), new Box(5)]))

        then:
        !binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        binding.get()
    }

    def "AnyMatch in Map with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e == 3 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.anyMatch(items, mapper, predicate)

        expect:
        !binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(4), new Box(5)]))

        then:
        !binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        binding.get()
    }

    def "AnyMatch in Map with observable predicate"() {
        given:
        Predicate<Box> p = { e -> e.id == 3 } as Predicate<Box>
        ObjectProperty<Predicate<Box>> predicate = new SimpleObjectProperty<>(p)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.anyMatch(items, predicate)

        expect:
        !binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(4), new Box(5)]))

        then:
        !binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        binding.get()

        when:
        predicate.set({ e -> e.id == 6 } as Predicate<Box>)

        then:
        !binding.get()

        when:
        items.put('key6', new Box(6))

        then:
        binding.get()
    }

    def "AnyMatch in Map with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e == 3 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.anyMatch(items, mapper, predicate)

        expect:
        !binding.get()

        when:
        items.putAll(toMap([new Box(1), new Box(2), new Box(4), new Box(5)]))

        then:
        !binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        binding.get()

        when:
        predicate.set({ e -> e == 6 } as Predicate<Integer>)

        then:
        !binding.get()

        when:
        mapper.set({ e -> e.id * 2 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "AllMatch in List"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        ObservableList<Integer> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.allMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(2)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([1, 3, 5])

        then:
        binding.get()
    }

    def "AllMatch in List with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableList<Box> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.allMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(1), new Box(3), new Box(3)])

        then:
        binding.get()
    }

    def "AllMatch in List with observable predicate"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        ObservableList<Integer> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.allMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(2)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([1, 3, 5])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()
    }

    def "AllMatch in List with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableList<Box> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.allMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(1), new Box(3), new Box(3)])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()

        then:
        mapper.set({ e -> e.id * 2 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "AllMatch in Set"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        ObservableSet<Integer> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.allMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(2)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([1, 3, 5])

        then:
        binding.get()
    }

    def "AllMatch in Set with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableSet<Box> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.allMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(1), new Box(3), new Box(3)])

        then:
        binding.get()
    }

    def "AllMatch in Set with observable predicate"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        ObservableSet<Integer> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.allMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(2)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([1, 3, 5])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()
    }

    def "AllMatch in Set with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableSet<Box> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.allMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(1), new Box(3), new Box(3)])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()

        then:
        mapper.set({ e -> e.id * 2 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "AllMatch in Map"() {
        given:
        Predicate<Box> predicate = { e -> e.id % 2 == 1 } as Predicate<Box>
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.allMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.put('key2', new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(1), new Box(3), new Box(5)]))

        then:
        binding.get()
    }

    def "AllMatch in Map with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.allMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.put('key2', new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(1), new Box(3), new Box(5)]))

        then:
        binding.get()
    }

    def "AllMatch in Map with observable predicate"() {
        given:
        Predicate<Box> p = { e -> e.id % 2 == 1 } as Predicate<Box>
        ObjectProperty<Predicate<Box>> predicate = new SimpleObjectProperty<>(p)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.allMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.put('key2', new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(1), new Box(3), new Box(5)]))

        then:
        binding.get()

        when:
        predicate.set({ e -> e.id % 2 == 0 } as Predicate<Box>)

        then:
        !binding.get()
    }

    def "AllMatch in Map with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.allMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.put('key2', new Box(2))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(1), new Box(3), new Box(5)]))

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()

        then:
        mapper.set({ e -> e.id * 2 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "NoneMatch in List"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        ObservableList<Integer> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.noneMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(3)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([2, 4, 6])

        then:
        binding.get()
    }

    def "NoneMatch in List with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableList<Box> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.noneMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(2), new Box(4), new Box(6)])

        then:
        binding.get()
    }

    def "NoneMatch in List with observable predicate"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        ObservableList<Integer> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.noneMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(3)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([2, 4, 6])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()
    }

    def "NoneMatch in List with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableList<Box> items = FXCollections.observableArrayList()
        BooleanBinding binding = MatchingBindings.noneMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(2), new Box(4), new Box(6)])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()

        then:
        mapper.set({ e -> e.id + 1 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "NoneMatch in Set"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        ObservableSet<Integer> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.noneMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(3)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([2, 4, 6])

        then:
        binding.get()
    }

    def "NoneMatch in Set with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableSet<Box> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.noneMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(2), new Box(4), new Box(6)])

        then:
        binding.get()
    }

    def "NoneMatch in Set with observable predicate"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        ObservableSet<Integer> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.noneMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.add(3)

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([2, 4, 6])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()
    }

    def "NoneMatch in Set with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableSet<Box> items = FXCollections.observableSet()
        BooleanBinding binding = MatchingBindings.noneMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.add(new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.addAll([new Box(2), new Box(4), new Box(6)])

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()

        then:
        mapper.set({ e -> e.id + 1 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    def "NoneMatch in Map"() {
        given:
        Predicate<Box> predicate = { e -> e.id % 2 == 1 } as Predicate<Box>
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.noneMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(2), new Box(4), new Box(6)]))

        then:
        binding.get()
    }

    def "NoneMatch in Map with mapper"() {
        given:
        Predicate<Integer> predicate = { e -> e % 2 == 1 } as Predicate<Integer>
        Function<Box, Integer> mapper = { e -> e.id } as Function<Box, Integer>
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.noneMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(2), new Box(4), new Box(6)]))

        then:
        binding.get()
    }

    def "NoneMatch in Map with observable predicate"() {
        given:
        Predicate<Box> p = { e -> e.id % 2 == 1 } as Predicate<Box>
        ObjectProperty<Predicate<Box>> predicate = new SimpleObjectProperty<>(p)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.noneMatch(items, predicate)

        expect:
        binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(2), new Box(4), new Box(6)]))

        then:
        binding.get()

        when:
        predicate.set({ e -> e.id % 2 == 0 } as Predicate<Box>)

        then:
        !binding.get()
    }

    def "NoneMatch in Map with observable predicate and mapper"() {
        given:
        Predicate<Integer> p = { e -> e % 2 == 1 } as Predicate<Integer>
        ObjectProperty<Predicate<Integer>> predicate = new SimpleObjectProperty<>(p)
        Function<Box, Integer> m = { e -> e.id } as Function<Box, Integer>
        ObjectProperty<Function<Box, Integer>> mapper = new SimpleObjectProperty<>(m)
        ObservableMap<String, Box> items = FXCollections.observableMap([:])
        BooleanBinding binding = MatchingBindings.noneMatch(items, mapper, predicate)

        expect:
        binding.get()

        when:
        items.put('key3', new Box(3))

        then:
        !binding.get()

        when:
        items.clear()
        items.putAll(toMap([new Box(2), new Box(4), new Box(6)]))

        then:
        binding.get()

        when:
        predicate.set({ e -> e % 2 == 0 } as Predicate<Integer>)

        then:
        !binding.get()

        then:
        mapper.set({ e -> e.id + 1 } as Function<Box, Integer>)

        then:
        binding.get()
    }

    @Canonical
    private static class Box {
        int id
    }

    private static toMap(list) {
        Map m = [:]
        list.eachWithIndex { e, i -> m.put("key${i}".toString(), e) }
        m
    }
}
