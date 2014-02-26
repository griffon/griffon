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

class CriteriaBuilderTests {
    private CriteriaBuilder builder = new CriteriaBuilder()

    @Test
    void exerciseSeveralConfigurations() {
        assert builder.name("foo") == new BinaryExpression("name", Operator.EQUAL, "foo")
        assert builder.name(operator: Operator.NOT_EQUAL, "foo") == new BinaryExpression("name", Operator.NOT_EQUAL, "foo")
        assert builder.eq(propertyName: "name", value: "foo") == new BinaryExpression("name", Operator.EQUAL, "foo")
        assert builder.eq(propertyName: "name", otherPropertyName: "lastname") == new PropertyExpression("name", Operator.EQUAL, "lastname")

        CompositeCriterion and = builder.and {
            name("foo")
            lastname("bar")
        }
        assert and.operator == Operator.AND
        assert and.criteria[0] == new BinaryExpression("name", Operator.EQUAL, "foo")
        assert and.criteria[1] == new BinaryExpression("lastname", Operator.EQUAL, "bar")

        def criteria = {
            or {
                name("foo")
                lastname("bar")
            }
        }
        criteria.delegate = builder
        CompositeCriterion or = criteria()
        assert or.operator == Operator.OR
        assert or.criteria[0] == new BinaryExpression("name", Operator.EQUAL, "foo")
        assert or.criteria[1] == new BinaryExpression("lastname", Operator.EQUAL, "bar")
    }
}