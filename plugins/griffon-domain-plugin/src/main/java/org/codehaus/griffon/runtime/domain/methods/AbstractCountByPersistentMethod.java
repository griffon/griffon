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
import griffon.plugins.domain.methods.CountByMethod;
import griffon.plugins.domain.orm.Criterion;
import griffon.plugins.domain.orm.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Andres Almiray
 */
public abstract class AbstractCountByPersistentMethod extends AbstractClausedStaticPersistentMethod implements CountByMethod {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCountByPersistentMethod.class);
    private static final String METHOD_PATTERN = "(" + METHOD_NAME + ")([A-Z]\\w*)";

    public AbstractCountByPersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler, Pattern.compile(METHOD_PATTERN), OPERATORS);
    }

    @Nullable
    @Override
    protected final <T extends GriffonDomain> Object doInvokeInternalWithExpressions(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments, @Nonnull List<GriffonMethodExpression> expressions, @Nullable String operatorInUse) {
        LOG.trace("{}.{}", domainClass.getClazz().getName(), methodName);
        final String operator = OPERATOR_OR.equals(operatorInUse) ? OPERATOR_OR : OPERATOR_AND;
        if (expressions.size() == 1) {
            return countBy(domainClass, methodName, expressions.get(0).getCriterion());
        } else {
            List<Criterion> criteria = new ArrayList<>();
            for (GriffonMethodExpression expr : expressions) {
                criteria.add(expr.getCriterion());
            }
            Criterion[] array = criteria.toArray(new Criterion[criteria.size()]);
            Criterion criterion = operator.equals(OPERATOR_OR) ? Restrictions.or(array) : Restrictions.and(array);
            return countBy(domainClass, methodName, criterion);
        }
    }

    protected <T extends GriffonDomain> int countBy(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Criterion criterion) {
        throw new UnsupportedDomainMethodException();
    }
}