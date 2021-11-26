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
package griffon.javafx.collections.transformation;

import griffon.javafx.collections.ObservableSetBase;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.WeakSetChangeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public abstract class TransformationSet<E, F> extends ObservableSetBase<E> implements ObservableSet<E> {
    private final ObservableSet<? extends F> source;
    private SetChangeListener<F> sourceListener;

    protected TransformationSet(@Nonnull ObservableSet<? extends F> source) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
        source.addListener(new WeakSetChangeListener<>(getListener()));
    }

    public final ObservableSet<? extends F> getSource() {
        return source;
    }

    public SetChangeListener<F> getListener() {
        if (sourceListener == null) {
            sourceListener = TransformationSet.this::sourceChanged;
        }
        return sourceListener;
    }

    public final boolean isInTransformationChain(@Nullable ObservableSet<?> set) {
        if (source == set) {
            return true;
        }
        Set<?> currentSource = source;
        while (currentSource instanceof TransformationSet) {
            currentSource = ((TransformationSet) currentSource).source;
            if (currentSource == set) {
                return true;
            }
        }
        return false;
    }

    protected abstract void sourceChanged(SetChangeListener.Change<? extends F> c);
}
