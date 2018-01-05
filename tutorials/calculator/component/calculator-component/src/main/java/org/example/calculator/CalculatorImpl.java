/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.function.Supplier;

public class CalculatorImpl implements Calculator {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorImpl.class);

    @Override
    @Nonnull
    public Calculation sum(@Min(0) @Max(100) long op1, @Min(0) @Max(100) long op2) {
        LOG.debug("SUM of {} and {}", op1, op2);
        return validate(op1, "op1").min(0).max(100).validate(op2, "op2").min(0).max(100).get(() -> op1 + op2);
    }

    @Override
    @Nonnull
    public Calculation sub(@Min(0) @Max(100) long op1, @Min(0) @Max(100) long op2) {
        LOG.debug("SUB of {} and {}", op1, op2);
        return validate(op1, "op1").min(0).max(100).validate(op2, "op2").min(0).max(100).get(() -> op1 - op2);
    }

    @Override
    @Nonnull
    public Calculation mul(@Min(0) @Max(100) long op1, @Min(0) @Max(100) long op2) {
        LOG.debug("MUL of {} and {}", op1, op2);
        return validate(op1, "op1").min(0).max(100).validate(op2, "op2").min(0).max(100).get(() -> op1 * op2);
    }

    @Override
    @Nonnull
    public Calculation div(@Min(0) @Max(100) long op1, @Min(1) @Max(100) long op2) {
        LOG.debug("DIV of {} and {}", op1, op2);
        return validate(op1, "op1").min(0).max(100).validate(op2, "op2").min(1).max(100).get(() -> op1 / op2);
    }

    private static CalculationChecker validate(long value, String param) {
        return new CalculationChecker(value, param);
    }

    private static class CalculationChecker {
        private final long value;
        private final String param;
        private String error;

        private CalculationChecker(long value, String param) {
            this.value = value;
            this.param = param;
        }

        public CalculationChecker min(long limit) {
            if (error == null) {
                if (value < limit) {
                    error = "Param '" + param + "' violates @Min(" + limit + ") constraint. value = " + value;
                }
            }
            return this;
        }

        public CalculationChecker max(long limit) {
            if (error == null) {
                if (value > limit) {
                    error = "Param '" + param + "' violates @Max(" + limit + ") constraint. value = " + value;
                }
            }
            return this;
        }

        public CalculationChecker validate(long value, String param) {
            CalculationChecker checker = new CalculationChecker(value, param);
            checker.error = error;
            return checker;
        }

        @Nonnull
        public Calculation get(@Nonnull Supplier<Long> func) {
            if (error == null) {
                return Calculation.of(func.get());
            }
            return Calculation.of(error);
        }
    }
}
