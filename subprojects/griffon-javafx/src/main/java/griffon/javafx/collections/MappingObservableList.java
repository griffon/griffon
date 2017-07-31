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

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public class MappingObservableList<T, S> extends TransformationList<T, S> {
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    private static final String ERROR_SOURCE_NULL = "Argument 'source' must not be null";

    private T[] elements;
    private Function<S, T> mapper;
    private ObservableValue<Function<S, T>> observableMapper;

    public MappingObservableList(@Nonnull ObservableList<? extends S> source, @Nonnull Function<S, T> mapper) {
        super(requireNonNull(source, ERROR_SOURCE_NULL));
        this.mapper = requireNonNull(mapper, ERROR_MAPPER_NULL);
        int size = source.size();
        this.elements = (T[]) new Object[size];
        for (int i = 0; i < size; ++i) {
            this.elements[i] = mapper.apply(source.get(i));
        }
    }

    public MappingObservableList(@Nonnull ObservableList<? extends S> source, @Nonnull ObservableValue<Function<S, T>> mapper) {
        super(requireNonNull(source, ERROR_SOURCE_NULL));
        this.observableMapper = requireNonNull(mapper, ERROR_MAPPER_NULL);
        int size = source.size();
        this.elements = (T[]) new Object[size];
        Function<S, T> function = resolveMapper();

        for (int i = 0; i < size; ++i) {
            this.elements[i] = function.apply(source.get(i));
        }

        mapper.addListener((v, o, n) -> updateAll());
    }

    @Nonnull
    protected Function<S, T> resolveMapper() {
        Function<S, T> function = observableMapper != null ? observableMapper.getValue() : mapper;
        return requireNonNull(function, ERROR_MAPPER_NULL);
    }

    @Override
    public int getSourceIndex(int index) {
        return index;
    }

    @Override
    public T get(int index) {
        return elements[index];
    }

    @Override
    public int size() {
        return getSource().size();
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends S> c) {
        beginChange();
        while (c.next()) {
            if (c.wasPermutated()) {
                permutate(c);
            } else if (c.wasReplaced()) {
                replace(c);
            } else if (c.wasUpdated()) {
                update(c);
            } else if (c.wasAdded()) {
                add(c);
            } else if (c.wasRemoved()) {
                remove(c);
            }
        }
        endChange();
    }

    private void permutate(ListChangeListener.Change<? extends S> c) {
        int from = c.getFrom();
        int to = c.getTo();
        int[] perms = new int[from - to];
        Function<S, T> function = resolveMapper();

        for (int i = from, j = 0; i < to; i++) {
            perms[j++] = c.getPermutation(i);
            elements[i] = function.apply(c.getList().get(i));
        }
        nextPermutation(from, to, perms);
    }

    private void replace(ListChangeListener.Change<? extends S> c) {
        int from = c.getFrom();
        int to = c.getTo();
        List<T> removed = new ArrayList<>();
        Function<S, T> function = resolveMapper();

        for (int i = from; i < to; i++) {
            elements[i] = function.apply(c.getList().get(i));
            removed.add(elements[i]);
        }
        nextReplace(from, to, removed);
    }

    private void update(ListChangeListener.Change<? extends S> c) {
        int from = c.getFrom();
        int to = c.getTo();
        Function<S, T> function = resolveMapper();

        for (int i = from; i < to; i++) {
            elements[i] = function.apply(c.getList().get(i));
            nextUpdate(i);
        }
    }

    private void add(ListChangeListener.Change<? extends S> c) {
        int from = 0;
        int to = c.getAddedSize();
        int offset = elements.length;
        T[] tmp = Arrays.copyOf(elements, offset + to);
        Function<S, T> function = resolveMapper();

        for (int i = 0; i < to; ++i) {
            tmp[offset + i] = function.apply(c.getAddedSubList().get(i));
        }

        elements = tmp;
        nextAdd(offset + from, offset + to);
    }

    private void remove(ListChangeListener.Change<? extends S> c) {
        int from = c.getFrom();
        int size = elements.length - c.getRemovedSize();
        int to = c.getTo();
        to = to == from ? from + c.getRemovedSize() - 1 : to;
        List<T> removed = new ArrayList<>();
        T[] tmp = (T[]) new Object[size];

        for (int i = 0, j = 0; i < elements.length; i++) {
            if (i < from || i > to) {
                tmp[j++] = elements[i];
            } else {
                removed.add(elements[i]);
            }
        }

        elements = tmp;
        nextRemove(from, removed);
    }

    private void updateAll() {
        Function<S, T> function = resolveMapper();
        // defensive copying
        List<S> copy = new ArrayList<>(getSource());
        List<T> removed = asList(elements);
        T[] tmp = (T[]) new Object[removed.size()];

        beginChange();
        for (int i = 0; i < removed.size(); i++) {
            tmp[i] = function.apply(copy.get(i));
        }
        elements = tmp;
        nextReplace(0, elements.length, removed);
        endChange();
    }
}
