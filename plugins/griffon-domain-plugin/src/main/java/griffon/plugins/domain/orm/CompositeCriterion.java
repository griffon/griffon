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
import java.util.Arrays;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
@ThreadSafe
public final class CompositeCriterion implements Criterion {
    private final Criterion[] criteria;
    private final Operator operator;

    public CompositeCriterion(@Nonnull Criterion... criteria) {
        this(Operator.AND, criteria);
    }

    public CompositeCriterion(@Nonnull Operator operator, @Nonnull Criterion... criteria) {
        requireNonNull(operator, "Argument 'operator' cannot be null");
        requireNonNull(criteria, "Argument 'criteria' cannot be null");
        requireState(criteria.length > 0, "There must be at least one Criterion");
        requireState(operator == Operator.AND || operator == Operator.OR, "Invalid operator '" + operator + "'. Allowed operators are AND, OR.");

        this.criteria = new Criterion[criteria.length];
        System.arraycopy(criteria, 0, this.criteria, 0, criteria.length);
        this.operator = operator;
    }

    @Nonnull
    public Criterion[] getCriteria() {
        Criterion[] tmp = new Criterion[criteria.length];
        System.arraycopy(criteria, 0, tmp, 0, criteria.length);
        return tmp;
    }

    @Nonnull
    public Operator getOperator() {
        return this.operator;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CompositeCriterion");
        sb.append("{criteria=").append(criteria == null ? "null" : Arrays.asList(criteria).toString());
        sb.append(", operator=").append(operator);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeCriterion that = (CompositeCriterion) o;

        return Arrays.equals(criteria, that.criteria) &&
            operator == that.operator;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(criteria);
        result = 31 * result + operator.hashCode();
        return result;
    }
}
