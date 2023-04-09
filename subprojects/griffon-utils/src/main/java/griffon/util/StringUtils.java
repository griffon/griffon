/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.util;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Contains utility methods for converting between different name types,
 * for example from class names -&gt; property names and vice-versa. The
 * key aspect of this class is that it has no dependencies outside the
 * JDK!
 */
public class StringUtils {
    private static final String PROPERTY_SET_PREFIX = "set";
    private static final String PROPERTY_GET_PREFIX = "get";

    private static final Pattern GETTER_PATTERN_1 = Pattern.compile("^get[A-Z][\\w]*$");
    private static final Pattern GETTER_PATTERN_2 = Pattern.compile("^is[A-Z][\\w]*$");
    private static final Pattern SETTER_PATTERN = Pattern.compile("^set[A-Z][\\w]*$");
    private static final String ERROR_METHOD_NULL = "Argument 'method' must not be null";

    private static final String[] KEYWORDS = new String[]{
        "abstract",
        "assert",
        "as",
        "break",
        "case",
        "catch",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "else",
        "enum",
        "extends",
        "final",
        "finally",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "in",
        "instanceof",
        "interface",
        "native",
        "new",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "try",
        "void",
        "volatile",
        "while"
    };

    private StringUtils() {
        // prevent instantiation
    }

    /**
     * Finds out if the given String is a Java/Groovy keyword.
     *
     * @param str The String to test
     *
     * @return <tt>true</tt> if the given String is a keyword, false otherwise
     */
    public static boolean isKeyword(String str) {
        return isNotBlank(str) && Arrays.binarySearch(KEYWORDS, str.toLowerCase(Locale.ENGLISH)) > -1;
    }

