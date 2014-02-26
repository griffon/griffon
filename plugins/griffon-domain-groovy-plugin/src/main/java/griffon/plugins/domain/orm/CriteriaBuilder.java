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

import groovy.util.AbstractFactory;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public final class CriteriaBuilder extends FactoryBuilderSupport {
    private static final String COMPOSITE_CRITERION = "_COMPOSITE_CRITERION_";
    private final Factory criterionFactory = new CriterionFactory();

    public CriteriaBuilder() {
        super();
        registerFactory(Operator.AND.op().toLowerCase(), new CompositeCriterionFactory(Operator.AND));
        registerFactory(Operator.OR.op().toLowerCase(), new CompositeCriterionFactory(Operator.OR));
        registerFactory("eq", new BinaryExpressionFactory(Operator.EQUAL));
        registerFactory("ne", new BinaryExpressionFactory(Operator.NOT_EQUAL));
        registerFactory("gt", new BinaryExpressionFactory(Operator.GREATER_THAN));
        registerFactory("ge", new BinaryExpressionFactory(Operator.GREATER_THAN_OR_EQUAL));
        registerFactory("lt", new BinaryExpressionFactory(Operator.LESS_THAN));
        registerFactory("le", new BinaryExpressionFactory(Operator.LESS_THAN_OR_EQUAL));
        // registerFactory("like", new RestrictionFactory(Operator.LIKE));
        // registerFactory("notLike", new RestrictionFactory(Operator.NOT_LIKE));
    }

    protected Factory resolveFactory(Object name, Map attributes, Object value) {
        Factory factory = super.resolveFactory(name, attributes, value);
        if (factory != null) {
            return factory;
        }
        return criterionFactory;
    }

    protected Object postNodeCompletion(Object parent, Object node) {
        node = super.postNodeCompletion(parent, node);
        Criterion criterion = (Criterion) getContext().remove(COMPOSITE_CRITERION);
        return criterion != null ? criterion : node;
    }

    private static class BinaryExpressionFactory extends AbstractFactory {
        private static final String PROPERTY_NAME = "propertyName";
        private static final String OTHER_PROPERTY_NAME = "otherPropertyName";
        private static final String VALUE = "value";
        private final Operator operator;

        public BinaryExpressionFactory(Operator operator) {
            this.operator = operator;
        }

        public boolean isLeaf() {
            return true;
        }

        public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties)
            throws InstantiationException, IllegalAccessException {
            FactoryBuilderSupport.checkValueIsNull(value, name);

            Object propertyName = properties.remove(PROPERTY_NAME);
            if (propertyName == null) {
                throw new IllegalArgumentException("In " + name + " you must specify a value for " + PROPERTY_NAME + ":");
            }

            Object otherPropertyName = properties.remove(OTHER_PROPERTY_NAME);
            value = properties.remove(VALUE);

            if (otherPropertyName != null) {
                return new PropertyExpression(propertyName.toString(), operator, otherPropertyName.toString());
            } else if (value != null) {
                return new BinaryExpression(propertyName.toString(), operator, value);
            }
            throw new IllegalArgumentException("In " + name + " you must specify a value for " + OTHER_PROPERTY_NAME + ": or " + VALUE + ":");
        }
    }

    private static class CompositeCriterionFactory extends AbstractFactory {
        private final Operator operator;

        public CompositeCriterionFactory(Operator operator) {
            this.operator = operator;
        }

        public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties)
            throws InstantiationException, IllegalAccessException {
            if (value != null) {
                throw new IllegalArgumentException("Can't set a value on '" + operator.op().toLowerCase() + "' node.");
            }
            if (properties != null && !properties.isEmpty()) {
                throw new IllegalArgumentException("Can't set properties on '" + operator.op().toLowerCase() + "' node.");
            }

            return new ArrayList();
        }

        public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
            if (child instanceof Criterion) {
                ((List) parent).add(child);
            }
        }

        public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            List<Criterion> list = (List<Criterion>) node;
            Criterion criterion = new CompositeCriterion(operator, list.toArray(new Criterion[list.size()]));
            if (parent instanceof List) {
                ((List) parent).add(criterion);
            }
            builder.getContext().put(COMPOSITE_CRITERION, criterion);
        }
    }

    private static class CriterionFactory extends AbstractFactory {
        private static final String OPERATOR = "operator";
        private static final String OP = "op";

        public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties)
            throws InstantiationException, IllegalAccessException {
            Operator operator = (Operator) properties.remove(OPERATOR);
            if (operator == null) operator = (Operator) properties.remove(OP);
            if (operator == null) operator = Operator.EQUAL;

            if (value == null) {
                if (operator == Operator.IS_NOT_NULL) {
                    return Restrictions.isNotNull(name.toString());
                } else {
                    return Restrictions.isNull(name.toString());
                }
            }

            switch (operator) {
                case EQUAL:
                    return Restrictions.eq(name.toString(), value);
                case NOT_EQUAL:
                    return Restrictions.ne(name.toString(), value);
                case GREATER_THAN:
                    return Restrictions.gt(name.toString(), value);
                case GREATER_THAN_OR_EQUAL:
                    return Restrictions.ge(name.toString(), value);
                case LESS_THAN:
                    return Restrictions.lt(name.toString(), value);
                case LESS_THAN_OR_EQUAL:
                    return Restrictions.le(name.toString(), value);
                case LIKE:
                    return Restrictions.like(name.toString(), value);
                case NOT_LIKE:
                    return Restrictions.notLike(name.toString(), value);
            }

            throw new IllegalArgumentException("Unsupported operator '" + operator.op().toLowerCase() + "'.");
        }

        public boolean isLeaf() {
            return true;
        }
    }
}
