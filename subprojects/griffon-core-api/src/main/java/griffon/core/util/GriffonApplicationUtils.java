/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.core.util;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static griffon.util.StringUtils.isBlank;

/**
 * Assorted utility methods and constants.
 *
 * @author Andres Almiray
 */
public final class GriffonApplicationUtils {
    public static final String platform;
    public static final String basePlatform;
    /**
     * True if running Linux, Solaris or MacOSX
     */
    private static final boolean isUnix;
    private static final boolean isWindows;
    private static final boolean isLinux;
    private static final boolean isSolaris;
    private static final boolean isMacOSX;
    private static final String osArch;
    private static final String osName;
    private static final String osVersion;
    private static final String javaVersion;
    private static final boolean is64Bit;
    private static final boolean isJdk11Compatible;

    static {
        osArch = System.getProperty("os.arch");
        osName = System.getProperty("os.name");
        is64Bit = osArch.contains("64");

        if (osName.contains("Windows")) {
            basePlatform = "windows";
            isWindows = true;
            isLinux = false;
            isUnix = false;
            isMacOSX = false;
            isSolaris = false;
        } else if (osName.contains("Linux")) {
            basePlatform = "linux";
            isWindows = false;
            isLinux = true;
            isUnix = true;
            isMacOSX = false;
            isSolaris = false;
        } else if (osName.contains("Solaris") || osName.contains("SunOS")) {
            basePlatform = "solaris";
            isWindows = false;
            isLinux = false;
            isUnix = true;
            isMacOSX = false;
            isSolaris = true;
        } else if (osName.contains("Mac OS")) {
            basePlatform = "macosx";
            isWindows = false;
            isLinux = false;
            isUnix = true;
            isMacOSX = true;
            isSolaris = false;
        } else {
            basePlatform = "unknown";
            isWindows = false;
            isLinux = false;
            isUnix = false;
            isMacOSX = false;
            isSolaris = false;
        }

        osVersion = System.getProperty("os.version");
        javaVersion = System.getProperty("java.version");

        boolean jdk11Compatible = false;
        try {
            Pattern pattern = Pattern.compile("(\\d+)[.+]?");
            Matcher matcher = pattern.matcher(javaVersion);
            jdk11Compatible = matcher.matches();
        } catch (NumberFormatException e) {
            jdk11Compatible = false;
        }
        isJdk11Compatible = jdk11Compatible;

        platform = basePlatform + (is64Bit && !isSolaris ? "64" : "");
    }

    private GriffonApplicationUtils() {
        // prevent instantiation
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isUnix() {
        return isUnix;
    }

    public static boolean isLinux() {
        return isLinux;
    }

    public static boolean isSolaris() {
        return isSolaris;
    }

    public static boolean isMacOSX() {
        return isMacOSX;
    }

    public static String getOsArch() {
        return osArch;
    }

    public static String getOsName() {
        return osName;
    }

    public static String getOsVersion() {
        return osVersion;
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    public static boolean is64Bit() {
        return is64Bit;
    }

    public static String getPlatform() {
        return platform;
    }

    public static String getBasePlatform() {
        return basePlatform;
    }

    public static boolean getIsWindows() {
        return isWindows;
    }

    public static boolean getIsUnix() {
        return isUnix;
    }

    public static boolean getIsLinux() {
        return isLinux;
    }

    public static boolean getIsSolaris() {
        return isSolaris;
    }

    public static boolean getIsMacOSX() {
        return isMacOSX;
    }

    public static boolean getIs64Bit() {
        return is64Bit;
    }

    public static boolean getIsJdk11Compatible() {
        return isJdk11Compatible;
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static Locale parseLocale(@Nullable String locale) {
        if (isBlank(locale)) {
            return Locale.getDefault();
        }
        String[] parts = locale.split("_");
        switch (parts.length) {
            case 1:
                return new Locale(parts[0]);
            case 2:
                return new Locale(parts[0], parts[1]);
            case 3:
                return new Locale(parts[0], parts[1], parts[2]);
            default:
                return Locale.getDefault();
        }
    }
}
