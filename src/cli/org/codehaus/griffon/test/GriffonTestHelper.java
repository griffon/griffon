package org.codehaus.groovy.griffon.test;

import junit.framework.TestSuite;
import java.util.List;

public interface GriffonTestHelper {
    TestSuite createTests(List<String> testNames, String type);
}
