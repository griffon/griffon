/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.runtime.javafx;

import griffon.core.ApplicationBootstrapper;
import griffon.core.test.TestCaseAware;
import griffon.javafx.JavaFXGriffonApplication;

import javax.annotation.Nonnull;

import static griffon.javafx.test.TestContext.getTestContext;

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