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
package org.codehaus.griffon.runtime.pivot;

import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.core.test.TestCaseAware;
import griffon.pivot.DesktopPivotGriffonApplication;
import org.apache.pivot.wtk.Display;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TestDesktopPivotApplication extends DesktopPivotGriffonApplication {
    private static Object testCase;
    private static GriffonApplication application;
    private static CountDownLatch latch;
    private static CountDownLatch readyLatch;

    @Nonnull
    public static GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    public static CountDownLatch getLatch() {
        return latch;
    }

    @Nonnull
    public static CountDownLatch getReadyLatch() {
        return readyLatch;
    }

    public static void init(Object testCase) {
        TestDesktopPivotApplication.testCase = testCase;
        latch = new CountDownLatch(1);
        readyLatch = new CountDownLatch(1);
    }

    public TestDesktopPivotApplication() {
    }

    public TestDesktopPivotApplication(@Nonnull String[] args) {
        super(args);
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    protected ApplicationBootstrapper createApplicationBootstrapper(@Nonnull Display display) {
        ApplicationBootstrapper bootstrapper = new TestPivotApplicationBootstrapper(this, display);
        if (bootstrapper instanceof TestCaseAware) {
            ((TestCaseAware) bootstrapper).setTestCase(testCase);
        }
        return bootstrapper;
    }

    @Override
    protected void afterStartup() {
        application = this;
        application.initialize();
        latch.countDown();
    }

    @Override
    public void ready() {
        super.ready();
        readyLatch.countDown();
    }

    @Override
    public void exit() {
        // empty
    }
}
