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

import javax.annotation.Nonnull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public interface Calculator {
    @Nonnull
    Calculation sum(@Min(0) @Max(100) long op1, @Min(0) @Max(100) long op2);

    @Nonnull
    Calculation sub(@Min(0) @Max(100) long op1, @Min(0) @Max(100) long op2);

    @Nonnull
    Calculation mul(@Min(0) @Max(100) long op1, @Min(0) @Max(100) long op2);

    @Nonnull
    Calculation div(@Min(0) @Max(100) long op1, @Min(1) @Max(100) long op2);
}
