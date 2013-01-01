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
 * WITHOUT c;pWARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util

import static griffon.util.GriffonApplicationUtils.*

/**
 * @author Andres Almiray
 */
class PlatformUtils {
    private static String platform = null

    private PlatformUtils() {}

    static final PLATFORMS = [
        windows: [
            nativelib: '.dll',
            webstartName: 'Windows',
            archs: ['x86']],
        linux: [
            nativelib: '.so',
            webstartName: 'Linux',
            archs: ['i386', 'x86']],
        macosx: [
            nativelib: '.jnilib',
            webstartName: 'Mac OS X',
            archs: ['i386', 'ppc']],
        solaris: [
            nativelib: '.so',
            webstartName: 'SunOS',
            archs: ['x86', 'sparc', 'sparcv9']],
        windows64: [
            nativelib: '.dll',
            webstartName: 'Windows',
            archs: ['amd64', 'x86_64']],
        linux64: [
            nativelib: '.so',
            webstartName: 'Linux',
            archs: ['amd64', 'x86_64']],
        macosx64: [
            nativelib: '.jnilib',
            webstartName: 'Mac OS X',
            archs: ['x86_64']],
        solaris64: [
            nativelib: '.so',
            webstartName: 'SunOS',
            archs: ['amd64', 'x86_64']]
    ]

    static String getPlatform() {
        if(!platform) {
            platform = 'windows'
            if(isSolaris) platform = 'solaris'
            else if(isLinux) platform = 'linux'
            else if(isMacOSX) platform = 'macosx'
            if(is64Bit) platform += '64'
        }
        platform
    }

    static void doForAllPlatforms(Closure callback) {
        PLATFORMS.each { platformKey, platformValue -> callback(platformKey, platformValue) }
    }

    static isCompatible(platforms) {
        platforms.any{ it == platform || (is64Bit && it == (platform[0..-3])) }
    }

    static doForCurrentPlatform(Closure callback) {
        def plf = platform
        callback(plf, PLATFORMS[plf])
    }
}
