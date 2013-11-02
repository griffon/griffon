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

package griffon.swing.formatters;

import griffon.core.resources.formatters.AbstractFormatter;
import griffon.core.resources.formatters.ParseException;
import griffon.swing.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Arrays;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.lang.Integer.toHexString;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class ColorFormatter extends AbstractFormatter<Color> {
    public static final String PATTERN_SHORT = "#RGB";
    public static final String PATTERN_SHORT_WITH_ALPHA = "#RGBA";
    public static final String PATTERN_LONG = "#RRGGBB";
    public static final String PATTERN_LONG_WITH_ALPHA = "#RRGGBBAA";
    public static final String DEFAULT_PATTERN = PATTERN_LONG;

    private static final String[] PATTERNS = new String[]{
        PATTERN_LONG,
        PATTERN_LONG_WITH_ALPHA,
        PATTERN_SHORT,
        PATTERN_SHORT_WITH_ALPHA
    };

    public static final ColorFormatter SHORT = new ColorFormatter(PATTERN_SHORT);
    public static final ColorFormatter SHORT_WITH_ALPHA = new ColorFormatter(PATTERN_SHORT_WITH_ALPHA);
    public static final ColorFormatter LONG = new ColorFormatter(PATTERN_LONG);
    public static final ColorFormatter LONG_WITH_ALPHA = new ColorFormatter(PATTERN_LONG_WITH_ALPHA);

    @Nonnull
    public static ColorFormatter getInstance(@Nullable String pattern) {
        return new ColorFormatter(pattern);
    }

    private final ColorFormatterDelegate delegate;

    public ColorFormatter() {
        this(DEFAULT_PATTERN);
    }

    protected ColorFormatter(@Nullable String pattern) {
        if (PATTERN_SHORT.equals(pattern)) {
            delegate = new ShortColorFormatterDelegate();
        } else if (PATTERN_SHORT_WITH_ALPHA.equals(pattern)) {
            delegate = new ShortWithAlphaColorFormatterDelegate();
        } else if (PATTERN_LONG.equals(pattern)) {
            delegate = new LongColorFormatterDelegate();
        } else if (PATTERN_LONG_WITH_ALPHA.equals(pattern)) {
            delegate = new LongWithAlphaColorFormatterDelegate();
        } else if (isBlank(pattern)) {
            delegate = new LongColorFormatterDelegate();
        } else {
            throw new IllegalArgumentException("Invalid pattern '" + pattern + "'. Valid patterns are " + Arrays.toString(PATTERNS));
        }
    }

    @Nonnull
    public String format(@Nonnull Color color) {
        return delegate.format(color);
    }

    @Nullable
    public Color parse(@Nullable String str) throws ParseException {
        return delegate.parse(str);
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static Color parseColor(@Nonnull String str) throws ParseException {
        requireNonBlank(str, "Argument cannot be blank");
        if (str.startsWith("#")) {
            switch (str.length()) {
                case 4:
                    return SHORT.parse(str);
                case 5:
                    return SHORT_WITH_ALPHA.parse(str);
                case 7:
                    return LONG.parse(str);
                case 9:
                    return LONG_WITH_ALPHA.parse(str);
                default:
                    throw parseError(str, Color.class);
            }
        } else {
            // assume it's a Color constant
            try {
                return Colors.valueOf(str.toUpperCase()).getColor();
            } catch (Exception e) {
                throw parseError(str, Color.class, e);
            }
        }
    }

    private static interface ColorFormatterDelegate {
        @Nonnull
        String getPattern();

        @Nonnull
        String format(@Nonnull Color color);

        @Nonnull
        Color parse(@Nonnull String str) throws ParseException;
    }

    private static abstract class AbstractColorFormatterDelegate implements ColorFormatterDelegate {
        private final String pattern;

        private AbstractColorFormatterDelegate(@Nonnull String pattern) {
            this.pattern = pattern;
        }

        @Nonnull
        public String getPattern() {
            return pattern;
        }
    }

    private static class ShortColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private ShortColorFormatterDelegate() {
            super(PATTERN_SHORT);
        }

        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, "Cannot format given Color because it's null");

            return new StringBuilder("#")
                .append(toHexString(color.getRed()).charAt(0))
                .append(toHexString(color.getGreen()).charAt(0))
                .append(toHexString(color.getBlue()).charAt(0))
                .toString();
        }

        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (isBlank(str) || !str.startsWith("#") || str.length() != 4) {
                throw parseError(str, Color.class);
            }

            int r = parseHexInt(new StringBuilder()
                .append(str.charAt(1))
                .append(str.charAt(1))
                .toString().toUpperCase(), Color.class);
            int g = parseHexInt(new StringBuilder()
                .append(str.charAt(2))
                .append(str.charAt(2))
                .toString().toUpperCase(), Color.class);
            int b = parseHexInt(new StringBuilder()
                .append(str.charAt(3))
                .append(str.charAt(3))
                .toString().toUpperCase(), Color.class);

            return new Color(r, g, b);
        }
    }

    private static class ShortWithAlphaColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private ShortWithAlphaColorFormatterDelegate() {
            super(PATTERN_SHORT_WITH_ALPHA);
        }

        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, "Cannot format given Color because it's null");

            return new StringBuilder("#")
                .append(toHexString(color.getRed()).charAt(0))
                .append(toHexString(color.getGreen()).charAt(0))
                .append(toHexString(color.getBlue()).charAt(0))
                .append(toHexString(color.getAlpha()).charAt(0))
                .toString();
        }

        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (isBlank(str) || !str.startsWith("#") || str.length() != 5) {
                throw parseError(str, Color.class);
            }

            int r = parseHexInt(new StringBuilder()
                .append(str.charAt(1))
                .append(str.charAt(1))
                .toString().toUpperCase(), Color.class);
            int g = parseHexInt(new StringBuilder()
                .append(str.charAt(2))
                .append(str.charAt(2))
                .toString().toUpperCase(), Color.class);
            int b = parseHexInt(new StringBuilder()
                .append(str.charAt(3))
                .append(str.charAt(3))
                .toString().toUpperCase(), Color.class);
            int a = parseHexInt(new StringBuilder()
                .append(str.charAt(4))
                .append(str.charAt(4))
                .toString().toUpperCase(), Color.class);

            return new Color(r, g, b, a);
        }
    }

    private static class LongColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private LongColorFormatterDelegate() {
            super(PATTERN_LONG);
        }

        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, "Cannot format given Color because it's null");
            return new StringBuilder("#")
                .append(padLeft(toHexString(color.getRed()), 2, "0"))
                .append(padLeft(toHexString(color.getGreen()), 2, "0"))
                .append(padLeft(toHexString(color.getBlue()), 2, "0"))
                .toString();
        }

        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (isBlank(str) || !str.startsWith("#") || str.length() != 7) {
                throw parseError(str, Color.class);
            }

            int r = parseHexInt(new StringBuilder()
                .append(str.charAt(1))
                .append(str.charAt(2))
                .toString().toUpperCase(), Color.class);
            int g = parseHexInt(new StringBuilder()
                .append(str.charAt(3))
                .append(str.charAt(4))
                .toString().toUpperCase(), Color.class);
            int b = parseHexInt(new StringBuilder()
                .append(str.charAt(5))
                .append(str.charAt(6))
                .toString().toUpperCase(), Color.class);

            return new Color(r, g, b);
        }
    }

    private static class LongWithAlphaColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private LongWithAlphaColorFormatterDelegate() {
            super(PATTERN_LONG_WITH_ALPHA);
        }

        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, "Cannot format given Color because it's null");

            return new StringBuilder("#")
                .append(padLeft(toHexString(color.getRed()), 2, "0"))
                .append(padLeft(toHexString(color.getGreen()), 2, "0"))
                .append(padLeft(toHexString(color.getBlue()), 2, "0"))
                .append(padLeft(toHexString(color.getAlpha()), 2, "0"))
                .toString();
        }

        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (isBlank(str) || !str.startsWith("#") || str.length() != 9) {
                throw parseError(str, Color.class);
            }

            int r = parseHexInt(new StringBuilder()
                .append(str.charAt(1))
                .append(str.charAt(2))
                .toString().toUpperCase(), Color.class);
            int g = parseHexInt(new StringBuilder()
                .append(str.charAt(3))
                .append(str.charAt(4))
                .toString().toUpperCase(), Color.class);
            int b = parseHexInt(new StringBuilder()
                .append(str.charAt(5))
                .append(str.charAt(6))
                .toString().toUpperCase(), Color.class);
            int a = parseHexInt(new StringBuilder()
                .append(str.charAt(7))
                .append(str.charAt(8))
                .toString().toUpperCase(), Color.class);

            return new Color(r, g, b, a);
        }
    }

    // following methods taken from org/codehaus/groovy/runtime/StringGroovyMethods.java

    private static String padLeft(String self, Number numberOfChars, String padding) {
        int numChars = numberOfChars.intValue();
        if (numChars <= self.length()) {
            return self;
        } else {
            return getPadding(padding, numChars - self.length()) + self;
        }
    }

    private static String getPadding(String padding, int length) {
        if (padding.length() < length) {
            return multiply(padding, length / padding.length() + 1).substring(0, length);
        } else {
            return padding.substring(0, length);
        }
    }

    public static String multiply(String self, Number factor) {
        int size = factor.intValue();
        if (size == 0)
            return "";
        else if (size < 0) {
            throw new IllegalArgumentException("multiply() should be called with a number of 0 or greater not: " + size);
        }
        StringBuilder answer = new StringBuilder(self);
        for (int i = 1; i < size; i++) {
            answer.append(self);
        }
        return answer.toString();
    }
}
