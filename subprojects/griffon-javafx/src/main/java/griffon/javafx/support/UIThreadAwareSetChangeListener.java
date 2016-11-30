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

import javafx.collections.SetChangeListener;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
class UIThreadAwareSetChangeListener<E> implements SetChangeListener<E>, UIThreadAware {
    private final SetChangeListener<E> delegate;

    UIThreadAwareSetChangeListener(@Nonnull SetChangeListener<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Override
    public void onChanged(final Change<? extends E> change) {
        if (isFxApplicationThread()) {
            delegate.onChanged(change);
        } else {
            runLater(() -> onChanged(change));
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + delegate.toString();
    }
}
