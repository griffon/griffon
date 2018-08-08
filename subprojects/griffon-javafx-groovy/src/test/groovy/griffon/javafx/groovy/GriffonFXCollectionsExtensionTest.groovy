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

import griffon.javafx.collections.ElementObservableList
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import org.junit.Ignore
import org.junit.Test

import javax.annotation.Nonnull
import java.util.function.Function

import static javafx.collections.FXCollections.observableArrayList
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.empty

class GriffonFXCollectionsExtensionTest {
    @Test
    void testOperations() {
        // given:
        ObservableList<String> source = observableArrayList()
        Function<String, Integer> mapper = { Integer.valueOf(it) } as Function
        ObservableList<Integer> target = source.mappedWith(mapper)

        // expect:
        assertThat(target, empty())

        // when:
        source.addAll(['1', '2', '3', '4', '5'])

        // then:
        assertThat(target, contains(1, 2, 3, 4, 5))

        // when:
        source.remove('3')

        // then:
        assertThat(target, contains(1, 2, 4, 5))

        // when:
        source.remove('2')
        source.remove('4')

        // then:
        assertThat(target, contains(1, 5))

        source.set(0, '5')
        source.set(1, '1')

        // then:
        assertThat(target, contains(5, 1))
    }

    @Test
    //@Ignore("java.lang.VerifyError when invoking Function.identity() with Groovy 2.4.15")
    void testOperationsWithObservable() {
        // given:
        ObservableList<Integer> source = observableArrayList()
        Function<Integer, Integer> function = Function.identity()
        ObjectProperty<Function<Integer, Integer>> mapper = new SimpleObjectProperty<>(function)
        ObservableList<Integer> target = source.mappedWith(mapper)

        // expect:
        assertThat(target, empty())

        // when:
        source.addAll([1, 2, 3, 4, 5])

        // then:
        assertThat(target, contains(1, 2, 3, 4, 5))

        // when:
        mapper.setValue({ i -> i * 2 } as Function)

        // then:
        assertThat(source, contains(1, 2, 3, 4, 5))
        assertThat(target, contains(2, 4, 6, 8, 10))

        // when:
        source.remove(1, 4)

        // then:
        assertThat(source, contains(1, 5))
        assertThat(target, contains(2, 10))
    }

    @Test
    void testOperationsWithObservableBean() {
        // given:
        ObservableList<ObservablePerson> source = new ElementObservableList<>(observableArrayList())
        Function<ObservablePerson, String> mapper = { it.name } as Function
        ObservableList<String> target = source.mappedWith(mapper)

        // expect:
        assertThat(target, empty())

        // when:
        source.addAll(
            new ObservablePerson('n1', 'l1'),
            new ObservablePerson('n2', 'l2'),
            new ObservablePerson('n3', 'l3'))

        // then:
        assertThat(target, contains('n1', 'n2', 'n3'))

        // when:
        source.get(2).setLastname('l33')

        // then:
        assertThat(target, contains('n1', 'n2', 'n3'))

        // when:
        source.get(2).setName('n33')

        // then:
        assertThat(target, contains('n1', 'n2', 'n33'))
    }

    static class ObservablePerson implements ElementObservableList.ObservableValueContainer {
        private StringProperty name = new SimpleStringProperty(this, 'name')
        private StringProperty lastname = new SimpleStringProperty(this, 'lastname')


        ObservablePerson(String name, String lastname) {
            setName(name)
            setLastname(lastname)
        }

        String getName() {
            name.get()
        }

        StringProperty nameProperty() {
            name
        }

        void setName(String name) {
            this.name.set(name)
        }

        String getLastname() {
            lastname.get()
        }

        StringProperty lastnameProperty() {
            lastname
        }

        void setLastname(String lastname) {
            this.lastname.set(lastname)
        }

        @Nonnull
        @Override
        ObservableValue<?>[] observableValues() {
            [nameProperty(), lastnameProperty()] as ObservableValue[]
        }
    }
}
