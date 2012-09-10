package org.codehaus.griffon.artifacts

import spock.lang.Specification
import spock.lang.Unroll

class VersionComparatorSpec extends Specification {
    @Unroll("With forward comparison #version1 compareTo #version2 yields #result")
    void testCompareForward() {
        setup:
        VersionComparator comparator = new VersionComparator()

        expect:
        result == comparator.compare(version1, version2)

        where:
        version1       | version2       | result
        '1'            | '1'            | 0
        '1'            | '2'            | -1
        '2'            | '1'            | 1
        '*'            | '1'            | 1
        '1'            | '*'            | -1
        '1.0.0'        | '1.0.0'        | 0
        '1.0.1'        | '1.0.0'        | 1
        '1.0.0'        | '1.0.1'        | -1
        '1.1.0'        | '1.0.0'        | 1
        '1.0.0'        | '1.1.0'        | -1
        '1.0-SNAPSHOT' | '1.0'          | 1
        '1.0'          | '1.0-SNAPSHOT' | -1
        '1.0-SNAPSHOT' | '1.0-SNAPSHOT' | 0
    }

    @Unroll("With reverse comparison #version1 compareTo #version2 yields #result")

    void testCompareReverse() {
        setup:
        VersionComparator comparator = new VersionComparator(true)

        expect:
        result == comparator.compare(version1, version2)

        where:
        version1       | version2       | result
        '1'            | '1'            | 0
        '1'            | '2'            | 1
        '2'            | '1'            | -1
        '*'            | '1'            | -1
        '1'            | '*'            | 1
        '1.0.0'        | '1.0.0'        | 0
        '1.0.1'        | '1.0.0'        | -1
        '1.0.0'        | '1.0.1'        | 1
        '1.1.0'        | '1.0.0'        | -1
        '1.0.0'        | '1.1.0'        | 1
        '1.0-SNAPSHOT' | '1.0'          | 1
        '1.0'          | '1.0-SNAPSHOT' | -1
        '1.0-SNAPSHOT' | '1.0-SNAPSHOT' | 0
    }
}
