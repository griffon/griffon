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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

public class Calculation implements Serializable {
    private static final long serialVersionUID = 1_000_000L;

    private String error;
    private long result;

    @Nonnull
    public static Calculation of(long result) {
        return new Calculation(result);
    }

    @Nonnull
    public static Calculation of(@Nonnull String error) {
        return new Calculation(error);
    }

    public Calculation(@Nonnull String error) {
        this.error = error;
    }

    public Calculation(long result) {
        this.result = result;
    }

    @Min(-100)
    @Max(10000)
    public long getResult() {
        return result;
    }

    @Nullable
    public String getError() {
        return error;
    }

    public boolean hasError() {
        return this.error != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Calculation that = (Calculation) o;

        return !(error != null ? !error.equals(that.error) : that.error != null) && result == that.result;
    }

    @Override
    public int hashCode() {
        int result1 = error != null ? error.hashCode() : 0;
        result1 = 31 * result1 + (int) (result ^ (result >>> 32));
        return result1;
    }
}
