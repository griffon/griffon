/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.formatter;

import griffon.annotations.core.Nullable;
import griffon.formatter.ParseException;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class BooleanFormatter extends AbstractFormatter<Boolean> {
    public static final String PATTERN_BOOL = "boolean";
    public static final String PATTERN_QUERY = "query";
    public static final String PATTERN_SWITCH = "switch";
    public static final String DEFAULT_PATTERN = PATTERN_BOOL;

    private static final String[] PATTERNS = new String[]{
        PATTERN_BOOL,
        PATTERN_QUERY,
        PATTERN_SWITCH
    };

    public static final BooleanFormatter BOOL = new BooleanFormatter(PATTERN_BOOL);
    public static final BooleanFormatter QUERY = new BooleanFormatter(PATTERN_QUERY);
    public static final BooleanFormatter SWITCH = new BooleanFormatter(PATTERN_SWITCH);

    private static final BooleanFormatter[] FORMATTERS = new BooleanFormatter[]{
        BOOL,
        QUERY,
        SWITCH
    };

    public static BooleanFormatter getInstance(@Nullable String pattern) {
        return new BooleanFormatter(pattern);
    }

    private final BooleanFormatterDelegate delegate;

    public BooleanFormatter() {
        this(DEFAULT_PATTERN);
    }

    protected BooleanFormatter(@Nullable String pattern) {
        if (PATTERN_BOOL.equals(pattern)) {
            delegate = new BoolBooleanFormatterDelegate();
        } else if (PATTERN_QUERY.equals(pattern)) {
            delegate = new QueryBooleanFormatterDelegate();
        } else if (PATTERN_SWITCH.equals(pattern)) {
            delegate = new SwitchBooleanFormatterDelegate();
        } else if (isBlank(pattern)) {
            delegate = new BoolBooleanFormatterDelegate();
        } else {
            throw new IllegalArgumentException("Invalid pattern '" + pattern + "'. Valid patterns are " + Arrays.toString(PATTERNS));
        }
    }

    public String getPattern() {
        return delegate.getPattern();
    }

    @Nullable
    public static Boolean parseBoolean(@Nullable String str) throws ParseException {
        if (isBlank(str)) { return null; }
        for (BooleanFormatter formatter : FORMATTERS) {
            try {
                return formatter.parse(str);
            } catch (ParseException e) {
                // ignore
            }
        }
        throw parseError(str, Boolean.class);
    }

    @Nullable
    @Override
    public String format(@Nullable Boolean value) {
        return value == null ? null : delegate.format(value);
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public Boolean parse(@Nullable String str) throws ParseException {
        return isBlank(str) ? null : delegate.parse(str);
    }

    private interface BooleanFormatterDelegate {
        String getPattern();

        String format(Boolean value);

        Boolean parse(String str) throws ParseException;
    }

    private abstract static class AbstractBooleanFormatterDelegate implements BooleanFormatterDelegate {
        private final String pattern;
        private final String[] tokens;

        private AbstractBooleanFormatterDelegate(String pattern, String[] tokens) {
            this.pattern = pattern;
            this.tokens = tokens;
        }

        @Override
        public String getPattern() {
            return pattern;
        }

        @Override
        public String format(Boolean bool) {
            requireNonNull(bool, "Can't format given Boolean because it's null");
            return bool ? tokens[1] : tokens[0];
        }

        @Override
        public Boolean parse(String str) throws ParseException {
            if (tokens[0].equalsIgnoreCase(str)) {
                return Boolean.FALSE;
            } else if (tokens[1].equalsIgnoreCase(str)) {
                return Boolean.TRUE;
            }
            throw parseError(str, Boolean.class);
        }
    }

    private static class BoolBooleanFormatterDelegate extends AbstractBooleanFormatterDelegate {
        private BoolBooleanFormatterDelegate() {
            super(PATTERN_BOOL, new String[]{"false", "true"});
        }
    }

    private static class QueryBooleanFormatterDelegate extends AbstractBooleanFormatterDelegate {
        private QueryBooleanFormatterDelegate() {
            super(PATTERN_QUERY, new String[]{"no", "yes"});
        }
    }

    private static class SwitchBooleanFormatterDelegate extends AbstractBooleanFormatterDelegate {
        private SwitchBooleanFormatterDelegate() {
            super(PATTERN_SWITCH, new String[]{"off", "on"});
        }
    }
}
