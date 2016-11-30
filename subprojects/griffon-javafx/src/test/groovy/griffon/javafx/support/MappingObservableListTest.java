/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.javafx.support;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class MappingObservableListTest {
    @Test
    public void testOperations() {
        // given:
        ObservableList<String> source = FXCollections.observableArrayList();
        Function<String, Integer> mapper = Integer::valueOf;
        ObservableList<Integer> target = MappingBindings.mapList(source, mapper);

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
        ObservableList<Integer> source = FXCollections.observableArrayList();
        Function<Integer, Integer> function = Function.identity();
        ObjectProperty<Function<Integer, Integer>> mapper = new SimpleObjectProperty<>(function);
        ObservableList<Integer> target = MappingBindings.mapList(source, mapper);

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
        System.out.println(source);
        System.out.println(target);

        // then:
        assertThat(source, contains(1, 5));
        assertThat(target, contains(2, 10));
    }
}
