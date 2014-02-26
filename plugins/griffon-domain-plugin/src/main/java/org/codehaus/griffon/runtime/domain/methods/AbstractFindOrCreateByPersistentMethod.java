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
package org.codehaus.griffon.runtime.domain.methods;

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.FindOrCreateByMethod;
import griffon.plugins.domain.orm.BinaryExpression;
import griffon.plugins.domain.orm.CompositeCriterion;
import griffon.plugins.domain.orm.Criterion;
import griffon.plugins.domain.orm.Restrictions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Andres Almiray
 */
public abstract class AbstractFindOrCreateByPersistentMethod extends AbstractClausedStaticPersistentMethod implements FindOrCreateByMethod {
    private static final String METHOD_PATTERN = "(" + METHOD_NAME + ")([A-Z]\\w*)";

    public AbstractFindOrCreateByPersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler, Pattern.compile(METHOD_PATTERN), OPERATORS);
    }

    @Override
    protected boolean isStrict() {
        return true;
    }

    @Nullable
    @Override
    protected final <T extends GriffonDomain> Object doInvokeInternalWithExpressions(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments, @Nonnull List<GriffonMethodExpression> expressions, @Nullable String operatorInUse) {
        if (expressions.size() == 1) {
            return findOrCreateBy(domainClass, methodName, expressions.get(0).getCriterion());
        } else {
            List<Criterion> criteria = new ArrayList<Criterion>();
            for (GriffonMethodExpression expr : expressions) {
                criteria.add(expr.getCriterion());
            }
            Criterion[] array = criteria.toArray(new Criterion[criteria.size()]);
            Criterion criterion = Restrictions.and(array);
            return findOrCreateBy(domainClass, methodName, criterion);
        }
    }

    @Nullable
    protected <T extends GriffonDomain> T findOrCreateBy(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Criterion criterion) {
        throw new UnsupportedDomainMethodException();
    }

    @Nonnull
    protected Map<String, Object> criterionToMap(@Nonnull Criterion criterion) {
        Map<String, Object> props = new LinkedHashMap<>();
        harvestProperties(criterion, props);
        return props;
    }

    private void harvestProperties(@Nonnull Criterion criterion, @Nonnull Map<String, Object> props) {
        if (criterion instanceof CompositeCriterion) {
            CompositeCriterion cc = (CompositeCriterion) criterion;
            for (Criterion c : cc.getCriteria()) {
                harvestProperties(c, props);
            }
        } else if (criterion instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) criterion;
            props.put(be.getPropertyName(), be.getValue());
        }
    }
}