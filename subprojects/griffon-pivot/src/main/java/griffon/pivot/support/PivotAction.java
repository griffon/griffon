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
package griffon.pivot.support;

import griffon.core.CallableWithArgs;
import griffon.core.RunnableWithArgs;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Component;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class PivotAction extends Action {
    private static final String ERROR_CALLABLE_NULL = "Argument 'callable' must not be null";
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    private String name;
    private String description;
    private RunnableWithArgs runnable;

    public PivotAction() {

    }

    public PivotAction(@Nonnull RunnableWithArgs runnable) {
        this.runnable = requireNonNull(runnable, ERROR_RUNNABLE_NULL);
    }

    @Deprecated
    public PivotAction(@Nonnull CallableWithArgs<Void> callable) {
        setCallable(callable);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Deprecated
    public void setCallable(@Nonnull final CallableWithArgs<Void> callable) {
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        this.runnable = callable::call;
    }

    public RunnableWithArgs getRunnable() {
        return runnable;
    }

    public void setRunnable(RunnableWithArgs runnable) {
        this.runnable = runnable;
    }

    public String toString() {
        return "Action[" + name + ", " + description + "]";
    }

    @Override
    public void perform(Component component) {
        requireNonNull(runnable, "Argument 'runnable' must not be null for " + this);
        runnable.run(component);
    }
}
