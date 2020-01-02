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
package griffon.lanterna.support;

import com.googlecode.lanterna.gui.Action;
import org.codehaus.griffon.runtime.core.properties.AbstractPropertySource;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class LanternaAction extends AbstractPropertySource implements Action {
    public static final String NAME = "name";

    private Runnable runnable;
    private Action delegate;
    private String name;

    public enum ResolveStrategy {
        DELEGATE_FIRST,
        RUNNABLE_FIRST,
        DELEGATE_ONLY,
        RUNNABLE_ONLY
    }

    private ResolveStrategy resolveStrategy = ResolveStrategy.DELEGATE_FIRST;

    public ResolveStrategy getResolveStrategy() {
        return resolveStrategy;
    }

    public void setResolveStrategy(ResolveStrategy resolveStrategy) {
        this.resolveStrategy = resolveStrategy != null ? resolveStrategy : ResolveStrategy.DELEGATE_FIRST;
    }

    public LanternaAction() {
    }

    public LanternaAction(String name) {
        this.name = name;
    }

    public LanternaAction(Runnable runnable) {
        this.runnable = runnable;
    }

    public LanternaAction(Action delegate) {
        this.delegate = delegate;
    }

    public Action getDelegate() {
        return delegate;
    }

    public void setDelegate(Action delegate) {
        this.delegate = delegate;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        pcs.firePropertyChange(NAME, this.name, this.name = name);
    }

    public String toString() {
        return name;
    }

    public void doAction() {
        switch (resolveStrategy) {
            case DELEGATE_ONLY:
                if (delegate != null) {
                    delegate.doAction();
                }
                break;
            case DELEGATE_FIRST:
                if (delegate != null) {
                    delegate.doAction();
                } else if (runnable != null) {
                    runnable.run();
                }
                break;
            case RUNNABLE_FIRST:
                if (runnable != null) {
                    runnable.run();
                } else if (delegate != null) {
                    delegate.doAction();
                }
                break;
            case RUNNABLE_ONLY:
                if (runnable != null) {
                    runnable.run();
                }
                break;
        }
    }
}
