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
package griffon.plugins.domain.orm

import org.junit.Test

import static griffon.plugins.domain.orm.Restrictions.*

class BeanCriterionEvaluatorTests {
    private BeanCriterionEvaluator evaluator = new BeanCriterionEvaluator()

    @Test
    void testUnaryNull() {
        Bean bean = new Bean()
        assert evaluator.eval(bean, isNull('prop1'))
        assert !evaluator.eval(bean, not(isNull('prop1')))
    }

    @Test
    void testUnaryNotNull() {
        Bean bean = new Bean(prop1: 'prop1')
        assert evaluator.eval(bean, isNotNull('prop1'))
        assert !evaluator.eval(bean, not(isNotNull('prop1')))
    }

    @Test
    void testBinaryEqual() {
        Bean bean = new Bean(prop1: 'prop1')
        assert evaluator.eval(bean, eq('prop1', 'prop1'))
        assert !evaluator.eval(bean, not(eq('prop1', 'prop1')))
    }

    @Test
    void testBinaryNotEqual() {
        Bean bean = new Bean(prop1: 'prop2')
        assert evaluator.eval(bean, ne('prop1', 'prop1'))
        assert !evaluator.eval(bean, not(ne('prop1', 'prop1')))
    }

    @Test
    void testBinaryGreaterThan() {
        Bean bean = new Bean(prop1: 'bbbb')
        assert evaluator.eval(bean, gt('prop1', 'aaaa'))
        assert !evaluator.eval(bean, not(gt('prop1', 'aaaa')))
    }

    @Test
    void testBinaryGreaterThanOrEqual() {
        Bean bean = new Bean(prop1: 'bbbb')
        assert evaluator.eval(bean, ge('prop1', 'aaaa'))
        assert !evaluator.eval(bean, not(ge('prop1', 'aaaa')))
        bean.prop1 = 'aaaa'
        assert evaluator.eval(bean, ge('prop1', 'aaaa'))
        assert !evaluator.eval(bean, not(ge('prop1', 'aaaa')))
    }

    @Test
    void testBinaryLessThan() {
        Bean bean = new Bean(prop1: 'aaaa')
        assert evaluator.eval(bean, lt('prop1', 'bbbb'))
        assert !evaluator.eval(bean, not(lt('prop1', 'bbbb')))
    }

    @Test
    void testBinaryLessThanOrEqual() {
        Bean bean = new Bean(prop1: 'aaaa')
        assert evaluator.eval(bean, le('prop1', 'bbbb'))
        assert !evaluator.eval(bean, not(le('prop1', 'bbbb')))
        bean.prop1 = 'bbbb'
        assert evaluator.eval(bean, le('prop1', 'bbbb'))
        assert !evaluator.eval(bean, not(le('prop1', 'bbbb')))
    }

    @Test
    void testPropertyEqual() {
        Bean bean = new Bean(prop1: 'prop1', prop2: 'prop1')
        assert evaluator.eval(bean, eqProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(eqProperty('prop1', 'prop2')))
    }

    @Test
    void testPropertyNotEqual() {
        Bean bean = new Bean(prop1: 'prop1', prop2: 'prop2')
        assert evaluator.eval(bean, neProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(neProperty('prop1', 'prop2')))
    }

    @Test
    void testPropertyGreaterThan() {
        Bean bean = new Bean(prop1: 'bbbb', prop2: 'aaaa')
        assert evaluator.eval(bean, gtProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(gtProperty('prop1', 'prop2')))
    }

    @Test
    void testPropertyGreaterThanOrEqual() {
        Bean bean = new Bean(prop1: 'bbbb', prop2: 'aaaa')
        assert evaluator.eval(bean, geProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(geProperty('prop1', 'prop2')))
        bean.prop1 = 'aaaa'
        assert evaluator.eval(bean, geProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(geProperty('prop1', 'prop2')))
    }

    @Test
    void testPropertyLessThan() {
        Bean bean = new Bean(prop1: 'aaaa', prop2: 'bbbb')
        assert evaluator.eval(bean, ltProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(ltProperty('prop1', 'prop2')))
    }

    @Test
    void testPropertyLessThanOrEqual() {
        Bean bean = new Bean(prop1: 'aaaa', prop2: 'bbbb')
        assert evaluator.eval(bean, leProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(leProperty('prop1', 'prop2')))
        bean.prop1 = 'bbbb'
        assert evaluator.eval(bean, leProperty('prop1', 'prop2'))
        assert !evaluator.eval(bean, not(leProperty('prop1', 'prop2')))
    }

    @Test
    void testComposite() {
        Bean bean = new Bean(prop1: 'aaaa', prop2: 'bbbb')
        assert evaluator.eval(bean, and(eq('prop1', 'aaaa'), eq('prop2', 'bbbb')))
        assert evaluator.eval(bean, or(eq('prop1', 'cccc'), eq('prop2', 'bbbb')))
        assert evaluator.eval(bean, and(eq('prop1', 'aaaa'), eq('prop2', 'bbbb'), neProperty('prop1', 'prop2')))
        assert evaluator.eval(bean, or(eq('prop1', 'cccc'), eq('prop2', 'bbbb'), neProperty('prop1', 'prop2')))
        // fail fast
        assert !evaluator.eval(bean, and(eq('prop1', 'cccc'), eq('prop2', 'bbbb'), neProperty('prop1', 'prop2')))

        assert !evaluator.eval(bean, not(and(eq('prop1', 'aaaa'), eq('prop2', 'bbbb'))))
        assert !evaluator.eval(bean, not(or(eq('prop1', 'cccc'), eq('prop2', 'bbbb'))))
    }
}

class Bean {
    String prop1
    String prop2
}