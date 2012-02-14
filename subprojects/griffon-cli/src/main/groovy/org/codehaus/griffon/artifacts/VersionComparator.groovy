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

package org.codehaus.griffon.artifacts

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class VersionComparator implements Comparator {
    private final boolean reverse

    VersionComparator(boolean reverse = false) {
        this.reverse = reverse
    }

    int compare(o1, o2) {
        boolean snapshot1 = o1.toString().endsWith('-SNAPSHOT')
        boolean snapshot2 = o2.toString().endsWith('-SNAPSHOT')

        if (reverse) {
            def o = o1
            o1 = o2
            o2 = o
        }

        o1 = o1.toString() - '-SNAPSHOT'
        o2 = o2.toString() - '-SNAPSHOT'

        int result = 0
        if (o1 == '*') {
            result = 1
        } else if (o2 == '*') {
            result = -1
        } else {
            def nums1
            try {
                def tokens = o1.split(/\./)
                tokens = tokens.findAll { it.trim() ==~ /\d+/ }
                nums1 = tokens*.toInteger()
            } catch (NumberFormatException e) {
                throw new InvalidVersionException("Cannot compare versions, left side [$o1] is invalid: ${e.message}")
            }
            def nums2
            try {
                def tokens = o2.split(/\./)
                tokens = tokens.findAll { it.trim() ==~ /\d+/ }
                nums2 = tokens*.toInteger()
            } catch (NumberFormatException e) {
                throw new InvalidVersionException("Cannot compare versions, right side [$o2] is invalid: ${e.message}")
            }
            boolean bigRight = nums2.size() > nums1.size()
            boolean bigLeft = nums1.size() > nums2.size()

            for (i in 0..<nums1.size()) {
                if (nums2.size() > i) {
                    result = nums1[i] <=> nums2[i]
                    if (result != 0) {
                        break
                    }
                    if (i == (nums1.size() - 1) && bigRight) {
                        if (nums2[i + 1] != 0) {
                            result = -1
                            break
                        }
                    }
                } else if (bigLeft) {
                    if (nums1[i] != 0) {
                        result = 1
                        break
                    }
                }
            }
        }

        if (result == 0) {
            if (snapshot1 && !snapshot2) {
                result = 1
            } else if (!snapshot1 && snapshot2) {
                result = -1
            }
        }

        result
    }

    boolean equals(obj) { false }

    int hashCode() { System.identityHashCode(this) }
}
