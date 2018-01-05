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
package org.codehaus.griffon.runtime.core

import org.junit.Test

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class AbstractObservableTest {
    @Test
    void observableSetup() {
        // given:
        MyObservable observable = new MyObservable()
        PropertyChangeListener listener1 = new PropertyChangeListenerWitness()
        PropertyChangeListener listener2 = new PropertyChangeListenerWitness()
        observable.addPropertyChangeListener(listener1)
        observable.addPropertyChangeListener('value2', listener2)

        // when:
        observable.value1 = 'value1'

        // then:
        assert listener1.invoked
        assert !listener2.invoked

        // when:
        listener1.invoked = false
        observable.value2 = 'value2'

        // then:
        assert listener1.invoked
        assert listener2.invoked

        // expect:
        assert observable.getPropertyChangeListeners().size() == 2
        assert !observable.getPropertyChangeListeners('value1')
        assert observable.getPropertyChangeListeners('value2').size() == 1

        // when:
        observable.removePropertyChangeListener(listener1)

        // then:
        assert observable.getPropertyChangeListeners('value2').size() == 1

        // when:
        observable.removePropertyChangeListener('value2', listener2)

        // then:
        assert !observable.getPropertyChangeListeners('value2')
    }
}

class PropertyChangeListenerWitness implements PropertyChangeListener {
    boolean invoked

    @Override
    void propertyChange(PropertyChangeEvent evt) {
        invoked = true
    }
}

class MyObservable extends AbstractObservable {
    private String value1
    private String value2

    String getValue1() {
        this.value1
    }

    void setValue1(String value1) {
        firePropertyChange('value1', this.value1, this.value1 = value1)
    }

    String getValue2() {
        this.value2
    }

    void setValue2(String value2) {
        firePropertyChange(new PropertyChangeEvent(this, 'value2', this.value2, this.value2 = value2))
    }
}
