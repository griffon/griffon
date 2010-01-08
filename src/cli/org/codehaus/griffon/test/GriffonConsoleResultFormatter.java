/*
 * Copyright 2009-2010 the original author or authors.
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

package org.codehaus.griffon.test;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Andres Almiray
 */
public class GriffonConsoleResultFormatter implements JUnitResultFormatter {
    private PrintStream out;
    private int failureCount;

    public void startTestSuite(JUnitTest test) {
        out.print("Running test " + test.getName() + "...");
        failureCount = 0;
    }

    public void endTestSuite(JUnitTest test) {
        if (failureCount == 0) out.println("PASSED");
    }

    public void setOutput(OutputStream outputStream) {
        if (outputStream instanceof PrintStream) {
            this.out = (PrintStream) outputStream;
        }
        else {
            this.out = new PrintStream(outputStream);
        }
    }

    public void setSystemOutput(String out) {

    }

    public void setSystemError(String err) {

    }

    public void addError(Test test, Throwable throwable) {
        failureCount++;
        if (test instanceof TestCase) {
            printFailedTest((TestCase) test);
        }
    }

    public void addFailure(Test test, AssertionFailedError assertionFailedError) {
        failureCount++;
        if (test instanceof TestCase) {
            printFailedTest((TestCase) test);
        }
    }

    public void endTest(Test test) {
    }

    public void startTest(Test test) {
    }

    private void printFailedTest(TestCase test) {
        if (failureCount == 1) out.println();
        out.println("                    " + test.getName() + "...FAILED");
    }
}
