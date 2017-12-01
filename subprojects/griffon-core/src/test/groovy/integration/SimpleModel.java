/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package integration;

import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

public class SimpleModel extends AbstractGriffonModel {
    private String value1;
    private String value2;

    public String getValue1() {
        return this.value1;
    }

    public void setValue1(String value1) throws PropertyVetoException {
        fireVetoableChange("value1", this.value1, value1);
        firePropertyChange("value1", this.value1, this.value1 = value1);
    }

    public String getValue2() {
        return this.value2;
    }

    public void setValue2(String value2) throws PropertyVetoException {
        fireVetoableChange(new PropertyChangeEvent(this, "value2", this.value2, value2));
        firePropertyChange(new PropertyChangeEvent(this, "value2", this.value2, this.value2 = value2));
    }
}
