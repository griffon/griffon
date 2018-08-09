/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.test.javafx;

import griffon.annotations.core.Nonnull;
import griffon.core.ApplicationBootstrapper;
import griffon.javafx.JavaFXGriffonApplication;
import griffon.test.core.TestCaseAware;

import static griffon.test.javafx.TestContext.getTestContext;

/**
 * @author Andres Almiray
 * @since 2.3.0
 */
public class TestJavaFXGriffonApplication extends JavaFXGriffonApplication {
    public TestJavaFXGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public TestJavaFXGriffonApplication(String[] args) {
        super(args);
    }

    @Override
    public void exit() {
        // empty
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    protected ApplicationBootstrapper createApplicationBootstrapper() {
        ApplicationBootstrapper bootstrapper = new TestJavaFXGriffonApplicationBootstrapper(this);
        if (bootstrapper instanceof TestCaseAware) {
            ((TestCaseAware) bootstrapper).setTestCase(getTestContext().getTestCase());
        }
        return bootstrapper;
    }
}