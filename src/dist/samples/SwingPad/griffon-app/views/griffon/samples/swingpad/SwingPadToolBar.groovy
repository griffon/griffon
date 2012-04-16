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

/**
 * @author Andres Almiray
 */

package griffon.samples.swingpad

import org.fife.ui.rtextarea.RTextArea

wrapToolbarAction = { int actionId, String id, Map params = [:] ->
    bean(new WrappingAction(RTextArea.getAction(actionId), [Action.NAME, Action.SHORT_DESCRIPTION]), id: id)
    noparent {
        params.each { key, value ->
            getVariable(id).putValue(key, value)
        }
    }
    getVariable(id)
}

noparent {
    wrapToolbarAction(RTextArea.UNDO_ACTION, 'undoToolbarAction', [
            (Action.SMALL_ICON): silkIcon('arrow_undo')])
    wrapToolbarAction(RTextArea.REDO_ACTION, 'redoToolbarAction', [
            (Action.SMALL_ICON): silkIcon('arrow_redo')])
}

toolBar(id: 'toolbar', rollover: true) {
    button(newAction, text: null)
    button(openAction, text: null)
    button(saveAction, text: null)
    separator(orientation: SwingConstants.VERTICAL)
    button(undoToolbarAction, text: null)
    button(redoToolbarAction, text: null)
    separator(orientation: SwingConstants.VERTICAL)
    button(cutAction, text: null)
    button(copyAction, text: null)
    button(pasteAction, text: null)
    separator(orientation: SwingConstants.VERTICAL)
    button(runScriptAction, text: null)
    separator(orientation: SwingConstants.VERTICAL)
    button(toggleLayoutAction, text: null)
    button(snapshotAction, text: null)
}
