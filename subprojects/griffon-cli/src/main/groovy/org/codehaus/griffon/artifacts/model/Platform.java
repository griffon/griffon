/*
 * Copyright 2011 the original author or authors.
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

package org.codehaus.griffon.artifacts.model;

import java.util.Arrays;
import java.util.List;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public enum Platform {
    LINUX("Linux"),
    LINUX64("Linux64"),
    MACOSX("MacOSX"),
    MACOSX64("MacOSX64"),
    WINDOWS("Windows"),
    WINDOWS64("Windows64"),
    SOLARIS("Solaris");

    private final String name;

    private Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLowercaseName() {
        return name().toLowerCase();
    }

    private static String[] NAMES;
    private static String[] LOWERCASE_NAMES;

    static {
        Platform[] values = Platform.values();
        NAMES = new String[values.length];
        LOWERCASE_NAMES = new String[values.length];

        int i = 0;
        for (Platform platform : Platform.values()) {
            NAMES[i] = platform.getName();
            LOWERCASE_NAMES[i++] = platform.getName().toLowerCase();
        }
    }

    /**
     * Finds a platform by name.
     *
     * @throws IllegalArgumentException if name is null, blank or there is no match
     */
    public static Platform findByName(String name) {
        if (isBlank(name)) throw new IllegalArgumentException("'" + name + "' is not a valid platform name.");
        for (Platform platform : Platform.values()) {
            if (platform.getName().equalsIgnoreCase(name)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("'" + name + "' is not a valid platform name.");
    }

    public static String[] getNames() {
        return NAMES;
    }

    public static List<String> getNamesAsList() {
        return Arrays.asList(NAMES);
    }

    public static String[] getLowercaseNames() {
        return LOWERCASE_NAMES;
    }

    public static List<String> getLowercaseNamesAsList() {
        return Arrays.asList(LOWERCASE_NAMES);
    }

    public static List<Platform> asList() {
        return Arrays.asList(values());
    }
}