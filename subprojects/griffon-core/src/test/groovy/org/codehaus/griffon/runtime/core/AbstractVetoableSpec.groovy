/*
 * Copyright 2008-2014 the original author or authors.
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
import java.beans.PropertyVetoException
import java.beans.VetoableChangeListener

class AbstractVetoableSpec {
    @Test
    void vetoableSetup() {
        // given:
        MyVetoable vetoable = new MyVetoable()
        VetoableChangeListener vlistener1 = new VetoableChangeListenerWitness()
        VetoableChangeListener vlistener2 = new VetoableChangeListenerWitness()
        PropertyChangeListener plistener1 = new PropertyChangeListenerWitness()
        PropertyChangeListener plistener2 = new PropertyChangeListenerWitness()
        vetoable.addVetoableChangeListener(vlistener1)
        vetoable.addVetoableChangeListener('value2', vlistener2)
        vetoable.addPropertyChangeListener(plistener1)
        vetoable.addPropertyChangeListener('value2', plistener2)

        // when:
        vetoable.value1 = 'value1'

        // then:
        assert vlistener1.invoked
        assert !vlistener2.invoked
        assert plistener1.invoked
        assert !plistener2.invoked

        // when:
        vlistener1.invoked = false
        plistener1.invoked = false
        vetoable.value2 = 'value2'

        // then:
        assert vlistener1.invoked
        assert vlistener2.invoked
        assert plistener1.invoked
        assert plistener2.invoked

        // expect:
        assert vetoable.getVetoableChangeListeners().size() == 2
        assert !vetoable.getVetoableChangeListeners('value1')
        assert vetoable.getVetoableChangeListeners('value2').size() == 1
        assert vetoable.getPropertyChangeListeners().size() == 2
        assert !vetoable.getPropertyChangeListeners('value1')
        assert vetoable.getPropertyChangeListeners('value2').size() == 1

        // when:
        vetoable.removeVetoableChangeListener(vlistener1)
        vetoable.removePropertyChangeListener(plistener1)

        // then:
        assert vetoable.getVetoableChangeListeners('value2').size() == 1
        assert vetoable.getPropertyChangeListeners('value2').size() == 1

        // when:
        vetoable.removeVetoableChangeListener('value2', vlistener2)
        vetoable.removePropertyChangeListener('value2', plistener2)

        // then:
        assert !vetoable.getVetoableChangeListeners('value2')
        assert !vetoable.getPropertyChangeListeners('value2')
    }
}

class VetoableChangeListenerWitness implements VetoableChangeListener {
    boolean invoked

    @Override
    void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        invoked = true
    }
}

class MyVetoable extends AbstractVetoable {
    private String value1
    private String value2

    String getValue1() {
        this.value1
    }

    void setValue1(String value1) throws PropertyVetoException {
        fireVetoableChange('value1', this.value1, value1)
        firePropertyChange('value1', this.value1, this.value1 = value1)
    }

    String getValue2() {
        this.value2
    }

    void setValue2(String value2) throws PropertyVetoException {
        fireVetoableChange(new PropertyChangeEvent(this, 'value2', this.value2, value2))
        firePropertyChange(new PropertyChangeEvent(this, 'value2', this.value2, this.value2 = value2))
    }
}


