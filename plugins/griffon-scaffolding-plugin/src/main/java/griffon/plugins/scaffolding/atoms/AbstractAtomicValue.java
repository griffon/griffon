/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.plugins.scaffolding.atoms;

import griffon.exceptions.NewInstanceException;
import griffon.plugins.scaffolding.AtomicValue;
import org.codehaus.griffon.runtime.core.AbstractObservable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.core.GriffonExceptionHandler.sanitize;

/**
 * @author Andres Almiray
 */
public abstract class AbstractAtomicValue extends AbstractObservable implements AtomicValue, Comparable<AtomicValue> {
    protected static final String ERROR_ARG_NULL = "Argument 'arg' cannot be null";

    protected Object value;

    @Nullable
    public Object getValue() {
        return value;
    }

    public void setValue(@Nullable Object value) {
        firePropertyChange("value", this.value, this.value = value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtomicValue)) return false;

        AtomicValue that = (AtomicValue) o;

        return !(value != null ? !value.equals(that.getValue()) : that.getValue() != null);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return null == value ? null : String.valueOf(value);
    }

    public int compareTo(AtomicValue other) {
        if (this == other) return 0;
        if (other == null || !getClass().isAssignableFrom(other.getClass())) {
            return -1;
        }
        Object otherValue = other.getValue();

        if (value == otherValue) return 0;
        if (value != null && otherValue == null) return -1;
        if (value == null && otherValue != null) return 1;
        if (value instanceof Comparable) {
            return ((Comparable) value).compareTo(otherValue);
        }
        if (otherValue instanceof Comparable) {
            return ((Comparable) otherValue).compareTo(value);
        }
        return -1;
    }

    @Nonnull
    public static AtomicValue wrap(@Nullable Object value, @Nonnull Class<?> atomicValueType) {
        try {
            AtomicValue atom = (AtomicValue) atomicValueType.newInstance();
            atom.setValue(value);
            return atom;
        } catch (InstantiationException | IllegalAccessException e) {
            NewInstanceException x = new NewInstanceException(atomicValueType, e);
            sanitize(x);
            throw x;
        }
    }
}
