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
public enum Operator {
    EQUAL("="), NOT_EQUAL("<>"),
    GREATER_THAN(">"), GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"), LESS_THAN_OR_EQUAL("<="),
    LIKE("LIKE"), NOT_LIKE("NOT LIKE"),
    IS_NULL("IS NULL"), IS_NOT_NULL("IS NOT NULL"),
    AND("AND"), OR("OR"), NOT("NOT");

    private final String op;

    Operator(String op) {
        this.op = op;
    }

    @Nonnull
    public String getOp() {
        return op;
    }

    @Nonnull
    public String op() {
        return op;
    }

    public String toString() {
        return this.op;
    }
}
