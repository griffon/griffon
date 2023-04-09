/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.runtime.core

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.annotations.core.Nonnull
import griffon.core.ExceptionHandler
import griffon.core.ExecutorServiceManager
import griffon.core.GriffonApplication
import griffon.core.LifecycleHandler
import griffon.core.threading.ThreadingHandler
import griffon.core.threading.UIThreadManager
import griffon.util.AnnotationUtils
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.codehaus.griffon.runtime.core.threading.UIThreadManagerTestSupport
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class AbstractLifecycleHandlerSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private LifecycleHandler lifecycleHandler

    def 'Query if UI thread'() {
        expect:
        !lifecycleHandler.UIThread
    }

    def 'Execute callable inside UI sync'() {
        given:
        boolean invoked = false

        when:
        invoked = lifecycleHandler.executeInsideUISync({
            true
        } as Callable)

        then:
        invoked
    }

    def 'Execute runnable inside UI sync'() {
        given:
        boolean invoked = false

        when:
        lifecycleHandler.executeInsideUISync {
            invoked = true
        }

        then:
        invoked
    }

    def 'Execute runnable inside UI async'() {
        given:
        boolean invoked = false

        when:
        lifecycleHandler.executeInsideUIAsync {
            invoked = true
        }

        then:
        invoked
    }

    def 'Execute runnable outside UI'() {
        given:
        boolean invoked = false

        when:
        lifecycleHandler.executeOutsideUI() {
            invoked = true
        }

        then:
        invoked
    }

    def 'Execute future'() {
        given:
        boolean invoked = false

        when:
        Future future = lifecycleHandler.executeFuture {
            invoked = true
        }
        future.get()

        then:
        invoked
    }

    def 'Execute future with ExecutorService'() {
        given:
        boolean invoked = false

        when:
        Future future = lifecycleHandler.executeFuture(Executors.newFixedThreadPool(1), {
            invoked = true
        })
        future.get()

        then:
        invoked
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager).to(DefaultExecutorServiceManager).in(Singleton)
            bind(UIThreadManager).to(UIThreadManagerTestSupport).in(Singleton)
            bind(ThreadingHandler).to(TestThreadingHandler).in(Singleton)
            bind(LifecycleHandler).to(TestLifecycleHandler).in(Singleton)
            bind(ExceptionHandler).toProvider(ExceptionHandlerProvider).in(Singleton)
            bind(ExecutorService).annotatedWith(AnnotationUtils.named('defaultExecutorService')).toProvider(DefaultExecutorServiceProvider).in(Singleton)
            bind(GriffonApplication).to(TestGriffonApplication).in(Singleton)
        }
    }

    static class TestLifecycleHandler extends AbstractLifecycleHandler {
        @Inject
        TestLifecycleHandler(@Nonnull GriffonApplication application) {
            super(application)
        }

        @Override
        void execute() {

        }
    }

    static class TestGriffonApplication extends AbstractGriffonApplication {
        @Inject
        private UIThreadManager uiThreadManager

        TestGriffonApplication() {
        }

        TestGriffonApplication(String[] args) {
            super(args)
        }

        @Nonnull
        @Override
        UIThreadManager getUIThreadManager() {
            this.uiThreadManager
        }

        @Override
        Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
            return null
        }
    }
}
