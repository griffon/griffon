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
package org.codehaus.griffon.runtime.core.artifact

import integration.SimpleModel
import integration.TestGriffonApplication
import org.codehaus.griffon.runtime.core.PropertyChangeListenerWitness
import org.codehaus.griffon.runtime.core.VetoableChangeListenerWitness
import org.junit.Test

import java.beans.PropertyChangeListener
import java.beans.VetoableChangeListener

class GriffonModelTest {
    @Test
    void modelAsObservable() {
        // given:
        SimpleModel model = new SimpleModel()
        model.application = new TestGriffonApplication()
        PropertyChangeListener listener1 = new PropertyChangeListenerWitness()
        PropertyChangeListener listener2 = new PropertyChangeListenerWitness()
        model.addPropertyChangeListener(listener1)
        model.addPropertyChangeListener('value2', listener2)

        // when:
        model.value1 = 'value1'

        // then:
        assert listener1.invoked
        assert !listener2.invoked

        // when:
        listener1.invoked = false
        model.value2 = 'value2'

        // then:
        assert listener1.invoked
        assert listener2.invoked

        // expect:
        assert model.getPropertyChangeListeners().size() == 2
        assert !model.getPropertyChangeListeners('value1')
        assert model.getPropertyChangeListeners('value2').size() == 1

        // when:
        model.removePropertyChangeListener(listener1)

        // then:
        assert model.getPropertyChangeListeners('value2').size() == 1

        // when:
        model.removePropertyChangeListener('value2', listener2)

        // then:
        assert !model.getPropertyChangeListeners('value2')
    }

    @Test
    void modelAsVetoable() {
        // given:
        SimpleModel model = new SimpleModel()
        model.application = new TestGriffonApplication()
        VetoableChangeListener vlistener1 = new VetoableChangeListenerWitness()
        VetoableChangeListener vlistener2 = new VetoableChangeListenerWitness()
        PropertyChangeListener plistener1 = new PropertyChangeListenerWitness()
        PropertyChangeListener plistener2 = new PropertyChangeListenerWitness()
        model.addVetoableChangeListener(vlistener1)
        model.addVetoableChangeListener('value2', vlistener2)
        model.addPropertyChangeListener(plistener1)
        model.addPropertyChangeListener('value2', plistener2)

        // when:
        model.value1 = 'value1'

        // then:
        assert vlistener1.invoked
        assert !vlistener2.invoked
        assert plistener1.invoked
        assert !plistener2.invoked

        // when:
        vlistener1.invoked = false
        plistener1.invoked = false
        model.value2 = 'value2'

        // then:
        assert vlistener1.invoked
        assert vlistener2.invoked
        assert plistener1.invoked
        assert plistener2.invoked

        // expect:
        assert model.getVetoableChangeListeners().size() == 2
        assert !model.getVetoableChangeListeners('value1')
        assert model.getVetoableChangeListeners('value2').size() == 1
        assert model.getPropertyChangeListeners().size() == 2
        assert !model.getPropertyChangeListeners('value1')
        assert model.getPropertyChangeListeners('value2').size() == 1

        // when:
        model.removeVetoableChangeListener(vlistener1)
        model.removePropertyChangeListener(plistener1)

        // then:
        assert model.getVetoableChangeListeners('value2').size() == 1
        assert model.getPropertyChangeListeners('value2').size() == 1

        // when:
        model.removeVetoableChangeListener('value2', vlistener2)
        model.removePropertyChangeListener('value2', plistener2)

        // then:
        assert !model.getVetoableChangeListeners('value2')
        assert !model.getPropertyChangeListeners('value2')
    }
}
