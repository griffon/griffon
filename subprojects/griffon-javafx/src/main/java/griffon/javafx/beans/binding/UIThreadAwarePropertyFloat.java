/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
class UIThreadAwarePropertyFloat extends AbstractUIThreadAwareProperty<Float> implements Property<Float> {
    UIThreadAwarePropertyFloat(@Nonnull Property<Float> delegate) {
        super(delegate);
    }

    @Override
    public void bind(ObservableValue<? extends Float> observable) {
        getDelegate().bind(observable);
    }

    @Override
    public Float getValue() {
        return getDelegate().getValue();
    }

    @Override
    public void setValue(final Float value) {
        if (isFxApplicationThread()) {
            getDelegate().setValue(value);
        } else {
            runLater(() -> getDelegate().setValue(value));
        }
    }
}
