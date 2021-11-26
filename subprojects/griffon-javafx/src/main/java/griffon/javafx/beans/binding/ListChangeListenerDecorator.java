/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import javafx.collections.ListChangeListener;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class ListChangeListenerDecorator<E> implements ListChangeListener<E> {
    private final ListChangeListener<E> delegate;

    public ListChangeListenerDecorator(ListChangeListener<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    protected final ListChangeListener<E> getDelegate() {
        return delegate;
    }

    @Override
    public void onChanged(final Change<? extends E> change) {
        delegate.onChanged(change);
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
