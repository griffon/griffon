package org.codehaus.groovy.griffon.test;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public interface GriffonTestRunner {
    TestResult runTests(TestSuite suite);
}
