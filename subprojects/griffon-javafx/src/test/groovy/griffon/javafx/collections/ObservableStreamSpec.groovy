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
package griffon.javafx.collections

import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.LongProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

import static javafx.collections.FXCollections.observableArrayList
import static javafx.collections.FXCollections.observableMap
import static javafx.collections.FXCollections.observableSet

/**
 * @author Andres Almiray
 */
@Unroll
class ObservableStreamSpec extends Specification {
    @Shared private Ghost INKY = new Ghost(id: 1, name: 'Inky')
    @Shared private Ghost PINKY = new Ghost(id: 2, name: 'Pinky')
    @Shared private Ghost BLINKY = new Ghost(id: 3, name: 'Blinky')
    @Shared private Ghost CLYDE = new Ghost(id: 4, name: 'Clyde')

    void "Check map/filter/reduce (default value) on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        ObjectBinding<Number> result = stream
            .map({ g -> g.name })
            .filter({ n -> n.endsWith('y') })
            .map({ n -> n.size() })
            .reduce(0, { a, b -> a + b })

        then:
        result.get() == 15

        where:
        type             | ghosts
        'ObservableList' | observableListsOfGhosts()
        'ObservableSet'  | observableSetOfGhosts()
        'ObservableMap'  | observableMapOfGhosts()
    }

    void "Check map/filter/reduce (supplier) on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        ObjectBinding<Number> result = stream
            .map({ g -> g.name })
            .filter({ n -> n.endsWith('y') })
            .map({ n -> n.size() })
            .reduce({ -> 0 } as Supplier, { a, b -> a + b })

        then:
        result.get() == 15

        where:
        type             | ghosts
        'ObservableList' | observableListsOfGhosts()
        'ObservableSet'  | observableSetOfGhosts()
        'ObservableMap'  | observableMapOfGhosts()
    }

    void "Check map/filter/reduce (default value) on #type with observables"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)
        ObjectProperty mapper1 = new SimpleObjectProperty<>({ g -> g.name } as Function)
        ObjectProperty filter1 = new SimpleObjectProperty<>({ n -> n.endsWith('y') } as Predicate)
        ObjectProperty mapper2 = new SimpleObjectProperty<>({ n -> n.size() } as Function)
        ObjectProperty accumulator1 = new SimpleObjectProperty<>({ a, b -> a + b } as BinaryOperator)

        when:
        ObjectBinding<Number> result = stream
            .map(mapper1)
            .filter(filter1)
            .map(mapper2)
            .reduce(0, accumulator1)

        then:
        result.get() == 15

        when:
        mapper1.setValue({ g -> g.id } as Function)
        filter1.setValue({ n -> n % 2 != 0 } as Predicate)
        mapper2.setValue({ n -> n * 2 } as Function)
        accumulator1.setValue({ a, b -> a * b } as BinaryOperator)

        then:
        result.get() == 12

        where:
        type             | ghosts
        'ObservableList' | observableListsOfGhosts()
        'ObservableSet'  | observableSetOfGhosts()
        'ObservableMap'  | observableMapOfGhosts()
    }

    void "Check map/filter/reduce (supplier) on #type with observables"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)
        ObjectProperty mapper1 = new SimpleObjectProperty<>({ g -> g.name } as Function)
        ObjectProperty filter1 = new SimpleObjectProperty<>({ n -> n.endsWith('y') } as Predicate)
        ObjectProperty mapper2 = new SimpleObjectProperty<>({ n -> n.size() } as Function)
        ObjectProperty accumulator1 = new SimpleObjectProperty<>({ a, b -> a + b } as BinaryOperator)

        when:
        ObjectBinding<Number> result = stream
            .map(mapper1)
            .filter(filter1)
            .map(mapper2)
            .reduce({ -> 0 } as Supplier, accumulator1)

        then:
        result.get() == 15

        when:
        mapper1.setValue({ g -> g.id } as Function)
        filter1.setValue({ n -> n % 2 != 0 } as Predicate)
        mapper2.setValue({ n -> n * 2 } as Function)
        accumulator1.setValue({ a, b -> a * b } as BinaryOperator)

        then:
        result.get() == 12

        where:
        type             | ghosts
        'ObservableList' | observableListsOfGhosts()
        'ObservableSet'  | observableSetOfGhosts()
        'ObservableMap'  | observableMapOfGhosts()
    }

    void "Check #method matcher on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        BooleanBinding binding = stream."$method"(predicate1 as Predicate)

        then:
        binding.get()

        when:
        ObjectProperty p = new SimpleObjectProperty<>(predicate1 as Predicate)
        binding = stream."$method"(p)

        then:
        binding.get()

        when:
        p.setValue(predicate2 as Predicate)

        then:
        !binding.get()

        where:
        type             | ghosts                    | method      | predicate1    | predicate2
        'ObservableList' | observableListsOfGhosts() | 'anyMatch'  | { it.id < 3 } | { it.id > 4 }
        'ObservableSet'  | observableSetOfGhosts()   | 'anyMatch'  | { it.id < 3 } | { it.id > 4 }
        'ObservableMap'  | observableMapOfGhosts()   | 'anyMatch'  | { it.id < 3 } | { it.id > 4 }
        'ObservableList' | observableListsOfGhosts() | 'allMatch'  | { it.id < 5 } | { it.id > 4 }
        'ObservableSet'  | observableSetOfGhosts()   | 'allMatch'  | { it.id < 5 } | { it.id > 4 }
        'ObservableMap'  | observableMapOfGhosts()   | 'allMatch'  | { it.id < 5 } | { it.id > 4 }
        'ObservableList' | observableListsOfGhosts() | 'noneMatch' | { it.id > 4 } | { it.id < 3 }
        'ObservableSet'  | observableSetOfGhosts()   | 'noneMatch' | { it.id > 4 } | { it.id < 3 }
        'ObservableMap'  | observableMapOfGhosts()   | 'noneMatch' | { it.id > 4 } | { it.id < 3 }
    }

    void "Check sorted on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        ObjectBinding<Ghost> first = stream.sorted().findFirst()

        then:
        first.get().id == 1

        when:
        first = stream.sorted(comparator2 as Comparator).findFirst()

        then:
        first.get().id == 4

        when:
        ObjectProperty c = new SimpleObjectProperty<>(comparator1 as Comparator)
        first = stream.sorted(c).findFirst()

        then:
        first.get().id == 1

        when:
        c.setValue(comparator2 as Comparator)

        then:
        first.get().id == 4

        where:
        type             | ghosts                    | comparator1             | comparator2
        'ObservableList' | observableListsOfGhosts() | { a, b -> a.id - b.id } | { a, b -> b.id - a.id }
        'ObservableSet'  | observableSetOfGhosts()   | { a, b -> a.id - b.id } | { a, b -> b.id - a.id }
        'ObservableMap'  | observableMapOfGhosts()   | { a, b -> a.id - b.id } | { a, b -> b.id - a.id }
    }

    void "Check limit on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        ObjectBinding<Number> result = stream.limit(1l)
            .map({ g -> g.name })
            .filter({ n -> n.endsWith('y') })
            .map({ n -> n.size() })
            .reduce(0, { a, b -> a + b })

        then:
        result.get() == 4

        when:
        LongProperty m = new SimpleLongProperty(1l)
        result = stream.limit(m)
            .map({ g -> g.name })
            .filter({ n -> n.endsWith('y') })
            .map({ n -> n.size() })
            .reduce(0, { a, b -> a + b })

        then:
        result.get() == 4

        when:
        m.setValue(2L)

        then:
        result.get() == 9

        where:
        type             | ghosts
        'ObservableList' | observableListsOfGhosts()
        'ObservableSet'  | observableSetOfGhosts()
        'ObservableMap'  | observableMapOfGhosts()
    }

    void "Check skip on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        ObjectBinding<Number> result = stream.skip(1l)
            .map({ g -> g.name })
            .filter({ n -> n.endsWith('y') })
            .map({ n -> n.size() })
            .reduce(0, { a, b -> a + b })

        then:
        result.get() == 11

        when:
        LongProperty m = new SimpleLongProperty(1l)
        result = stream.skip(m)
            .map({ g -> g.name })
            .filter({ n -> n.endsWith('y') })
            .map({ n -> n.size() })
            .reduce(0, { a, b -> a + b })

        then:
        result.get() == 11

        when:
        m.setValue(2L)

        then:
        result.get() == 6

        where:
        type             | ghosts
        'ObservableList' | observableListsOfGhosts()
        'ObservableSet'  | observableSetOfGhosts()
        'ObservableMap'  | observableMapOfGhosts()
    }

    void "Check #method"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(observableArrayList())
        Ghost value = CLYDE

        when:
        ObjectBinding<Ghost> result = stream."$method"()

        then:
        !result.get()

        where:
        method << ['findFirst', 'findAny']
    }

    void "Check #method (default value)"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(observableArrayList())
        Ghost value = CLYDE

        when:
        ObjectBinding<Ghost> result = stream."$method"(value)

        then:
        value == result.get()

        where:
        method << ['findFirst', 'findAny']
    }

    void "Check #method (supplier)"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(observableArrayList())
        Ghost value = CLYDE

        when:
        ObjectBinding<Ghost> result = stream."$method"({ -> value } as Supplier)

        then:
        value == result.get()

        where:
        method << ['findFirst', 'findAny']
    }

    void "Check #method on #type"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(ghosts)

        when:
        ObjectBinding<Ghost> result = stream."$method"(comparator1 as Comparator)

        then:
        result.get() == value1

        when:
        result = stream."$method"(comparator2 as Comparator)

        then:
        result.get() == value2

        when:
        ObjectProperty c = new SimpleObjectProperty<>(comparator1 as Comparator)
        result = stream."$method"(c)

        then:
        result.get() == value1

        when:
        c.setValue(comparator2 as Comparator)

        then:
        result.get() == value2

        where:
        type             | ghosts                    | method | comparator1             | comparator2             | value1 | value2
        'ObservableList' | observableListsOfGhosts() | 'min'  | { a, b -> a.id - b.id } | { a, b -> b.id - a.id } | INKY   | CLYDE
        'ObservableSet'  | observableSetOfGhosts()   | 'min'  | { a, b -> a.id - b.id } | { a, b -> b.id - a.id } | INKY   | CLYDE
        'ObservableMap'  | observableMapOfGhosts()   | 'min'  | { a, b -> a.id - b.id } | { a, b -> b.id - a.id } | INKY   | CLYDE
        'ObservableList' | observableListsOfGhosts() | 'max'  | { a, b -> a.id - b.id } | { a, b -> b.id - a.id } | CLYDE  | INKY
        'ObservableSet'  | observableSetOfGhosts()   | 'max'  | { a, b -> a.id - b.id } | { a, b -> b.id - a.id } | CLYDE  | INKY
        'ObservableMap'  | observableMapOfGhosts()   | 'max'  | { a, b -> a.id - b.id } | { a, b -> b.id - a.id } | CLYDE  | INKY
    }

    void "Check #method on #type (default value)"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(observableArrayList())

        when:
        ObjectBinding<Ghost> result = stream."$method"(value1, comparator1 as Comparator)

        then:
        result.get() == value1

        when:
        ObjectProperty c = new SimpleObjectProperty<>(comparator1 as Comparator)
        result = stream."$method"(value1, c)

        then:
        result.get() == value1

        where:
        type             | ghosts                    | method | comparator1             | value1
        'ObservableList' | observableListsOfGhosts() | 'min'  | { a, b -> a.id - b.id } | INKY
        'ObservableSet'  | observableSetOfGhosts()   | 'min'  | { a, b -> a.id - b.id } | INKY
        'ObservableMap'  | observableMapOfGhosts()   | 'min'  | { a, b -> a.id - b.id } | INKY
        'ObservableList' | observableListsOfGhosts() | 'max'  | { a, b -> a.id - b.id } | CLYDE
        'ObservableSet'  | observableSetOfGhosts()   | 'max'  | { a, b -> a.id - b.id } | CLYDE
        'ObservableMap'  | observableMapOfGhosts()   | 'max'  | { a, b -> a.id - b.id } | CLYDE
    }

    void "Check #method on #type (supplier)"() {
        given:
        ObservableStream<Ghost> stream = GriffonFXCollections.observableStream(observableArrayList())

        when:
        ObjectBinding<Ghost> result = stream."$method"({ -> value1 } as Supplier, comparator1 as Comparator)

        then:
        result.get() == value1

        when:
        ObjectProperty c = new SimpleObjectProperty<>(comparator1 as Comparator)
        result = stream."$method"({ -> value1 } as Supplier, c)

        then:
        result.get() == value1

        where:
        type             | ghosts                    | method | comparator1             | value1
        'ObservableList' | observableListsOfGhosts() | 'min'  | { a, b -> a.id - b.id } | INKY
        'ObservableSet'  | observableSetOfGhosts()   | 'min'  | { a, b -> a.id - b.id } | INKY
        'ObservableMap'  | observableMapOfGhosts()   | 'min'  | { a, b -> a.id - b.id } | INKY
        'ObservableList' | observableListsOfGhosts() | 'max'  | { a, b -> a.id - b.id } | CLYDE
        'ObservableSet'  | observableSetOfGhosts()   | 'max'  | { a, b -> a.id - b.id } | CLYDE
        'ObservableMap'  | observableMapOfGhosts()   | 'max'  | { a, b -> a.id - b.id } | CLYDE
    }

    private ObservableList<Ghost> observableListsOfGhosts() {
        return observableArrayList(
            INKY,
            PINKY,
            BLINKY,
            CLYDE
        )
    }

    private ObservableSet<Ghost> observableSetOfGhosts() {
        return observableSet([
            INKY,
            PINKY,
            BLINKY,
            CLYDE
        ] as LinkedHashSet)
    }

    private ObservableMap<String, Ghost> observableMapOfGhosts() {
        return observableMap(
            g1: INKY,
            g2: PINKY,
            g3: BLINKY,
            g4: CLYDE
        )
    }

    private class Ghost implements Comparable<Ghost> {
        int id
        String name

        @Override
        int compareTo(Ghost o) {
            id - o?.id
        }


        @Override
        String toString() {
            name
        }
    }
}
