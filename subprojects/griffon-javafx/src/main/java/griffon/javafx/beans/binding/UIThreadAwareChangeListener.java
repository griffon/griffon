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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
class UIThreadAwareChangeListener<T> extends ChangeListenerDecorator<T> implements UIThreadAware {
    UIThreadAwareChangeListener(@Nonnull ChangeListener<? super T> delegate) {
        super(delegate);
    }

    @Override
    public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue) {
        if (isFxApplicationThread()) {
            getDelegate().changed(observable, oldValue, newValue);
        } else {
            runLater(() -> getDelegate().changed(observable, oldValue, newValue));
        }
    }
}
