/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.lanterna3.support;

import org.codehaus.griffon.runtime.core.properties.AbstractPropertySource;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LanternaAction extends AbstractPropertySource {
    public static final String NAME = "name";

    private Runnable runnable;
    private String name;

    public LanternaAction(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public LanternaAction(Runnable runnable) {
        this("", runnable);
    }

    public LanternaAction(String name) {
        this(name, null);
    }

    public LanternaAction() {
        this("", null);
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
        if (runnable != null) {
            runnable.run();
        }
    }
}
