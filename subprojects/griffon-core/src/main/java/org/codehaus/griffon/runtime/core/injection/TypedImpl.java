/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.injection;

import griffon.inject.Typed;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
public class TypedImpl implements Typed, Serializable {
    private final Class<?> value;

    public TypedImpl(Class<?> value) {
        this.value = requireNonNull(value, "artifact");
    }

    public Class<?> value() {
        return this.value;
    }

    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ value.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Typed)) {
            return false;
        }

        Typed other = (Typed) o;
        return value.equals(other.value());
    }

    public String toString() {
        return "@" + Typed.class.getName() + "(value=" + value + ")";
    }

    public Class<? extends Annotation> annotationType() {
        return Typed.class;
    }

    private static final long serialVersionUID = 0;
}
