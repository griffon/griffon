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

/**
 * @author Andres Almiray
 */
public class PotentialTest {
    private String classPattern;
    private String methodName;
    private String filePattern;

    public PotentialTest(String testPattern) {
        if (containsMethodName(testPattern)) {
            // Filter out the method name
            int pos = testPattern.lastIndexOf('.');
            this.methodName = testPattern.substring(pos + 1);
            this.classPattern = testPattern.substring(0, pos);
        }
        else {
            this.classPattern = testPattern;
        }

        this.filePattern = classPatternToFilePattern(this.classPattern);
    }

    public PotentialTest(String classPattern, String methodName) {
        this.classPattern = classPattern;
        this.methodName = methodName;
        this.filePattern = classPatternToFilePattern(classPattern);
    }

    public String getClassPattern() {
        return classPattern;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFilePattern() {
        return filePattern;
    }

    boolean hasMethodName() {
        return this.methodName != null;
    }

    String classPatternToFilePattern(String classPattern) {
        if (classPattern.indexOf('.') != -1) {
            return classPattern.replace('.', '/');
        }
        else {
            // Allow the test class to be in any package.
            return "**/" + classPattern;
        }
    }

    boolean containsMethodName(String testPattern) {
        // Probably we should check for method names starting with "test"
        // return Character.isLowerCase(testPattern.charAt(testPattern.lastIndexOf('.') + 1));
        return testPattern.substring(testPattern.lastIndexOf('.')+1).startsWith("test");
    }
}
