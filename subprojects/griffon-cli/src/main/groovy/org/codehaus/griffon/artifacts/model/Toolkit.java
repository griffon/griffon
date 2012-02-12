/*
 * Copyright 2011-2012 the original author or authors.
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
public enum Toolkit {
    SWING("Swing"),
    JAVAFX("JavaFX"),
    SWT("SWT");

    private final String name;

    private Toolkit(String name) {
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
        Toolkit[] values = Toolkit.values();
        NAMES = new String[values.length];
        LOWERCASE_NAMES = new String[values.length];

        int i = 0;
        for (Toolkit toolkit : Toolkit.values()) {
            NAMES[i] = toolkit.getName();
            LOWERCASE_NAMES[i++] = toolkit.getName().toLowerCase();
        }
    }

    /**
     * Finds a toolkit by name.
     *
     * @throws IllegalArgumentException if name is null, blank or there is no match
     */
    public static Toolkit findByName(String name) {
        if (isBlank(name)) throw new IllegalArgumentException("'" + name + "' is not a valid toolkit name.");
        for (Toolkit toolkit : Toolkit.values()) {
            if (toolkit.getName().equalsIgnoreCase(name)) {
                return toolkit;
            }
        }
        throw new IllegalArgumentException("'" + name + "' is not a valid toolkit name.");
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

    public static List<Toolkit> asList() {
        return Arrays.asList(values());
    }
}
