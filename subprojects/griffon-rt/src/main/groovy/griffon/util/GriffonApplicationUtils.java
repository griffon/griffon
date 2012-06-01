/*
 * Copyright 2008-2012 the original author or authors.
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
package griffon.util;

import griffon.core.GriffonArtifact;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;

/**
 * Assorted utility methods and constants.
 *
 * @author Andres Almiray
 */
public final class GriffonApplicationUtils {
    private GriffonApplicationUtils() {
    }

    private static final boolean isWindows;
    private static final boolean isWindows95;
    private static final boolean isWindows98;
    private static final boolean isWindowsNT;
    private static final boolean isWindows2000;
    private static final boolean isWindows2003;
    private static final boolean isWindowsXP;
    private static final boolean isWindowsVista;
    private static final boolean isWindows7;

    /**
     * True if running Linux, Solaris or MacOSX
     */
    private static final boolean isUnix;

    private static final boolean isLinux;
    private static final boolean isSolaris;
    private static final boolean isMacOSX;

    private static final String osArch;
    private static final String osName;
    private static final String osVersion;
    private static final String javaVersion;
    private static final boolean is64Bit;

    private static final boolean isJdk14;
    private static final boolean isJdk15;
    private static final boolean isJdk16;
    private static final boolean isJdk17;
    private static final boolean isJdk18;

    public static final String platform;

