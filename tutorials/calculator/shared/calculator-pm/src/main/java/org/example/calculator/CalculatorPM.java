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
package org.example.calculator;

public interface CalculatorPM {
    String PM_CALCULATION = "calculation";

    String ATTR_OP1 = "op1";
    String ATTR_OP2 = "op2";
    String ATTR_RESULT = "result";
    String ATTR_ERROR = "error";

    String COMMAND_SUM = "calculator-command-sum";
    String COMMAND_SUB = "calculator-command-sub";
    String COMMAND_MUL = "calculator-command-mul";
    String COMMAND_DIV = "calculator-command-div";
}
