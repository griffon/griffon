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

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractCriterionEvaluator implements CriterionEvaluator {
    @Override
    public final boolean eval(@Nonnull Object target, @Nonnull Object criterion) {
        requireNonNull(target, "Argument 'target' cannot be null");
        requireNonNull(criterion, "Argument 'criterion' cannot be null");

        if (criterion instanceof Criterion) {
            return evalAsCriterion(target, (Criterion) criterion);
        } else {
            return evalAsObject(target, criterion);
        }
    }

    protected boolean evalAsObject(@Nonnull Object target, @Nonnull Object criterion) {
        throw new UnsupportedOperationException("Can't handle criterion of type " + criterion.getClass().getName());
    }

    protected boolean evalAsCriterion(@Nonnull Object target, @Nonnull Object criterion) {
        if (criterion instanceof CompositeCriterion) {
            CompositeCriterion compositeCriterion = (CompositeCriterion) criterion;
            Operator op = compositeCriterion.getOperator();
            for (Criterion c : compositeCriterion.getCriteria()) {
                boolean result = eval(target, c);
                if (op == Operator.AND && !result) {
                    return false;
                } else if (op == Operator.OR && result) {
                    return true;
                }
            }
            return op == Operator.AND;
        } else if (criterion instanceof UnaryExpression) {
            return evalUnary((UnaryExpression) criterion, target);
        } else if (criterion instanceof BinaryExpression) {
            return evalBinary((BinaryExpression) criterion, target);
        } else if (criterion instanceof PropertyExpression) {
            return evalProperty((PropertyExpression) criterion, target);
        }
        throw new IllegalArgumentException("Don't know how to evaluate criterion " + criterion);
    }

    protected abstract boolean evalUnary(@Nonnull UnaryExpression criterion, @Nonnull Object target);

    protected abstract boolean evalBinary(@Nonnull BinaryExpression criterion, @Nonnull Object target);

    protected abstract boolean evalProperty(@Nonnull PropertyExpression criterion, @Nonnull Object target);
}
