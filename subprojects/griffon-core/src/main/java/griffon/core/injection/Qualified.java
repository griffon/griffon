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
package griffon.core.injection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class Qualified<T> {
    private final Annotation qualifier;
    private final T instance;

    public Qualified(@Nonnull T instance, @Nullable Annotation qualifier) {
        this.instance = requireNonNull(instance, "Argument 'instance' must not be null");
        this.qualifier = qualifier;
    }

    @Nullable
    public Annotation getQualifier() {
        return qualifier;
    }

    @Nonnull
    public T getInstance() {
        return instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qualified that = (Qualified) o;

        return instance.equals(that.instance) &&
            !(qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null);
    }

    @Override
    public int hashCode() {
        int result = qualifier != null ? qualifier.hashCode() : 0;
        result = 31 * result + instance.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Qualified{");
        sb.append("qualifier=").append(qualifier);
        sb.append(", instance=").append(instance);
        sb.append('}');
        return sb.toString();
    }
}