    static {
        osArch = System.getProperty("os.arch");
        osName = System.getProperty("os.name");

        if (osName.contains("Windows")) {
            platform = "windows";
            isWindows = true;
            isLinux = false;
            isUnix = false;
            isMacOSX = false;
            isSolaris = false;
            if (osName.contains("95")) {
                isWindows95 = true;
                isWindows98 = false;
                isWindowsNT = false;
                isWindows2000 = false;
                isWindows2003 = false;
                isWindowsXP = false;
                isWindowsVista = false;
                isWindows7 = false;
            } else if (osName.contains("98")) {
                isWindows95 = false;
                isWindows98 = true;
                isWindowsNT = false;
                isWindows2000 = false;
                isWindows2003 = false;
                isWindowsXP = false;
                isWindowsVista = false;
                isWindows7 = false;
            } else if (osName.contains("NT")) {
                isWindows95 = false;
                isWindows98 = false;
                isWindowsNT = false;
                isWindows2000 = true;
                isWindows2003 = false;
                isWindowsXP = false;
                isWindowsVista = false;
                isWindows7 = false;
            } else if (osName.contains("2003")) {
                isWindows95 = false;
                isWindows98 = false;
                isWindowsNT = false;
                isWindows2000 = false;
                isWindows2003 = true;
                isWindowsXP = true;
                isWindowsVista = false;
                isWindows7 = false;
            } else if (osName.contains("XP")) {
                isWindows95 = false;
                isWindows98 = false;
                isWindowsNT = true;
                isWindows2000 = true;
                isWindows2003 = true;
                isWindowsXP = false;
                isWindowsVista = false;
                isWindows7 = false;
            } else if (osName.contains("Vista")) {
                isWindows95 = false;
                isWindows98 = false;
                isWindowsNT = false;
                isWindows2000 = false;
                isWindows2003 = false;
                isWindowsXP = false;
                isWindowsVista = true;
                isWindows7 = false;
            } else if (osName.contains("Windows 7")) {
                isWindows95 = false;
                isWindows98 = false;
                isWindowsNT = false;
                isWindows2000 = false;
                isWindows2003 = false;
                isWindowsXP = false;
                isWindowsVista = false;
                isWindows7 = true;
            } else {
                isWindows95 = false;
                isWindows98 = false;
                isWindowsNT = false;
                isWindows2000 = false;
                isWindows2003 = false;
                isWindowsXP = false;
                isWindowsVista = false;
                isWindows7 = false;
            }
        } else if (osName.contains("Linux")) {
            platform = "linux";
            isWindows = false;
            isLinux = true;
            isUnix = true;
            isMacOSX = false;
            isSolaris = false;
            isWindows95 = false;
            isWindows98 = false;
            isWindowsNT = false;
            isWindows2000 = false;
            isWindows2003 = false;
            isWindowsXP = false;
            isWindowsVista = false;
            isWindows7 = false;
        } else if (osName.contains("Solaris") || osName.contains("SunOS")) {
            platform = "solaris";
            isWindows = false;
            isLinux = false;
            isUnix = true;
            isMacOSX = false;
            isSolaris = true;
            isWindows95 = false;
            isWindows98 = false;
            isWindowsNT = false;
            isWindows2000 = false;
            isWindows2003 = false;
            isWindowsXP = false;
            isWindowsVista = false;
            isWindows7 = false;
        } else if (osName.contains("Mac OS")) {
            platform = "macosx";
            isWindows = false;
            isLinux = false;
            isUnix = true;
            isMacOSX = true;
            isSolaris = false;
            isWindows95 = false;
            isWindows98 = false;
            isWindowsNT = false;
            isWindows2000 = false;
            isWindows2003 = false;
            isWindowsXP = false;
            isWindowsVista = false;
            isWindows7 = false;
        } else {
            platform = "unknown";
            isWindows = false;
            isLinux = false;
            isUnix = false;
            isMacOSX = false;
            isSolaris = false;
            isWindows95 = false;
            isWindows98 = false;
            isWindowsNT = false;
            isWindows2000 = false;
            isWindows2003 = false;
            isWindowsXP = false;
            isWindowsVista = false;
            isWindows7 = false;
        }

        osVersion = System.getProperty("os.version");
        javaVersion = System.getProperty("java.version");
        String version = javaVersion.substring(0, 3);
        isJdk14 = true;
        if (version.equals("1.8")) {
            isJdk18 = true;
            isJdk17 = true;
            isJdk16 = true;
            isJdk15 = true;
        } else if (version.equals("1.7")) {
            isJdk18 = false;
            isJdk17 = true;
            isJdk16 = true;
            isJdk15 = true;
        } else if (version.equals("1.6")) {
            isJdk18 = false;
            isJdk17 = false;
            isJdk16 = true;
            isJdk15 = true;
        } else if (version.equals("1.5")) {
            isJdk18 = false;
            isJdk17 = false;
            isJdk16 = false;
            isJdk15 = true;
        } else {
            isJdk18 = false;
            isJdk17 = false;
            isJdk16 = false;
            isJdk15 = false;
        }

        is64Bit = osArch.contains("64");
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isWindows95() {
        return isWindows95;
    }

    public static boolean isWindows98() {
        return isWindows98;
    }

    public static boolean isWindowsNT() {
        return isWindowsNT;
    }

    public static boolean isWindows2000() {
        return isWindows2000;
    }

    public static boolean isWindows2003() {
        return isWindows2003;
    }

    public static boolean isWindowsXP() {
        return isWindowsXP;
    }

    public static boolean isWindowsVista() {
        return isWindowsVista;
    }

    public static boolean isWindows7() {
        return isWindows7;
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

    public static boolean isJdk14() {
        return isJdk14;
    }

    public static boolean isJdk15() {
        return isJdk15;
    }

    public static boolean isJdk16() {
        return isJdk16;
    }

    public static boolean isJdk17() {
        return isJdk17;
    }

    public static boolean isJdk18() {
        return isJdk18;
    }

    public static String getPlatform() {
        return platform;
    }

    public static boolean getIsWindows() {
        return isWindows;
    }

    public static boolean getIsWindows95() {
        return isWindows95;
    }

    public static boolean getIsWindows98() {
        return isWindows98;
    }

    public static boolean getIsWindowsNT() {
        return isWindowsNT;
    }

    public static boolean getIsWindows2000() {
        return isWindows2000;
    }

    public static boolean getIsWindows2003() {
        return isWindows2003;
    }

    public static boolean getIsWindowsXP() {
        return isWindowsXP;
    }

    public static boolean getIsWindowsVista() {
        return isWindowsVista;
    }

    public static boolean getIsWindows7() {
        return isWindows7;
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

    public static boolean getIsJdk14() {
        return isJdk14;
    }

    public static boolean getIsJdk15() {
        return isJdk15;
    }

    public static boolean getIsJdk16() {
        return isJdk16;
    }

    public static boolean getIsJdk17() {
        return isJdk17;
    }

    public static boolean getIsJdk18() {
        return isJdk18;
    }

    public static MetaClass metaClassOf(Object obj) {
        if (obj == null) return null;
        if (GriffonArtifact.class.isAssignableFrom(obj.getClass())) {
            return ((GriffonArtifact) obj).getGriffonClass().getMetaClass();
        } else if (GroovyObject.class.isAssignableFrom(obj.getClass())) {
            return ((GroovyObject) obj).getMetaClass();
        }
        return GroovySystem.getMetaClassRegistry().getMetaClass(obj.getClass());
    }
}
