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

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
class AbstractUIThreadAwareObservableNumberValue<T> implements ObservableNumberValue, UIThreadAware {
    private final ObservableNumberValue delegate;

    AbstractUIThreadAwareObservableNumberValue(ObservableNumberValue delegate) {
        this.delegate = delegate;
    }

    @Nonnull
    public ObservableNumberValue getDelegate() {
        return delegate;
    }

    @Override
    public int intValue() {
        return delegate.intValue();
    }

    @Override
    public long longValue() {
        return delegate.longValue();
    }

    @Override
    public float floatValue() {
        return delegate.floatValue();
    }

    @Override
    public double doubleValue() {
        return delegate.doubleValue();
    }

    @Override
    public Number getValue() {
        return delegate.getValue();
    }

    @Override
    public void addListener(ChangeListener<? super Number> listener) {
        if (listener instanceof UIThreadAware) {
            delegate.addListener(listener);
        } else {
            delegate.addListener(new UIThreadAwareChangeListener<>(listener));
        }
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
        if (listener instanceof UIThreadAware) {
            delegate.removeListener(listener);
        } else {
            delegate.removeListener(new UIThreadAwareChangeListener<>(listener));
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (listener instanceof UIThreadAware) {
            delegate.addListener(listener);
        } else {
            delegate.addListener(new UIThreadAwareInvalidationListener(listener));
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        if (listener instanceof UIThreadAware) {
            delegate.removeListener(listener);
        } else {
            delegate.removeListener(new UIThreadAwareInvalidationListener(listener));
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
