/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@SuppressWarnings("unchecked")
abstract class AbstractObservableStream<T> implements ObservableStream<T> {
    protected static final String ERROR_PREDICATE_NULL = "Argument 'predicate' must not be null";
    protected static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    protected static final String ERROR_COMPARATOR_NULL = "Argument 'comparator' must not be null";
    protected static final String ERROR_OBSERVABLE_NULL = "Argument 'observable' must not be null";
    protected static final String ERROR_ACCUMULATOR = "Argument 'accumulator' must not be null";
    protected static final String ERROR_SUPPLIER_NULL = "Argument 'supplier' must not be null";
    protected static final String ERROR_COMBINER_NULL = "Argument 'combiner' must not be null";
    protected static final String ERROR_IDENTITY_NULL = "Argument 'identity' must not be null";

    protected final Observable observable;
    protected final List<StreamOp> operations = new ArrayList<>();

    AbstractObservableStream(@Nonnull Observable observable, @Nonnull List<StreamOp> operations) {
        this.observable = requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        this.operations.addAll(operations);
    }

    @Nonnull
    protected abstract <E> ObservableStream<E> createInstance(@Nonnull List<StreamOp> operations);

    @Nonnull
    protected abstract Stream createStream();

    @Nonnull
    @Override
    public ObservableStream<T> limit(final long maxSize) {
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.limit(maxSize);
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> limit(@Nonnull final ObservableLongValue maxSize) {
        requireNonNull(maxSize, ERROR_OBSERVABLE_NULL);
        return createInstance(push(operations, new StreamOp() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.limit(maxSize.get());
            }

            @Nullable
            @Override
            public Observable dependency() {
                return maxSize;
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> skip(final long n) {
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.skip(n);
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> skip(@Nonnull final ObservableLongValue n) {
        requireNonNull(n, ERROR_OBSERVABLE_NULL);
        return createInstance(push(operations, new StreamOp() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.skip(n.get());
            }

            @Nullable
            @Override
            public Observable dependency() {
                return n;
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> distinct() {
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.distinct();
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> sorted() {
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.sorted();
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> sorted(@Nonnull final Comparator<? super T> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.sorted(comparator);
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> sorted(@Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createInstance(push(operations, new StreamOp() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                Comparator<? super T> c = comparator.getValue();
                requireNonNull(c, ERROR_COMPARATOR_NULL);
                return stream.sorted(c);
            }

            @Nullable
            @Override
            public Observable dependency() {
                return comparator;
            }
        }));
    }

    @Nonnull
    public ObservableStream<T> filter(@Nonnull final Predicate<? super T> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.filter(predicate);
            }
        }));
    }

    @Nonnull
    @Override
    public <R> ObservableStream<R> map(@Nonnull final Function<? super T, ? extends R> mapper) {
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.map(mapper);
            }
        }));
    }

    @Nonnull
    @Override
    public <R> ObservableStream<R> flatMap(@Nonnull final Function<? super T, ? extends ObservableStream<? extends R>> mapper) {
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createInstance(push(operations, new StreamOpAdapter() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                return stream.flatMap(mapper);
            }
        }));
    }

    @Nonnull
    @Override
    public ObservableStream<T> filter(@Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createInstance(push(operations, new StreamOp() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                Predicate<? super T> p = predicate.getValue();
                requireNonNull(p, ERROR_PREDICATE_NULL);
                return stream.filter(p);
            }

            @Nullable
            @Override
            public Observable dependency() {
                return predicate;
            }
        }));
    }

    @Nonnull
    @Override
    public <R> ObservableStream<R> map(@Nonnull final ObservableValue<Function<? super T, ? extends R>> mapper) {
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createInstance(push(operations, new StreamOp() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                Function<? super T, ? extends R> m = mapper.getValue();
                requireNonNull(m, ERROR_MAPPER_NULL);
                return stream.map(m);
            }

            @Nullable
            @Override
            public Observable dependency() {
                return mapper;
            }
        }));
    }

    @Nonnull
    @Override
    public <R> ObservableStream<R> flatMap(@Nonnull final ObservableValue<Function<? super T, ? extends ObservableStream<? extends R>>> mapper) {
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createInstance(push(operations, new StreamOp() {
            @Nonnull
            @Override
            public Stream apply(@Nonnull Stream stream) {
                Function<? super T, ? extends ObservableStream<? extends R>> m = mapper.getValue();
                requireNonNull(m, ERROR_MAPPER_NULL);
                return stream.flatMap(m);
            }

            @Nullable
            @Override
            public Observable dependency() {
                return mapper;
            }
        }));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> reduce(@Nonnull final BinaryOperator<T> accumulator) {
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        return createObjectBinding(() -> (T) stream().reduce(accumulator).orElse(null), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> reduce(@Nullable final T defaultValue, @Nonnull final BinaryOperator<T> accumulator) {
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        return createObjectBinding(() -> (T) stream().reduce(accumulator).orElse(defaultValue), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> reduce(@Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> accumulator) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        return createObjectBinding(() -> (T) stream().reduce(accumulator).orElseGet(supplier), dependencies());
    }

    @Nonnull
    @Override
    public <U> ObjectBinding<U> reduce(@Nullable final U identity, @Nonnull final BiFunction<U, ? super T, U> accumulator, @Nonnull final BinaryOperator<U> combiner) {
        requireNonNull(combiner, ERROR_COMBINER_NULL);
        return createObjectBinding(() -> (U) stream().reduce(identity, accumulator, combiner), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> reduce(@Nonnull final ObservableValue<BinaryOperator<T>> accumulator) {
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        return createObjectBinding(() -> {
            BinaryOperator<T> a = accumulator.getValue();
            requireNonNull(a, ERROR_ACCUMULATOR);
            return (T) stream().reduce(a).orElse(null);
        }, dependencies(accumulator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> reduce(@Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> accumulator) {
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        return createObjectBinding(() -> {
            BinaryOperator<T> a = accumulator.getValue();
            requireNonNull(a, ERROR_ACCUMULATOR);
            return (T) stream().reduce(a).orElse(defaultValue);
        }, dependencies(accumulator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> reduce(@Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> accumulator) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        return createObjectBinding(() -> {
            BinaryOperator<T> a = accumulator.getValue();
            requireNonNull(a, ERROR_ACCUMULATOR);
            return (T) stream().reduce(a).orElseGet(supplier);
        }, dependencies(accumulator));
    }

    @Nonnull
    @Override
    public <U> ObjectBinding<U> reduce(@Nonnull final ObservableValue<U> identity, @Nonnull final ObservableValue<BiFunction<U, ? super T, U>> accumulator, @Nonnull final ObservableValue<BinaryOperator<U>> combiner) {
        requireNonNull(identity, ERROR_IDENTITY_NULL);
        requireNonNull(accumulator, ERROR_ACCUMULATOR);
        requireNonNull(combiner, ERROR_COMBINER_NULL);
        return createObjectBinding(() -> {
            U i = identity.getValue();
            requireNonNull(i, ERROR_IDENTITY_NULL);
            BiFunction<U, ? super T, U> a = accumulator.getValue();
            requireNonNull(a, ERROR_ACCUMULATOR);
            BinaryOperator<U> c = combiner.getValue();
            requireNonNull(c, ERROR_COMBINER_NULL);
            return (U) stream().reduce(i, a, c);
        }, dependencies(identity, accumulator, combiner));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> min(@Nonnull final Comparator<? super T> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> (T) stream().min(comparator).orElse(null), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> max(@Nonnull final Comparator<? super T> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> (T) stream().max(comparator).orElse(null), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> min(@Nullable final T defaultValue, @Nonnull final Comparator<? super T> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> (T) stream().min(comparator).orElse(defaultValue), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> max(@Nullable final T defaultValue, @Nonnull final Comparator<? super T> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> (T) stream().max(comparator).orElse(defaultValue), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> min(@Nonnull final Supplier<T> supplier, @Nonnull final Comparator<? super T> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> (T) stream().min(comparator).orElseGet(supplier), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> max(@Nonnull final Supplier<T> supplier, @Nonnull final Comparator<? super T> comparator) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> (T) stream().max(comparator).orElseGet(supplier), dependencies());
    }

    @Nonnull
    @Override
    public BooleanBinding anyMatch(@Nonnull final Predicate<? super T> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> stream().anyMatch(predicate), dependencies());
    }

    @Nonnull
    @Override
    public BooleanBinding allMatch(@Nonnull final Predicate<? super T> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> stream().allMatch(predicate), dependencies());
    }

    @Nonnull
    @Override
    public BooleanBinding noneMatch(@Nonnull final Predicate<? super T> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> stream().noneMatch(predicate), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> min(@Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> {
            Comparator<? super T> c = comparator.getValue();
            requireNonNull(c, ERROR_COMPARATOR_NULL);
            return (T) stream().min(c).orElse(null);
        }, dependencies(comparator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> max(@Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> {
            Comparator<? super T> c = comparator.getValue();
            requireNonNull(c, ERROR_COMPARATOR_NULL);
            return (T) stream().max(c).orElse(null);
        }, dependencies(comparator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> min(@Nullable final T defaultValue, @Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> {
            Comparator<? super T> c = comparator.getValue();
            requireNonNull(c, ERROR_COMPARATOR_NULL);
            return (T) stream().min(c).orElse(defaultValue);
        }, dependencies(comparator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> max(@Nullable final T defaultValue, @Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        return createObjectBinding(() -> {
            Comparator<? super T> c = comparator.getValue();
            requireNonNull(c, ERROR_COMPARATOR_NULL);
            return (T) stream().max(c).orElse(defaultValue);
        }, dependencies(comparator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> min(@Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> {
            Comparator<? super T> c = comparator.getValue();
            requireNonNull(c, ERROR_COMPARATOR_NULL);
            return (T) stream().min(c).orElseGet(supplier);
        }, dependencies(comparator));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> max(@Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<Comparator<? super T>> comparator) {
        requireNonNull(comparator, ERROR_COMPARATOR_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> {
            Comparator<? super T> c = comparator.getValue();
            requireNonNull(c, ERROR_COMPARATOR_NULL);
            return (T) stream().max(c).orElseGet(supplier);
        }, dependencies(comparator));
    }

    @Nonnull
    @Override
    public BooleanBinding anyMatch(@Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> p = predicate.getValue();
            requireNonNull(p, ERROR_PREDICATE_NULL);
            return stream().anyMatch(p);
        }, dependencies(predicate));
    }

    @Nonnull
    @Override
    public BooleanBinding allMatch(@Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> p = predicate.getValue();
            requireNonNull(p, ERROR_PREDICATE_NULL);
            return stream().allMatch(p);
        }, dependencies(predicate));
    }

    @Nonnull
    @Override
    public BooleanBinding noneMatch(@Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> p = predicate.getValue();
            requireNonNull(p, ERROR_PREDICATE_NULL);
            return stream().noneMatch(p);
        }, dependencies(predicate));
    }

    @Nonnull
    @Override
    public ObjectBinding<T> findFirst() {
        return createObjectBinding(() -> (T) stream().findFirst().orElse(null), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> findFirst(@Nullable final T defaultValue) {
        return createObjectBinding(() -> (T) stream().findFirst().orElse(defaultValue), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> findFirst(@Nonnull final Supplier<T> supplier) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> (T) stream().findFirst().orElseGet(supplier), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> findAny() {
        return createObjectBinding(() -> (T) stream().findAny().orElse(null), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> findAny(@Nullable final T defaultValue) {
        return createObjectBinding(() -> (T) stream().findAny().orElse(defaultValue), dependencies());
    }

    @Nonnull
    @Override
    public ObjectBinding<T> findAny(@Nonnull final Supplier<T> supplier) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> (T) stream().findAny().orElseGet(supplier), dependencies());
    }

    @Nonnull
    private Stream stream() {
        Stream stream = createStream();

        for (StreamOp op : operations) {
            stream = op.apply(stream);
        }

        return stream;
    }

    @Nonnull
    private Observable[] dependencies(Observable... deps) {
        List<Observable> dependencies = new ArrayList<>();
        dependencies.add(observable);
        if (deps != null) {
            Collections.addAll(dependencies, deps);
        }

        for (StreamOp op : operations) {
            Observable dependency = op.dependency();
            if (dependency != null) {
                dependencies.add(dependency);
            }
        }

        return dependencies.toArray(new Observable[dependencies.size()]);
    }

    private static List<StreamOp> push(List<StreamOp> operations, StreamOp op) {
        List<StreamOp> ops = new ArrayList<>(operations);
        ops.add(op);
        return ops;
    }

    interface StreamOp {
        @Nonnull
        Stream apply(@Nonnull Stream stream);

        @Nullable
        Observable dependency();
    }

    static class StreamOpAdapter implements StreamOp {
        @Nonnull
        @Override
        public Stream apply(@Nonnull Stream stream) {
            return stream;
        }

        @Nullable
        @Override
        public Observable dependency() {
            return null;
        }
    }
}
