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
package griffon.javafx.formatters;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import javafx.scene.paint.Color;
import org.kordamp.jsr377.formatter.AbstractFormatter;
import org.kordamp.jsr377.formatter.ParseException;

import java.lang.reflect.Field;
import java.util.Arrays;

import static java.lang.Integer.toHexString;
import static java.util.Objects.requireNonNull;

/**
 * <p>A {@code Formatter} that can parse Strings into {@code javafx.scene.paint.Color} and back
 * using several patterns</p>
 * <p/>
 * <p>
 * Supported patterns are:
 * <ul>
 * <li>{@code #RGB}</li>
 * <li>{@code #RGBA}</li>
 * <li>{@code #RRGGBB}</li>
 * <li>{@code #RRGGBBAA}</li>
 * </ul>
 * Where each letter stands for a particular color components in hexadecimal
 * <ul>
 * <li>{@code R} - red</li>
 * <li>{@code G} - green</li>
 * <li>{@code B} - blue</li>
 * <li>{@code A} - alpha</li>
 * </ul>
 * </p>
 *
 * @author Andres Almiray
 * @see org.kordamp.jsr377.formatter.Formatter
 * @since 2.0.0
 */
public class ColorFormatter extends AbstractFormatter<Color> {
    /**
     * "#RGB"
     */
    public static final String PATTERN_SHORT = "#RGB";

    /**
     * "#RGBA"
     */
    public static final String PATTERN_SHORT_WITH_ALPHA = "#RGBA";

    /**
     * "#RRGGBB"
     */
    public static final String PATTERN_LONG = "#RRGGBB";

    /**
     * "#RRGGBBAA"
     */
    public static final String PATTERN_LONG_WITH_ALPHA = "#RRGGBBAA";

    /**
     * "#RRGGBB"
     */
    public static final String DEFAULT_PATTERN = PATTERN_LONG;

    private static final String[] PATTERNS = new String[]{
        PATTERN_LONG,
        PATTERN_LONG_WITH_ALPHA,
        PATTERN_SHORT,
        PATTERN_SHORT_WITH_ALPHA
    };

    /**
     * {@code ColorFormatter} that uses the <b>{@code PATTERN_SHORT}</b> pattern
     */
    public static final ColorFormatter SHORT = new ColorFormatter(PATTERN_SHORT);

    /**
     * {@code ColorFormatter} that uses the <b>{@code PATTERN_SHORT_WITH_ALPHA}</b> pattern
     */
    public static final ColorFormatter SHORT_WITH_ALPHA = new ColorFormatter(PATTERN_SHORT_WITH_ALPHA);

    /**
     * {@code ColorFormatter} that uses the <b>{@code PATTERN_LONG}</b> pattern
     */
    public static final ColorFormatter LONG = new ColorFormatter(PATTERN_LONG);

    /**
     * {@code ColorFormatter} that uses the <b>{@code PATTERN_LONG_WITH_ALPHA}</b> pattern
     */
    public static final ColorFormatter LONG_WITH_ALPHA = new ColorFormatter(PATTERN_LONG_WITH_ALPHA);

    private static final String ERROR_COLOR_NULL = "Cannot format given Color because it's null";
    private static final String HASH = "#";
    private static final String ZERO = "0";

    /**
     * <p>Returns a {@code ColorFormatter} given a color pattern.</p>
     *
     * @param pattern the input pattern. Must be one of the 4 supported color patterns.
     *
     * @return a {@code ColorPattern} instance
     *
     * @throws IllegalArgumentException if the supplied {@code pattern} is not supported
     */
    @Nonnull
    public static ColorFormatter getInstance(@Nonnull String pattern) {
        return new ColorFormatter(pattern);
    }

    private final ColorFormatterDelegate delegate;

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

    @Override
    @Nullable
    public String format(@Nullable Color color) {
        return color == null ? null : delegate.format(color);
    }

    @Override
    @Nullable
    public Color parse(@Nullable String str) throws ParseException {
        return isBlank(str) ? null : delegate.parse(str);
    }

    /**
     * Returns the pattern used by this {@code ColorFormatter}
     *
     * @return the pattern this {@code ColorFormatter} uses for parsing/formatting.
     */
    @Nonnull
    public String getPattern() {
        return delegate.getPattern();
    }

