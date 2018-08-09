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
package griffon.javafx.collections;

import griffon.annotations.core.Nonnull;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.junit.Test;

import java.util.function.Function;

import static javafx.collections.FXCollections.observableArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class MappingObservableListTest {
    @Test
    public void testOperations() {
        // given:
        ObservableList<String> source = observableArrayList();
        Function<String, Integer> mapper = Integer::valueOf;
        ObservableList<Integer> target = new MappingObservableList<>(source, mapper);

        // expect:
        assertThat(target, empty());

        // when:
        source.addAll("1", "2", "3", "4", "5");

        // then:
        assertThat(target, contains(1, 2, 3, 4, 5));

        // when:
        source.remove("3");

        // then:
        assertThat(target, contains(1, 2, 4, 5));

        // when:
        source.remove("2");
        source.remove("4");

        // then:
        assertThat(target, contains(1, 5));

        source.set(0, "5");
        source.set(1, "1");

        // then:
        assertThat(target, contains(5, 1));
    }

    @Test
    public void testOperationsWithObservable() {
        // given:
        ObservableList<Integer> source = observableArrayList();
        Function<Integer, Integer> function = Function.identity();
        ObjectProperty<Function<Integer, Integer>> mapper = new SimpleObjectProperty<>(function);
        ObservableList<Integer> target = new MappingObservableList<>(source, mapper);

        // expect:
        assertThat(target, empty());

        // when:
        source.addAll(1, 2, 3, 4, 5);

        // then:
        assertThat(target, contains(1, 2, 3, 4, 5));

        // when:
        mapper.setValue(i -> i * 2);

        // then:
        assertThat(source, contains(1, 2, 3, 4, 5));
        assertThat(target, contains(2, 4, 6, 8, 10));

        // when:
        source.remove(1, 4);

        // then:
        assertThat(source, contains(1, 5));
        assertThat(target, contains(2, 10));
    }

    @Test
    public void testOperationsWithObservableBean() {
        // given:
        ObservableList<ObservablePerson> source = new ElementObservableList<>(observableArrayList());
        Function<ObservablePerson, String> mapper = ObservablePerson::getName;
        ObservableList<String> target = new MappingObservableList<>(source, mapper);

        // expect:
        assertThat(target, empty());

        // when:
        source.addAll(
            new ObservablePerson("n1", "l1"),
            new ObservablePerson("n2", "l2"),
            new ObservablePerson("n3", "l3"));

        // then:
        assertThat(target, contains("n1", "n2", "n3"));

        // when:
        source.get(2).setLastname("l33");

        // then:
        assertThat(target, contains("n1", "n2", "n3"));

        // when:
        source.get(2).setName("n33");

        // then:
        assertThat(target, contains("n1", "n2", "n33"));
    }

    public static class ObservablePerson implements ElementObservableList.ObservableValueContainer {
        private final StringProperty name = new SimpleStringProperty(this, "name");
        private final StringProperty lastname = new SimpleStringProperty(this, "lastname");


        public ObservablePerson(String name, String lastname) {
            setName(name);
            setLastname(lastname);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getLastname() {
            return lastname.get();
        }

        public StringProperty lastnameProperty() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname.set(lastname);
        }

        @Nonnull
        @Override
        public ObservableValue<?>[] observableValues() {
            return new StringProperty[]{nameProperty(), lastnameProperty()};
        }
    }
}
