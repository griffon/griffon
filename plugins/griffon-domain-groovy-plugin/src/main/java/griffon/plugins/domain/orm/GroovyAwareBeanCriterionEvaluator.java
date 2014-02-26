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

import groovy.lang.Closure;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public class GroovyAwareBeanCriterionEvaluator extends BeanCriterionEvaluator {
    @Override
    protected boolean evalAsObject(@Nonnull Object target, @Nonnull Object criterion) {
        if (criterion instanceof Closure) {
            return evalAsCriterion(target, buildCriterion((Closure) criterion));
        }
        return super.evalAsCriterion(target, criterion);
    }

    @Nonnull
    private Criterion buildCriterion(@Nonnull Closure criteria) {
        Criterion criterion = null;

        try {
            criteria.setDelegate(new CriteriaBuilder());
            criterion = (Criterion) criteria.call();
        } catch (ClassCastException cce) {
            criteria.setDelegate(new CriteriaBuilder());
            criterion = (Criterion) criteria.call();
        }

        return criterion;
    }
}
