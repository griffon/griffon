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

/**
 * @author Andres Almiray
 */
public final class Restrictions {
    @Nonnull
    public static CompositeCriterion and(@Nonnull Criterion lhs, @Nonnull Criterion rhs) {
        return new CompositeCriterion(Operator.AND, lhs, rhs);
    }

    @Nonnull
    public static CompositeCriterion and(@Nonnull Criterion... criteria) {
        return new CompositeCriterion(Operator.AND, criteria);
    }

    @Nonnull
    public static CompositeCriterion or(@Nonnull Criterion lhs, @Nonnull Criterion rhs) {
        return new CompositeCriterion(Operator.OR, lhs, rhs);
    }

    @Nonnull
    public static CompositeCriterion or(@Nonnull Criterion... criteria) {
        return new CompositeCriterion(Operator.OR, criteria);
    }

    @Nonnull
    public static BinaryExpression eq(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.EQUAL, value);
    }

    @Nonnull
    public static BinaryExpression ne(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.NOT_EQUAL, value);
    }

    @Nonnull
    public static BinaryExpression gt(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.GREATER_THAN, value);
    }

    @Nonnull
    public static BinaryExpression ge(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.GREATER_THAN_OR_EQUAL, value);
    }

    @Nonnull
    public static BinaryExpression lt(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.LESS_THAN, value);
    }

    @Nonnull
    public static BinaryExpression le(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.LESS_THAN_OR_EQUAL, value);
    }

    @Nonnull
    public static BinaryExpression like(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.LIKE, value);
    }

    @Nonnull
    public static BinaryExpression notLike(@Nonnull String propertyName, @Nonnull Object value) {
        return new BinaryExpression(propertyName, Operator.NOT_LIKE, value);
    }

    @Nonnull
    public static PropertyExpression eqProperty(@Nonnull String propertyName, @Nonnull String otherPropertyName) {
        return new PropertyExpression(propertyName, Operator.EQUAL, otherPropertyName);
    }

    @Nonnull
    public static PropertyExpression neProperty(@Nonnull String propertyName, @Nonnull String otherPropertyName) {
        return new PropertyExpression(propertyName, Operator.NOT_EQUAL, otherPropertyName);
    }

    @Nonnull
    public static PropertyExpression gtProperty(@Nonnull String propertyName, @Nonnull String otherPropertyName) {
        return new PropertyExpression(propertyName, Operator.GREATER_THAN, otherPropertyName);
    }

    @Nonnull
    public static PropertyExpression geProperty(@Nonnull String propertyName, @Nonnull String otherPropertyName) {
        return new PropertyExpression(propertyName, Operator.GREATER_THAN_OR_EQUAL, otherPropertyName);
    }

    @Nonnull
    public static PropertyExpression ltProperty(@Nonnull String propertyName, @Nonnull String otherPropertyName) {
        return new PropertyExpression(propertyName, Operator.LESS_THAN, otherPropertyName);
    }

    @Nonnull
    public static PropertyExpression leProperty(@Nonnull String propertyName, @Nonnull String otherPropertyName) {
        return new PropertyExpression(propertyName, Operator.LESS_THAN_OR_EQUAL, otherPropertyName);
    }

    @Nonnull
    public static UnaryExpression isNull(@Nonnull String propertyName) {
        return new UnaryExpression(propertyName, Operator.IS_NULL);
    }

    @Nonnull
    public static UnaryExpression isNotNull(@Nonnull String propertyName) {
        return new UnaryExpression(propertyName, Operator.IS_NOT_NULL);
    }

    @Nonnull
    public static Criterion not(@Nonnull Criterion criterion) {
        if (criterion instanceof BinaryExpression) {
            BinaryExpression exp = (BinaryExpression) criterion;
            switch (exp.getOperator()) {
                case EQUAL:
                    return ne(exp.getPropertyName(), exp.getValue());
                case NOT_EQUAL:
                    return eq(exp.getPropertyName(), exp.getValue());
                case GREATER_THAN:
                    return le(exp.getPropertyName(), exp.getValue());
                case GREATER_THAN_OR_EQUAL:
                    return lt(exp.getPropertyName(), exp.getValue());
                case LESS_THAN:
                    return ge(exp.getPropertyName(), exp.getValue());
                case LESS_THAN_OR_EQUAL:
                    return gt(exp.getPropertyName(), exp.getValue());
                case LIKE:
                    return notLike(exp.getPropertyName(), exp.getValue());
                case NOT_LIKE:
                    return like(exp.getPropertyName(), exp.getValue());
            }
        } else if (criterion instanceof PropertyExpression) {
            PropertyExpression exp = (PropertyExpression) criterion;
            switch (exp.getOperator()) {
                case EQUAL:
                    return neProperty(exp.getPropertyName(), exp.getOtherPropertyName());
                case NOT_EQUAL:
                    return eqProperty(exp.getPropertyName(), exp.getOtherPropertyName());
                case GREATER_THAN:
                    return leProperty(exp.getPropertyName(), exp.getOtherPropertyName());
                case GREATER_THAN_OR_EQUAL:
                    return ltProperty(exp.getPropertyName(), exp.getOtherPropertyName());
                case LESS_THAN:
                    return geProperty(exp.getPropertyName(), exp.getOtherPropertyName());
                case LESS_THAN_OR_EQUAL:
                    return gtProperty(exp.getPropertyName(), exp.getOtherPropertyName());
            }
        } else if (criterion instanceof UnaryExpression) {
            UnaryExpression exp = (UnaryExpression) criterion;
            switch (exp.getOperator()) {
                case IS_NULL:
                    return isNotNull(exp.getPropertyName());
                case IS_NOT_NULL:
                    return isNull(exp.getPropertyName());
            }
        } else if (criterion instanceof CompositeCriterion) {
            CompositeCriterion cc = (CompositeCriterion) criterion;
            Operator op = cc.getOperator() == Operator.AND ? Operator.OR : Operator.AND;
            Criterion[] criteria = new Criterion[cc.getCriteria().length];
            for (int i = 0; i < criteria.length; i++) {
                criteria[i] = not(cc.getCriteria()[i]);
            }
            return new CompositeCriterion(op, criteria);
        }

        return criterion;
    }
}
