/*
 * Copyright 2008-2011 the original author or authors.
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
package griffon.util

import griffon.core.GriffonArtifact

/**
 * Assorted utility methods and constants.
 *
 * @author Andres Almiray
 */
class GriffonApplicationUtils {
    private GriffonApplicationUtils() { }

    static final boolean isWindows
    static final boolean isWindows95
    static final boolean isWindows98
    static final boolean isWindowsNT
    static final boolean isWindows2000
    static final boolean isWindows2003
    static final boolean isWindowsXP
    static final boolean isWindowsVista
    static final boolean isWindows7

    /** True if running Linux, Solaris or MacOSX  */
    static final boolean isUnix

    static final boolean isLinux
    static final boolean isSolaris
    static final boolean isMacOSX

    static final String osArch
    static final String osName
    static final String osVersion
    static final String javaVersion
    static final boolean is64Bit

    static final boolean isJdk14
    static final boolean isJdk15
    static final boolean isJdk16
    static final boolean isJdk17

    static final String platform

    static {
        osArch = System.getProperty('os.arch')
        osName = System.getProperty('os.name')

        switch (osName) {
            case ~/Windows.*/:
                isWindows = true
                isWindows95 = osName ==~ /95/
                isWindows98 = osName ==~ /98/
                isWindowsNT = isWindows2000 = (osName =~ /XP/) || (osName =~ /NT/)
                isWindows2003 = osName ==~ /2003/
                isWindowsXP = (osName =~ /XP/) || (osName =~ /2003/)
                isWindowsVista = osName ==~ /Vista/
                isWindows7 = osName ==~ /Windows 7/
                platform = 'windows'
                break
            case ~/Linux.*/:
                isUnix = true
                isLinux = true
                platform = 'linux'
                break
            case ~/Solaris.*/:
            case ~/SunOS.*/:
                isUnix = true
                isSolaris = true
                platform = 'solaris'
                break
            case ~/Mac OS.*/:
                isUnix = true
                isMacOSX = true
                platform = 'macosx'
        }

        osVersion = System.getProperty('os.version')
        javaVersion = System.getProperty('java.version')
        switch (new BigDecimal(javaVersion[0..2])) {
            case {it >= 1.7}: isJdk17 = true
            case {it >= 1.6}: isJdk16 = true
            case {it >= 1.5}: isJdk15 = true
            case {it >= 1.4}: isJdk14 = true
        }

        is64Bit = osArch.contains('64')
    }

    static MetaClass metaClassOf(obj) {
        if (obj == null) return null
        switch (obj.getClass()) {
            case GriffonArtifact:
                return obj.getGriffonClass().getMetaClass()
            case GroovyObject:
                return obj.getMetaClass()
        }
        return GroovySystem.getMetaClassRegistry().getMetaClass(obj.getClass())
    }
}
