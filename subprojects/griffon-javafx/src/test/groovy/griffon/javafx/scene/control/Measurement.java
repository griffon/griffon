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
package griffon.javafx.scene.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Measurement {
    private final StringProperty name = new SimpleStringProperty(this, "name", "");
    private final IntegerProperty amount = new SimpleIntegerProperty(this, "amount", 0);
    private final ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>(this, "timestamp", LocalDateTime.now());

    public Measurement(String name, int amount) {
        this.name.set(name);
        this.amount.set(amount);
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public ReadOnlyIntegerProperty amountProperty() {
        return amount;
    }

    public ReadOnlyObjectProperty<LocalDateTime> timestampProperty() {
        return timestamp;
    }

    public String getName() {
        return name.get();
    }

    public int getAmount() {
        return amount.get();
    }

    public LocalDateTime getTimestamp() {
        return timestamp.get();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Measurement{");
        sb.append("name=").append(getName());
        sb.append(", amount=").append(getAmount());
        sb.append(", timestamp=").append(getTimestamp());
        sb.append('}');
        return sb.toString();
    }
}
