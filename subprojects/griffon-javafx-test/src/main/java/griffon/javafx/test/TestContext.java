/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.javafx.test;

import org.testfx.api.FxRobot;

/**
 * @author Andres Almiray
 * @since 2.3.0
 */
public class TestContext extends FxRobot {
    private static final TestContext INSTANCE;

    static {
        INSTANCE = new TestContext();
    }

    private String windowName;
    private Object testCase;

    private TestContext() {

    }

    public static TestContext getTestContext() {
        return INSTANCE;
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public Object getTestCase() {
        return testCase;
    }

    public void setTestCase(Object testCase) {
        this.testCase = testCase;
    }
}