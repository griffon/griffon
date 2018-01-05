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
package org.codehaus.griffon.runtime.swing

import com.google.inject.AbstractModule
import griffon.core.ExecutorServiceManager
import griffon.core.threading.UIThreadManager
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.jukito.JukitoRunner
import org.jukito.UseModules
import org.junit.runner.RunWith

import javax.application.threading.ThreadingHandler
import javax.application.threading.ThreadingHandlerTest
import javax.inject.Inject
import javax.inject.Singleton
import javax.swing.SwingUtilities
import java.util.concurrent.ExecutorService

import static griffon.util.AnnotationUtils.named

@RunWith(JukitoRunner)
@UseModules(TestModule)
class SwingUIThreadManagerTest extends ThreadingHandlerTest {
    @Inject private UIThreadManager uiThreadManager

    @Override
    protected ThreadingHandler resolveThreadingHandler() {
        return uiThreadManager
    }

    @Override
    protected boolean isUIThread() {
        return SwingUtilities.isEventDispatchThread()
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
                .to(SwingUIThreadManager)
                .in(Singleton)
        }
    }
}
