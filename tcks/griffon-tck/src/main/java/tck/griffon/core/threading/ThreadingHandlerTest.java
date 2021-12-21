/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
package tck.griffon.core.threading;

import griffon.core.threading.ThreadingHandler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Andres Almiray
 */
public abstract class ThreadingHandlerTest {
    protected abstract ThreadingHandler resolveThreadingHandler();

    protected abstract boolean isUIThread();

    @Test
    public void verify_executeInsideUIAsync() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        AtomicBoolean threadWitness = new AtomicBoolean();
        AtomicBoolean executeWitness = new AtomicBoolean();
        Runnable task = () -> {
            if (isUIThread()) {
                threadWitness.set(true);
            }
            executeWitness.set(true);
        };

        // when:
        resolveThreadingHandler().executeInsideUIAsync(task);
        await().timeout(2, TimeUnit.SECONDS).until(executeWitness::get, equalTo(true));

        // then:
        assertThat(threadWitness.get(), equalTo(true));
    }

    @Test
    public void verify_executeInsideUISync() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        AtomicBoolean threadWitness = new AtomicBoolean();
        Runnable task = () -> {
            if (isUIThread()) {
                threadWitness.set(true);
            }
        };

        // when:
        resolveThreadingHandler().executeInsideUISync(task);

        // then:
        assertThat(threadWitness.get(), equalTo(true));
    }

    @Test
    public void verify_executeInsideUISync_and_return_value() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        Callable<Boolean> task = this::isUIThread;

        // when:
        Boolean result = resolveThreadingHandler().executeInsideUISync(task);

        // then:
        assertThat(result, equalTo(true));
    }

    @Test
    public void verify_executeOutsideUI() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        AtomicBoolean threadWitness = new AtomicBoolean();
        AtomicBoolean executeWitness = new AtomicBoolean();
        Runnable task = () -> {
            if (!isUIThread()) {
                threadWitness.set(true);
            }
            executeWitness.set(true);
        };

        // when:
        resolveThreadingHandler().executeOutsideUI(task);
        await().timeout(2, TimeUnit.SECONDS).until(executeWitness::get, equalTo(true));

        // then:
        assertThat(threadWitness.get(), equalTo(true));
    }

    @Test
    public void verify_executeOutsideUIAsync() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        AtomicBoolean threadWitness = new AtomicBoolean();
        AtomicBoolean executeWitness = new AtomicBoolean();
        Runnable task = () -> {
            if (!isUIThread()) {
                threadWitness.set(true);
            }
            executeWitness.set(true);
        };

        // when:
        resolveThreadingHandler().executeOutsideUIAsync(task);
        await().timeout(2, TimeUnit.SECONDS).until(executeWitness::get, equalTo(true));

        // then:
        assertThat(threadWitness.get(), equalTo(true));
    }

    @Test
    public void verify_executeOutsideUIAsync_returning_CompletionStage() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        AtomicBoolean executeWitness = new AtomicBoolean();
        Callable<Boolean> task = () -> {
            executeWitness.set(true);
            return !isUIThread();
        };

        // when:
        CompletionStage<Boolean> promise = resolveThreadingHandler().executeOutsideUIAsync(task);
        promise.thenAccept(result -> {
            // then:
            assertThat(result, equalTo(true));
        });
        await().timeout(2, TimeUnit.SECONDS).until(executeWitness::get, equalTo(true));
    }

    @Test
    public void verify_executeInsideUIAsync_returning_CompletionStage() {
        // expect:
        assertThat(isUIThread(), equalTo(false));

        // given:
        AtomicBoolean executeWitness = new AtomicBoolean();
        Callable<Boolean> task = () -> {
            executeWitness.set(true);
            return isUIThread();
        };

        // when:
        CompletionStage<Boolean> promise = resolveThreadingHandler().executeInsideUIAsync(task);
        promise.thenAccept(result -> {
            // then:
            assertThat(result, equalTo(true));
        });
        await().timeout(2, TimeUnit.SECONDS).until(executeWitness::get, equalTo(true));
    }
}
