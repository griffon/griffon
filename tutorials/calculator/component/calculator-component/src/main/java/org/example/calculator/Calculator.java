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