    /**
     * Capitalizes a String (makes the first char uppercase) taking care
     * of blank strings and single character strings.
     *
     * @param str The String to be capitalized
     *
     * @return Capitalized version of the target string if it is not blank
     */
    public static String capitalize(String str) {
        if (isBlank(str)) { return str; }
        if (str.length() == 1) { return str.toUpperCase(); }
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    /**
     * Uncapitalizes a String (makes the first char lowercase) taking care
     * of blank strings and single character strings.
     *
     * @param str The String to be uncapitalized
     *
     * @return Uncapitalized version of the target string if it is not blank
     */
    public static String uncapitalize(String str) {
        if (isBlank(str)) { return str; }
        if (str.length() == 1) { return String.valueOf(Character.toLowerCase(str.charAt(0))); }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Retrieves the name of a setter for the specified property name
     *
     * @param propertyName The property name
     *
     * @return The setter equivalent
     */
    public static String getSetterName(String propertyName) {
        return PROPERTY_SET_PREFIX + capitalize(propertyName);
    }

    /**
     * Calculate the name for a getter method to retrieve the specified property
     *
     * @param propertyName The property name
     *
     * @return The name for the getter method for this property, if it were to exist, i.e. getConstraints
     */
    public static String getGetterName(String propertyName) {
        return PROPERTY_GET_PREFIX + capitalize(propertyName);
    }

    /**
     * Returns the class name for the given logical name and trailing name. For example "person" and "Controller" would evaluate to "PersonController"
     *
     * @param logicalName  The logical name
     * @param trailingName The trailing name
     *
     * @return The class name
     */
    public static String getClassName(String logicalName, String trailingName) {
        if (isBlank(logicalName)) {
            throw new IllegalArgumentException("Argument [logicalName] must not be null or blank");
        }

        String className = capitalize(logicalName);
        if (trailingName != null) {
            className = className + trailingName;
        }
        return className;
    }

    /**
     * Returns the class name representation of the given name
     *
     * @param name The name to convert
     *
     * @return The property name representation
     */
    public static String getClassNameRepresentation(String name) {
        StringBuilder buf = new StringBuilder();
        if (name != null && name.length() > 0) {
            String[] tokens = name.split("[^\\w\\d]");
            for (String token1 : tokens) {
                String token = token1.trim();
                buf.append(capitalize(token));
            }
        }

        return buf.toString();
    }

    /**
     * Converts foo-bar into FooBar. Empty and null strings are returned
     * as-is.
     *
     * @param name The lower case hyphen separated name
     *
     * @return The class name equivalent.
     */
    public static String getClassNameForLowerCaseHyphenSeparatedName(String name) {
        // Handle null and empty strings.
        if (isBlank(name)) { return name; }

        if (name.indexOf('-') > -1) {
            StringBuilder buf = new StringBuilder();
            String[] tokens = name.split("-");
            for (String token : tokens) {
                if (token == null || token.length() == 0) { continue; }
                buf.append(capitalize(token));
            }
            return buf.toString();
        }

        return capitalize(name);
    }

    /**
     * Retrieves the logical class name of a Griffon artifact given the Griffon class
     * and a specified trailing name
     *
     * @param clazz        The class
     * @param trailingName The trailing name such as "Controller" or "TagLib"
     *
     * @return The logical class name
     */
    public static String getLogicalName(Class<?> clazz, String trailingName) {
        return getLogicalName(clazz.getName(), trailingName);
    }

    /**
     * Retrieves the logical name of the class without the trailing name
     *
     * @param name         The name of the class
     * @param trailingName The trailing name
     *
     * @return The logical name
     */
    public static String getLogicalName(String name, String trailingName) {
        if (isNotBlank(name) && isNotBlank(trailingName)) {
            String shortName = getShortName(name);
            if (shortName.endsWith(trailingName)) {
                return shortName.substring(0, shortName.length() - trailingName.length());
            }
        }
        return name;
    }

    public static String getLogicalPropertyName(String className, String trailingName) {
        if (isNotBlank(className) && isNotBlank(trailingName) && className.length() == trailingName.length() + 1 && className.endsWith(trailingName)) {
            return className.substring(0, 1).toLowerCase();
        }
        return getLogicalName(getPropertyName(className), trailingName);
    }

    /**
     * Shorter version of getPropertyNameRepresentation
     *
     * @param name The name to convert
     *
     * @return The property name version
     */
    public static String getPropertyName(String name) {
        return getPropertyNameRepresentation(name);
    }

    /**
     * Returns the property name representation of the given {@code Method}
     *
     * @param method The method to inspect
     *
     * @return The property name representation
     *
     * @since 3.0.0
     */
    public static String getPropertyName(Method method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        String name = method.getName();
        if (GETTER_PATTERN_1.matcher(name).matches() || SETTER_PATTERN.matcher(name).matches()) {
            return uncapitalize(name.substring(3));
        } else if (GETTER_PATTERN_2.matcher(name).matches()) {
            return uncapitalize(name.substring(2));
        }
        return name;
    }


    /**
     * Shorter version of getPropertyNameRepresentation
     *
     * @param clazz The clazz to convert
     *
     * @return The property name version
     */
    public static String getPropertyName(Class<?> clazz) {
        return getPropertyNameRepresentation(clazz);
    }

    /**
     * Returns the property name equivalent for the specified class
     *
     * @param targetClass The class to get the property name for
     *
     * @return A property name representation of the class name (eg. MyClass becomes myClass)
     */
    public static String getPropertyNameRepresentation(Class<?> targetClass) {
        String shortName = getShortName(targetClass);
        return getPropertyNameRepresentation(shortName);
    }

    /**
     * Returns the property name representation of the given name
     *
     * @param name The name to convert
     *
     * @return The property name representation
     */
    public static String getPropertyNameRepresentation(String name) {
        if (isBlank(name)) { return name; }
        // Strip any package from the name.
        int pos = name.lastIndexOf('.');
        if (pos != -1) {
            name = name.substring(pos + 1);
        }

        // Check whether the name begins with two upper case letters.
        if (name.length() > 1 && Character.isUpperCase(name.charAt(0)) && Character.isUpperCase(name.charAt(1))) {
            return name;
        }

        String propertyName = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        if (propertyName.indexOf(' ') > -1) {
            propertyName = propertyName.replaceAll("\\s", "");
        }
        return propertyName;
    }

    /**
     * Converts foo-bar into fooBar
     *
     * @param name The lower case hyphen separated name
     *
     * @return The property name equivalent
     */
    public static String getPropertyNameForLowerCaseHyphenSeparatedName(String name) {
        return getPropertyName(getClassNameForLowerCaseHyphenSeparatedName(name));
    }

    /**
     * Returns the class name without the package prefix
     *
     * @param targetClass The class to get a short name for
     *
     * @return The short name of the class
     */
    public static String getShortName(Class<?> targetClass) {
        String className = targetClass.getName();
        return getShortName(className);
    }

    /**
     * Returns the class name without the package prefix
     *
     * @param className The class name to get a short name for
     *
     * @return The short name of the class
     */
    public static String getShortName(String className) {
        if (isBlank(className)) { return className; }
        int i = className.lastIndexOf('.');
        if (i > -1) {
            className = className.substring(i + 1, className.length());
        }
        return className;
    }

    /**
     * Converts a property name into its natural language equivalent eg ('firstName' becomes 'First Name')
     *
     * @param name The property name to convert
     *
     * @return The converted property name
     */
    public static String getNaturalName(String name) {
        name = getShortName(name);
        if (isBlank(name)) { return name; }
        List<String> words = new ArrayList<>();
        int i = 0;
        char[] chars = name.toCharArray();
        for (char c : chars) {
            String w;
            if (i >= words.size()) {
                w = "";
                words.add(i, w);
            } else {
                w = words.get(i);
            }

            if (Character.isLowerCase(c) || Character.isDigit(c)) {
                if (Character.isLowerCase(c) && w.length() == 0) {
                    c = Character.toUpperCase(c);
                } else if (w.length() > 1 && Character.isUpperCase(w.charAt(w.length() - 1))) {
                    w = "";
                    words.add(++i, w);
                }

                words.set(i, w + c);
            } else if (Character.isUpperCase(c)) {
                if ((i == 0 && w.length() == 0) || Character.isUpperCase(w.charAt(w.length() - 1))) {
                    words.set(i, w + c);
                } else {
                    words.add(++i, String.valueOf(c));
                }
            }
        }

        StringBuilder buf = new StringBuilder();
        for (Iterator<String> j = words.iterator(); j.hasNext(); ) {
            String word = j.next();
            buf.append(word);
            if (j.hasNext()) {
                buf.append(' ');
            }
        }
        return buf.toString();
    }

    /**
     * <p>Determines whether a given string is <code>null</code>, empty,
     * or only contains whitespace. If it contains anything other than
     * whitespace then the string is not considered to be blank and the
     * method returns <code>false</code>.</p>
     *
     * @param str The string to test.
     *
     * @return <code>true</code> if the string is <code>null</code>, or
     * blank.
     */
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>Determines whether a given string is not <code>null</code>, empty,
     * or only contains whitespace. If it contains anything other than
     * whitespace then the string is not considered to be blank and the
     * method returns <code>true</code>.</p>
     *
     * @param str The string to test.
     *
     * @return <code>true</code> if the string is not <code>null</code>, nor
     * blank.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Checks that the specified String is not {@code blank}. This
     * method is designed primarily for doing parameter validation in methods
     * and constructors, as demonstrated below:
     * <blockquote><pre>
     * public Foo(String str) {
     *     this.str = StringUtils.requireNonBlank(str);
     * }
     * </pre></blockquote>
     *
     * @param str the String to check for blank
     *
     * @return {@code str} if not {@code blank}
     *
     * @throws IllegalArgumentException if {@code str} is {@code blank}
     */
    public static String requireNonBlank(String str) {
        if (isBlank(str)) {
            throw new IllegalArgumentException();
        }
        return str;
    }

    /**
     * Checks that the specified String is not {@code blank} and
     * throws a customized {@link IllegalArgumentException} if it is. This method
     * is designed primarily for doing parameter validation in methods and
     * constructors with multiple parameters, as demonstrated below:
     * <blockquote><pre>
     * public Foo(String str) {
     *     this.str = StringUtils.requireNonBlank(str, "str must not be null");
     * }
     * </pre></blockquote>
     *
     * @param str     the String to check for blank
     * @param message detail message to be used in the event that a {@code
     *                IllegalArgumentException} is thrown
     *
     * @return {@code str} if not {@code blank}
     *
     * @throws IllegalArgumentException if {@code str} is {@code blank}
     */
    public static String requireNonBlank(String str, String message) {
        if (isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    /**
     * Retrieves the hyphenated name representation of the supplied class. For example
     * MyFunkyGriffonThingy would be my-funky-griffon-thingy.
     *
     * @param clazz The class to convert
     *
     * @return The hyphenated name representation
     */
    public static String getHyphenatedName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getHyphenatedName(clazz.getName());
    }

    /**
     * Retrieves the hyphenated name representation of the given class name.
     * For example MyFunkyGriffonThingy would be my-funky-griffon-thingy.
     *
     * @param name The class name to convert.
     *
     * @return The hyphenated name representation.
     */
    public static String getHyphenatedName(String name) {
        if (isBlank(name)) { return name; }
        if (name.endsWith(".groovy")) {
            name = name.substring(0, name.length() - 7);
        }
        String naturalName = getNaturalName(getShortName(name));
        return naturalName.replaceAll("\\s", "-").toLowerCase();
    }

    /**
     * Concatenates the <code>toString()</code> representation of each
     * item in this Iterable, with the given String as a separator between each item.
     *
     * @param self      an Iterable of objects
     * @param separator a String separator
     *
     * @return the joined String
     */
    @Nonnull
    public static String join(@Nonnull Iterable<?> self, @Nullable String separator) {
        StringBuilder buffer = new StringBuilder();
        boolean first = true;

        if (separator == null) { separator = ""; }

        for (Object value : self) {
            if (first) {
                first = false;
            } else {
                buffer.append(separator);
            }
            buffer.append(String.valueOf(value));
        }
        return buffer.toString();
    }

    /**
     * Applies single or double quotes to a string if it contains whitespace characters
     *
     * @param str the String to be surrounded by quotes
     *
     * @return a copy of the original String, surrounded by quotes
     */
    public static String quote(String str) {
        if (isBlank(str)) { return str; }
        for (int i = 0; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                str = applyQuotes(str);
                break;
            }
        }
        return str;
    }

    /**
     * Removes single or double quotes from a String
     *
     * @param str the String from which quotes will be removed
     *
     * @return the unquoted String
     */
    public static String unquote(String str) {
        if (isBlank(str)) { return str; }
        if ((str.startsWith("'") && str.endsWith("'")) ||
            (str.startsWith("\"") && str.endsWith("\""))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private static String applyQuotes(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char b;
        char c = 0;
        int i;
        int len = string.length();
        StringBuilder sb = new StringBuilder(len * 2);
        String t;
        char[] chars = string.toCharArray();
        char[] buffer = new char[1030];
        int bufferIndex = 0;
        sb.append('"');
        for (i = 0; i < len; i += 1) {
            if (bufferIndex > 1024) {
                sb.append(buffer, 0, bufferIndex);
                bufferIndex = 0;
            }
            b = c;
            c = chars[i];
            switch (c) {
                case '\\':
                case '"':
                    buffer[bufferIndex++] = '\\';
                    buffer[bufferIndex++] = c;
                    break;
                case '/':
                    if (b == '<') {
                        buffer[bufferIndex++] = '\\';
                    }
                    buffer[bufferIndex++] = c;
                    break;
                default:
                    if (c < ' ') {
                        switch (c) {
                            case '\b':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'b';
                                break;
                            case '\t':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 't';
                                break;
                            case '\n':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'n';
                                break;
                            case '\f':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'f';
                                break;
                            case '\r':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'r';
                                break;
                            default:
                                t = "000" + Integer.toHexString(c);
                                int tLength = t.length();
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'u';
                                buffer[bufferIndex++] = t.charAt(tLength - 4);
                                buffer[bufferIndex++] = t.charAt(tLength - 3);
                                buffer[bufferIndex++] = t.charAt(tLength - 2);
                                buffer[bufferIndex++] = t.charAt(tLength - 1);
                        }
                    } else {
                        buffer[bufferIndex++] = c;
                    }
            }
        }
        sb.append(buffer, 0, bufferIndex);
        sb.append('"');
        return sb.toString();
    }
}
