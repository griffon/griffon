/*
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
package griffon.javafx.collections;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.collections.FXCollections.observableArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElementObservableListTest {
    @Test
    public void testOperations() {
        // given:
        ObservableList<ObservablePerson> source = observableArrayList();
        ObservableList<ObservablePerson> target = new ElementObservableList<>(source);
        final AtomicBoolean changed = new AtomicBoolean(false);
        ListChangeListener<ObservablePerson> listener = c -> changed.set(true);
        target.addListener(listener);

        // when:
        ObservablePerson person = new ObservablePerson(1, "Andres", "Almiray");
        source.add(person);

        // then:
        assertTrue(changed.get());

        // when:
        changed.set(false);
        person.setLastname("Jaramillo");

        // then:
        assertTrue(changed.get());

        // when:
        source.remove(person);
        changed.set(false);
        person.setLastname("Almiray");

        // then:
        assertFalse(changed.get());
    }


    public static class ObservablePerson implements ElementObservableList.PropertyContainer {
        private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
        private final StringProperty name = new SimpleStringProperty(this, "name");
        private final StringProperty lastname = new SimpleStringProperty(this, "lastname");

        public ObservablePerson(int id, String name, String lastname) {
            setId(id);
            setName(name);
            setLastname(lastname);
        }

        public int getId() {
            return id.get();
        }

        public IntegerProperty idProperty() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getLastname() {
            return lastname.get();
        }

        public StringProperty lastnameProperty() {
            return lastname;
        }

        public void setId(int id) {
            this.id.set(id);
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public void setLastname(String lastname) {
            this.lastname.set(lastname);
        }

        @Override
        public Property<?>[] properties() {
            return new Property<?>[]{
                idProperty(),
                nameProperty(),
                lastnameProperty()
            };
        }
    }
}
