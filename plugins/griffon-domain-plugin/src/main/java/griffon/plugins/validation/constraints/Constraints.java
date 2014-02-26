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
package griffon.plugins.validation.constraints;

import griffon.types.IntRange;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.validation.constraints.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 */
public final class Constraints {
    private Constraints() {

    }

    @Nonnull
    public static CollectionUtils.MapBuilder<String, List<ConstraintDef>> map() {
        return CollectionUtils.map();
    }

    @Nonnull
    public static CollectionUtils.ListBuilder<ConstraintDef> list() {
        return CollectionUtils.list();
    }

    @Nonnull
    public static CollectionUtils.ListBuilder<ConstraintDef> list(@Nonnull ConstraintDef... defs) {
        CollectionUtils.ListBuilder<ConstraintDef> list = CollectionUtils.<ConstraintDef>list();
        Collections.addAll(list, defs);
        return list;
    }

    @Nonnull
    public static ConstraintDef blank(boolean value) {
        return new ConstraintDef(BlankConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef creditCard(boolean value) {
        return new ConstraintDef(CreditCardConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef email(boolean value) {
        return new ConstraintDef(EmailConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef inList(@Nonnull List<?> elements) {
        return new ConstraintDef(InListConstraint.VALIDATION_DSL_NAME, elements);
    }

    @Nonnull
    public static ConstraintDef matches(@Nonnull String pattern) {
        return new ConstraintDef(MatchesConstraint.VALIDATION_DSL_NAME, pattern);
    }

    @Nonnull
    public static ConstraintDef max(@Nonnull Object value) {
        return new ConstraintDef(MaxConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef maxSize(int value) {
        return new ConstraintDef(MaxSizeConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef min(Object value) {
        return new ConstraintDef(MinConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef minSize(int value) {
        return new ConstraintDef(MinSizeConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef notEqual(@Nonnull Object value) {
        return new ConstraintDef(NotEqualConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef nullable(boolean value) {
        return new ConstraintDef(NullableConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef range(int from, int to) {
        return new ConstraintDef(RangeConstraint.VALIDATION_DSL_NAME, new IntRange(from, to));
    }

    @Nonnull
    public static ConstraintDef scale(int scale) {
        return new ConstraintDef(ScaleConstraint.VALIDATION_DSL_NAME, scale);
    }

    @Nonnull
    public static ConstraintDef size(int from, int to) {
        return new ConstraintDef(SizeConstraint.VALIDATION_DSL_NAME, new IntRange(from, to));
    }

    @Nonnull
    public static ConstraintDef url(boolean value) {
        return new ConstraintDef(UrlConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef url(@Nonnull String pattern) {
        return new ConstraintDef(UrlConstraint.VALIDATION_DSL_NAME, pattern);
    }

    @Nonnull
    public static ConstraintDef url(@Nonnull List<?> pattern) {
        return new ConstraintDef(UrlConstraint.VALIDATION_DSL_NAME, pattern);
    }

    @Nonnull
    public static ConstraintDef unique(boolean value) {
        return new ConstraintDef("unique", value);
    }

    @Nonnull
    public static ConstraintDef date(@Nonnull String value) {
        return new ConstraintDef(DateConstraint.VALIDATION_DSL_NAME, value);
    }

    @Nonnull
    public static ConstraintDef shared(@Nonnull String value) {
        return new ConstraintDef("shared", value);
    }
}
