/*
 * Copyright 2007-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package griffon.samples.groovyfxpad

import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.AbstractAction
import javax.swing.Action

/**
 * @author Andres Almiray
 */
class WrappingAction extends AbstractAction {
    private final Action delegateAction
    
    WrappingAction(final Action delegateAction, final List<String> skip = []) {
        this.delegateAction = delegateAction
        copyProperty(Action.NAME)
        copyProperty(Action.MNEMONIC_KEY)
        copyProperty(Action.ACCELERATOR_KEY)
        copyProperty(Action.LARGE_ICON_KEY)
        copyProperty(Action.SMALL_ICON)
        copyProperty(Action.LONG_DESCRIPTION)
        copyProperty(Action.SHORT_DESCRIPTION)
        delegateAction.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(!skip.contains(event.propertyName)) putValue(event.propertyName, event.newValue)
            }
        })
    }

    private void copyProperty(String key) {
        putValue(key, delegateAction.getValue(key))
    }

    void actionPerformed(ActionEvent event) {
        this.delegateAction.actionPerformed(event)
    }
}
