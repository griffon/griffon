/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.core.resources.formatters;

import java.util.Arrays;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class BooleanFormatter extends AbstractFormatter {
    private static final String PATTERN_BOOL = "boolean";
    private static final String PATTERN_QUERY = "query";
    private static final String PATTERN_SWITCH = "switch";
    private static final String DEFAULT_PATTERN = PATTERN_BOOL;

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

    public static BooleanFormatter getInstance(String pattern) {
        return new BooleanFormatter(pattern);
    }

    private final BooleanFormatterDelegate delegate;

    public BooleanFormatter() {
        this(DEFAULT_PATTERN);
    }

    protected BooleanFormatter(String pattern) {
        if (PATTERN_BOOL.equals(pattern)) {
            delegate = new BoolBooleanFormatterDelegate();
        } else if (PATTERN_QUERY.equals(pattern)) {
            delegate = new QueryBooleanFormatterDelegate();
        } else if (PATTERN_SWITCH.equals(pattern)) {
            delegate = new SwitchBooleanFormatterDelegate();
        } else {
            throw new IllegalArgumentException("Invalid pattern '" + pattern + "'. Valid patterns are " + Arrays.toString(PATTERNS));
        }
    }

    public static Boolean parseBoolean(String str) throws ParseException {
        for (BooleanFormatter formatter : FORMATTERS) {
            try {
                return (Boolean) formatter.parse(str);
            } catch (ParseException e) {
                // ignore
            }
        }
        throw parseError(str, Boolean.class);
    }

    @Override
    public String format(Object obj) {
        if (obj instanceof Boolean) {
            return format((Boolean) obj);
        }
        throw new IllegalArgumentException("Can't format given Object as a Boolean");
    }

    public String format(Boolean value) {
        return delegate.format(value);
    }

    @Override
    public Object parse(String str) throws ParseException {
        return delegate.parse(str);
    }

    private static interface BooleanFormatterDelegate {
        String getPattern();

        String format(Boolean value);

        Boolean parse(String str) throws ParseException;
    }

    private static abstract class AbstractBooleanFormatterDelegate implements BooleanFormatterDelegate {
        private final String pattern;
        private final String[] tokens;

        private AbstractBooleanFormatterDelegate(String pattern, String[] tokens) {
            this.pattern = pattern;
            this.tokens = tokens;
        }

        public String getPattern() {
            return pattern;
        }

        public String format(Boolean bool) {
            if (bool == null) {
                throw new IllegalArgumentException("Can't format given Boolean because it's null");
            }

            return bool ? tokens[1] : tokens[0];
        }

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
