/*
 * Copyright 2009-2014 the original author or authors.
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

package org.codehaus.griffon.test.report.junit;

import junit.framework.Test;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter;
import org.codehaus.griffon.test.support.TestStacktraceSanitizer;

import java.io.*;

/**
 * JUnit plain text formatter that sanitises the stack traces generated
 * by tests.
 */
public class PlainFormatter extends PlainJUnitResultFormatter {
    protected String name;
    protected File file;

    protected String systemOutput;
    protected String systemError;

    public PlainFormatter(String name, File file) {
        this.name = name;
        this.file = file;
        try {
            super.setOutput(new BufferedOutputStream(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOutput(OutputStream out) {
        throw new IllegalStateException("This should not be called");
    }

    public void setSystemError(String out) {
        systemError = out;
        super.setSystemError(out);
    }

    public void setSystemOutput(String out) {
        systemOutput = out;
        super.setSystemOutput(out);
    }

    public void addFailure(Test test, Throwable throwable) {
        TestStacktraceSanitizer.sanitize(throwable);
        super.addFailure(test, throwable);
    }

    public void addError(Test test, Throwable throwable) {
        TestStacktraceSanitizer.sanitize(throwable);
        super.addError(test, throwable);
    }

    public void endTestSuite(JUnitTest suite) {
        super.endTestSuite(suite);
        File parentFile = file.getParentFile();
        writeToFile(new File(parentFile, name + "-out.txt"), systemOutput);
        writeToFile(new File(parentFile, name + "-err.txt"), systemError);
    }

    protected void writeToFile(File file, String text) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                    //IGNORE
                }
            }
        }
    }
}
