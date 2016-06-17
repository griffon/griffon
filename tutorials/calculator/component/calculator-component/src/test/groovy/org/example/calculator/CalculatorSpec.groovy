/*
 * Copyright 2016 the original author or authors.
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
package org.example.calculator

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class CalculatorSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Shared private Calculator calculator = new CalculatorImpl()

    @Unroll("The sum of #op1 and #op2 is equal to #result")
    void sum_operation() {
        when:
        Calculation calculation = calculator.sum(op1, op2)

        then:
        !calculation.hasError()
        calculation.result == result

        where:
        op1 << (0..10).collect { it * 10 }
        op2 << (10..0).collect { it * 10 }
        result << (0..10).collect { 100 }
    }

    @Unroll("The sub of #op1 and #op2 is equal to #result")
    void sub_operation() {
        when:
        Calculation calculation = calculator.sub(op1, op2)

        then:
        !calculation.hasError()
        calculation.result == result

        where:
        op1 << (10..0).collect { it * 10 }
        op2 << (0..10).collect { it * 10 }
        result << (0..10).collect { 100 - (it * 20) }
    }

    @Unroll("The mul of #op1 and #op2 is equal to #result")
    void mul_operation() {
        when:
        Calculation calculation = calculator.mul(op1, op2)

        then:
        !calculation.hasError()
        calculation.result == result

        where:
        op1 << (0..10).collect { it * 10 }
        op2 << (0..10).collect { it * 10 }
        result << (0..10).collect { it * it * 100 }
    }

    @Unroll("The div of #op1 and #op2 is equal to #result")
    void div_operation() {
        when:
        Calculation calculation = calculator.div(op1, op2)

        then:
        !calculation.hasError()
        calculation.result == result

        where:
        op1 << (1..10).collect { 100 }
        op2 << (1..10).collect { it * 10 }
        result << (1..10).collect { (100 / (it * 10)) as int }
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with sum")
    void sum_constraints() {
        when:
        Calculation calculation = calculator.sum(op1, op2)

        then:
        calculation.hasError()

        where:
        op1 | op2
        -1  | 0
        0   | -1
        101 | 0
        0   | 101
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with sub")
    void sub_constraints() {
        when:
        Calculation calculation = calculator.sub(op1, op2)

        then:
        calculation.hasError()

        where:
        op1 | op2
        -1  | 0
        0   | -1
        101 | 0
        0   | 101
    }


    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with mul")
    void mul_constraints() {
        when:
        Calculation calculation = calculator.mul(op1, op2)

        then:
        calculation.hasError()

        where:
        op1 | op2
        -1  | 0
        0   | -1
        101 | 0
        0   | 101
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with div")
    void div_constraints() {
        when:
        Calculation calculation = calculator.div(op1, op2)

        then:
        calculation.hasError()

        where:
        op1 | op2
        -1  | 0
        0   | 0
        101 | 1
        0   | 101
    }
}
