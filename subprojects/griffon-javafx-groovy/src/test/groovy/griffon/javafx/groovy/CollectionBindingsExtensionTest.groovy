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
package griffon.javafx.groovy

import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import org.junit.jupiter.api.Test

import java.util.function.Function

import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertThat

class CollectionBindingsExtensionTest {
    @Test
    void "Join list with string delimiter"() {
        ObservableList<Object> items = FXCollections.observableArrayList()
        StringBinding joined = items.join(', ')

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('A'))

        items << 1
        assertThat(joined.get(), equalTo('A, 1'))

        items << Runnable
        assertThat(joined.get(), equalTo('A, 1, interface java.lang.Runnable'))
    }

    @Test
    void "Join list with observable delimiter"() {
        ObservableList<Object> items = FXCollections.observableArrayList()
        StringProperty delimiter = new SimpleStringProperty(', ')
        StringBinding joined = items.join(delimiter)

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('A'))

        items << 1
        assertThat(joined.get(), equalTo('A, 1'))

        items << Runnable
        assertThat(joined.get(), equalTo('A, 1, interface java.lang.Runnable'))

        delimiter.set(':')
        assertThat(joined.get(), equalTo('A:1:interface java.lang.Runnable'))
    }

    @Test
    void "Join list with string delimiter and mapper"() {
        ObservableList<Object> items = FXCollections.observableArrayList()
        Function<String, String> mapper = { s -> '"' + s + '"' }
        StringBinding joined = items.join(', ', mapper)

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('"A"'))

        items << 1
        assertThat(joined.get(), equalTo('"A", "1"'))

        items << Runnable
        assertThat(joined.get(), equalTo('"A", "1", "interface java.lang.Runnable"'))
    }

    @Test
    void "Join list with observable delimiter and mapper"() {
        ObservableList<Object> items = FXCollections.observableArrayList()
        StringProperty delimiter = new SimpleStringProperty(', ')
        Function<String, String> function = { s -> '"' + s + '"' }
        ObjectProperty<Function<String, String>> mapper = new SimpleObjectProperty(function)
        StringBinding joined = items.join(delimiter, mapper)

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('"A"'))

        items << 1
        assertThat(joined.get(), equalTo('"A", "1"'))

        items << Runnable
        assertThat(joined.get(), equalTo('"A", "1", "interface java.lang.Runnable"'))

        delimiter.set(':')
        assertThat(joined.get(), equalTo('"A":"1":"interface java.lang.Runnable"'))

        mapper.set({ s -> '[' + s + ']' } as Function)
        assertThat(joined.get(), equalTo('[A]:[1]:[interface java.lang.Runnable]'))
    }

    @Test
    void "Join set with string delimiter"() {
        ObservableSet<Object> items = FXCollections.observableSet()
        StringBinding joined = items.join(', ')

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('A'))

        items << 1
        assert joined.get() == 'A, 1' || joined.get() == '1, A'
    }

    @Test
    void "Join set with observable delimiter"() {
        ObservableSet<Object> items = FXCollections.observableSet()
        StringProperty delimiter = new SimpleStringProperty(', ')
        StringBinding joined = items.join(delimiter)

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('A'))

        items << 1
        assert joined.get() == 'A, 1' || joined.get() == '1, A'

        delimiter.set(':')
        assert joined.get() == 'A:1' || joined.get() == '1:A'
    }


    @Test
    void "Join set with string delimiter and mapper"() {
        ObservableSet<Object> items = FXCollections.observableSet()
        Function<String, String> mapper = { s -> '"' + s + '"' }
        StringBinding joined = items.join(', ', mapper)

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('"A"'))

        items << 1
        assert joined.get() == '"A", "1"' || joined.get() == '"1", "A"'
    }

    @Test
    void "Join set with observable delimiter and mapper"() {
        ObservableSet<Object> items = FXCollections.observableSet()
        StringProperty delimiter = new SimpleStringProperty(', ')
        Function<String, String> function = { s -> '"' + s + '"' }
        ObjectProperty<Function<String, String>> mapper = new SimpleObjectProperty(function)
        StringBinding joined = items.join(delimiter, mapper)

        assertThat(joined.get(), equalTo(''))

        items << 'A'
        assertThat(joined.get(), equalTo('"A"'))

        items << 1
        assert joined.get() == '"A", "1"' || joined.get() == '"1", "A"'

        delimiter.set(':')
        assert joined.get() == '"A":"1"' || joined.get() == '"1":"A"'

        mapper.set({ s -> '[' + s + ']' } as Function)
        assert joined.get() == '[A]:[1]' || joined.get() == '[1]:[A]"'
    }

    @Test
    void "Join map with string delimiter"() {
        ObservableMap<Object, Object> items = FXCollections.observableHashMap()
        StringBinding joined = items.join('; ')

        assertThat(joined.get(), equalTo(''))

        items.key1 = 'value1'
        assertThat(joined.get(), equalTo('key1=value1'))

        items.key2 = 'value2'
        assertThat(joined.get(), equalTo('key1=value1; key2=value2'))
    }

    @Test
    void "Join map with observable delimiter"() {
        ObservableMap<Object, Object> items = FXCollections.observableHashMap()
        StringProperty delimiter = new SimpleStringProperty('; ')
        StringBinding joined = items.join(delimiter)

        assertThat(joined.get(), equalTo(''))

        items.key1 = 'value1'
        assertThat(joined.get(), equalTo('key1=value1'))

        items.key2 = 'value2'
        assertThat(joined.get(), equalTo('key1=value1; key2=value2'))

        delimiter.set(', ')
        assertThat(joined.get(), equalTo('key1=value1, key2=value2'))
    }

    @Test
    void "Join map with string delimiter and mapper"() {
        ObservableMap<Object, Object> items = FXCollections.observableHashMap()
        Function<Map.Entry<Object, Object>, String> function = { e -> e.key + ':' + e.value }
        StringBinding joined = items.join('; ', function)

        assertThat(joined.get(), equalTo(''))

        items.key1 = 'value1'
        assertThat(joined.get(), equalTo('key1:value1'))

        items.key2 = 'value2'
        assertThat(joined.get(), equalTo('key1:value1; key2:value2'))
    }

    @Test
    void "Join map with observable delimiter and mapper"() {
        ObservableMap<Object, Object> items = FXCollections.observableHashMap()
        StringProperty delimiter = new SimpleStringProperty('; ')
        Function<Map.Entry<Object, Object>, String> function = { e -> e.key + '=' + e.value }
        ObjectProperty<Function<Map.Entry<Object, Object>, String>> mapper = new SimpleObjectProperty<>(function)
        StringBinding joined = items.join(delimiter, mapper)

        assertThat(joined.get(), equalTo(''))

        items.key1 = 'value1'
        assertThat(joined.get(), equalTo('key1=value1'))

        items.key2 = 'value2'
        assertThat(joined.get(), equalTo('key1=value1; key2=value2'))

        delimiter.set(', ')
        assertThat(joined.get(), equalTo('key1=value1, key2=value2'))

        mapper.set({ e -> e.key + ':' + e.value } as Function)
        assertThat(joined.get(), equalTo('key1:value1, key2:value2'))
    }
}
