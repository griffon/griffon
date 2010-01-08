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

import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

import java.io.*;

/**
 * @author Andres Almiray
 */
public class FormattedOutput {
    private File file;
    private JUnitResultFormatter formatter;
    private OutputStream output;

    public FormattedOutput(File file, JUnitResultFormatter formatter) {
        this.file = file;
        this.formatter = formatter;
    }

    public File getFile() {
        return file;
    }

    public JUnitResultFormatter getFormatter() {
        return formatter;
    }

    public void start(JUnitTest test) {
        try {
            output = new BufferedOutputStream(new FileOutputStream(file));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        formatter.setOutput(output);
        formatter.startTestSuite(test);
    }

    public void end(JUnitTest test, String out, String err) {
        // Blatant hack for "plain" formatter.
        if (file.getName().endsWith(".txt")) {
            String baseName = file.getName().substring(0, file.getName().length() - 4);
            writeToFile(new File(file.getParentFile(), baseName + "-out.txt"), out);
            writeToFile(new File(file.getParentFile(), baseName + "-err.txt"), err);
        }

        formatter.setSystemOutput(out);
        formatter.setSystemError(err);
        formatter.endTestSuite(test);

        if (output != null) {
            try { output.close(); } catch (IOException ex) {}
        }
    }

    /**
     * Writes a string of text to a file, creating the file if it doesn't
     * exist or overwriting the existing contents if it does.
     * @param file The file to write to.
     * @param text The text to write into the file.
     */
    private void writeToFile(File file, String text) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(text);
            writer.close();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ex) {}
            }
        }
    }
}
