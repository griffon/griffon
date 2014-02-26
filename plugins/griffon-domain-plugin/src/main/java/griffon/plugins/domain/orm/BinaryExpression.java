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
package griffon.plugins.domain.orm;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
@ThreadSafe
public final class BinaryExpression implements Criterion {
    private final String propertyName;
    private final Operator operator;
    private final Object value;

    public BinaryExpression(@Nonnull String propertyName, @Nonnull Operator operator, @Nonnull Object value) {
        this.propertyName = requireNonBlank(propertyName, "Argument 'propertyName' cannot be blank");
        this.operator = requireNonNull(operator, "Argument 'operator' cannot be null");
        this.value = requireNonNull(value, "Argument 'value' cannot be null");
    }

    @Nonnull
    public String getPropertyName() {
        return propertyName;
    }

    @Nonnull
    public Object getValue() {
        return value;
    }

    @Nonnull
    public Operator getOperator() {
        return operator;
    }

    public String toString() {
        return propertyName + " " + operator + " " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryExpression that = (BinaryExpression) o;

        return operator == that.operator &&
            propertyName.equals(that.propertyName) &&
            value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = operator.hashCode();
        result = 31 * result + propertyName.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}