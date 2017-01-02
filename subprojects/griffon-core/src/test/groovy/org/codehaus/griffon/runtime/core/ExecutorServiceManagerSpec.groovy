/*
 * Copyright 2008-2017 the original author or authors.
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
import com.google.inject.Inject
import griffon.core.ExecutorServiceManager
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Unroll
class ExecutorServiceManagerSpec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private ExecutorServiceManager executorServiceManager

    def 'Excercise API'() {
        given:
        ExecutorService s1 = Executors.newFixedThreadPool(1)
        ExecutorService s2 = Executors.newFixedThreadPool(1)
        ExecutorService s3 = Executors.newFixedThreadPool(1)
        s3.shutdownNow()

        when:
        executorServiceManager.add(null)
        executorServiceManager.add(s1)
        executorServiceManager.add(s2)
        executorServiceManager.add(s3)

        executorServiceManager.remove(s2)

        executorServiceManager.shutdownAll()

        then:
        executorServiceManager.executorServices.size() == 2
        s1.shutdown
        !s2.shutdown
        s3.shutdown
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ExecutorServiceManager)
                .to(DefaultExecutorServiceManager)
        }
    }
}
