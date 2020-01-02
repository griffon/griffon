/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package org.codehaus.griffon.runtime.javafx

import com.google.inject.AbstractModule
import griffon.core.ExceptionHandler
import griffon.core.ExecutorServiceManager
import griffon.core.GriffonExceptionHandler
import griffon.core.threading.UIThreadManager
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension
import name.falgout.jeffrey.testing.junit.guice.IncludeModule
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.junit.jupiter.api.extension.ExtendWith

import javax.application.threading.ThreadingHandler
import javax.application.threading.tck.ThreadingHandlerTest
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ExecutorService

import static griffon.util.AnnotationUtils.named

@ExtendWith(GuiceExtension)
@IncludeModule(TestModule)
class JavaFXUIThreadManagerTest extends ThreadingHandlerTest {
    static {
        // initialize UI toolkit
        new JFXPanel()
    }

    @Inject private UIThreadManager uiThreadManager

    @Override
    protected ThreadingHandler resolveThreadingHandler() {
        return uiThreadManager
    }

    @Override
    protected boolean isUIThread() {
        return Platform.isFxApplicationThread()
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ExecutorServiceManager)
                .to(DefaultExecutorServiceManager)
                .in(Singleton)

            bind(ExecutorService)
                .annotatedWith(named('defaultExecutorService'))
                .toProvider(DefaultExecutorServiceProvider)
                .in(Singleton)

            bind(UIThreadManager)
                .to(JavaFXUIThreadManager)
                .in(Singleton)

            bind(ExceptionHandler)
                .to(GriffonExceptionHandler)
                .in(Singleton)
        }
    }
}