    /**
     * <p>Parses a string into a {@code javafx.scene.paint.Color} instance.</p>
     * <p>The parsing pattern is chosen given the length of the input string
     * <ul>
     * <li>4 - {@code #RGB}</li>
     * <li>5 - {@code #RGBA}</li>
     * <li>7 - {@code #RRGGBB}</li>
     * <li>9 - {@code #RRGGBBAA}</li>
     * </ul>
     * </p>
     * The input string may also be any of the Color constants identified by
     * {@code javafx.scene.paint.Color}.
     *
     * @param str the string representation of a {@code javafx.scene.paint.Color}
     *
     * @return a {@code javafx.scene.paint.Color} instance matching the supplied RGBA color components
     *
     * @throws ParseException if the string cannot be parsed by the chosen pattern
     * @see javafx.scene.paint.Color
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static Color parseColor(@Nonnull String str) throws ParseException {
        if (str.startsWith(HASH)) {
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
                String colorFieldName = str.toUpperCase();
                Field field = Color.class.getField(colorFieldName);
                return (Color) field.get(null);
            } catch (Exception e) {
                throw parseError(str, Color.class, e);
            }
        }
    }

    private interface ColorFormatterDelegate {
        @Nonnull
        String getPattern();

        @Nonnull
        String format(@Nonnull Color color);

        @Nonnull
        Color parse(@Nonnull String str) throws ParseException;
    }

    private abstract static class AbstractColorFormatterDelegate implements ColorFormatterDelegate {
        private final String pattern;

        private AbstractColorFormatterDelegate(@Nonnull String pattern) {
            this.pattern = pattern;
        }

        @Override
        @Nonnull
        public String getPattern() {
            return pattern;
        }
    }

    private static int red(Color color) {
        return toIntColor(color.getRed());
    }

    private static int green(Color color) {
        return toIntColor(color.getGreen());
    }

    private static int blue(Color color) {
        return toIntColor(color.getBlue());
    }

    private static int alpha(Color color) {
        return toIntColor(color.getOpacity());
    }

    private static int toIntColor(double c) {
        return (int) (c * 255);
    }

    private static class ShortColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private ShortColorFormatterDelegate() {
            super(PATTERN_SHORT);
        }

        @Override
        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, ERROR_COLOR_NULL);

            return new StringBuilder(HASH)
                .append(toHexString(red(color)).charAt(0))
                .append(toHexString(green(color)).charAt(0))
                .append(toHexString(blue(color)).charAt(0))
                .toString();
        }

        @Override
        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (!str.startsWith(HASH) || str.length() != 4) {
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

            return Color.rgb(r, g, b);
        }
    }

    private static class ShortWithAlphaColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private ShortWithAlphaColorFormatterDelegate() {
            super(PATTERN_SHORT_WITH_ALPHA);
        }

        @Override
        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, ERROR_COLOR_NULL);

            return new StringBuilder(HASH)
                .append(toHexString(red(color)).charAt(0))
                .append(toHexString(green(color)).charAt(0))
                .append(toHexString(blue(color)).charAt(0))
                .append(toHexString(alpha(color)).charAt(0))
                .toString();
        }

        @Override
        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (!str.startsWith(HASH) || str.length() != 5) {
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

            return Color.rgb(r, g, b, a / 255d);
        }
    }

    private static class LongColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private LongColorFormatterDelegate() {
            super(PATTERN_LONG);
        }

        @Override
        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, ERROR_COLOR_NULL);

            return new StringBuilder(HASH)
                .append(padLeft(toHexString(red(color)), ZERO))
                .append(padLeft(toHexString(green(color)), ZERO))
                .append(padLeft(toHexString(blue(color)), ZERO))
                .toString();
        }

        @Override
        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (!str.startsWith(HASH) || str.length() != 7) {
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

            return Color.rgb(r, g, b);
        }
    }

    private static class LongWithAlphaColorFormatterDelegate extends AbstractColorFormatterDelegate {
        private LongWithAlphaColorFormatterDelegate() {
            super(PATTERN_LONG_WITH_ALPHA);
        }

        @Override
        @Nonnull
        public String format(@Nonnull Color color) {
            requireNonNull(color, ERROR_COLOR_NULL);

            return new StringBuilder(HASH)
                .append(padLeft(toHexString(red(color)), ZERO))
                .append(padLeft(toHexString(green(color)), ZERO))
                .append(padLeft(toHexString(blue(color)), ZERO))
                .append(padLeft(toHexString(alpha(color)), ZERO))
                .toString();
        }

        @Override
        @Nonnull
        public Color parse(@Nonnull String str) throws ParseException {
            if (!str.startsWith(HASH) || str.length() != 9) {
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

            return Color.rgb(r, g, b, a / 255d);
        }
    }

    private static String padLeft(String self, String padding) {
        return 2 <= self.length() ? self : padding + self;
    }

    private static int parseHexInt(@Nonnull String val, @Nonnull Class<?> klass) throws ParseException {
        try {
            return Integer.parseInt(String.valueOf(requireNonNull(val)).trim(), 16) & 0xFF;
        } catch (NumberFormatException e) {
            throw parseError(val, klass, e);
        }
    }
}
