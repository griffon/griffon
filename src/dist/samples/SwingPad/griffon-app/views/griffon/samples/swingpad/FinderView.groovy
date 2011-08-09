/*
 * Copyright 2007-2011 the original author or authors.
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

import static griffon.util.GriffonNameUtils.isBlank

actions {
    action(id: 'cancelAction',
       name: app.getMessage('application.action.Cancel.name', 'Cancel'),
       closure: controller.hide,
       mnemonic: app.getMessage('application.action.Cancel.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Cancel.description', 'Cancel')
    )
    action(id: 'previousAction',
       name: app.getMessage('application.action.Previous.name', 'Previous'),
       closure: controller.findPreviousAction,
       mnemonic: app.getMessage('application.action.Previous.mnemonic', 'P'),
       shortDescription: app.getMessage('application.action.Previous.description', 'Previous'),
       enabled: bind {!isBlank(model.toFind)}
    )
    action(id: 'nextAction',
       name: app.getMessage('application.action.Next.name', 'Next'),
       closure: controller.findNextAction,
       mnemonic: app.getMessage('application.action.Next.mnemonic', 'N'),
       shortDescription: app.getMessage('application.action.Next.description', 'Next'),
       enabled: bind {!isBlank(model.toFind)}
    )
    action(id: 'replaceAction',
       name: app.getMessage('application.action.Replace.name', 'Replace'),
       closure: controller.replaceAction,
       mnemonic: app.getMessage('application.action.Replace.mnemonic', 'R'),
       shortDescription: app.getMessage('application.action.Replace.description', 'Replace'),
       enabled: bind {!isBlank(model.replaceWith)}
    )
    action(id: 'replaceAllAction',
       name: app.getMessage('application.action.ReplaceAll.name', 'ReplaceAll'),
       closure: controller.replaceAllAction,
       mnemonic: app.getMessage('application.action.ReplaceAll.mnemonic', 'A'),
       shortDescription: app.getMessage('application.action.ReplaceAll.description', 'ReplaceAll'),
       enabled: bind {!isBlank(model.replaceWith)}
    )
}

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    
    label(app.getMessage('application.dialog.Finder.find', 'Find:'))
    textField(columns: 40, constraints: 'right, wrap',
        text: bind('toFind', source: model, mutual: true))
    label(app.getMessage('application.dialog.Finder.replace', 'Replace:'))
    textField(columns: 40, constraints: 'right, wrap',
        text: bind('replaceWith', source: model, mutual: true))
        
    panel(constraints: 'span 2, grow, wrap') {
        migLayout layoutConstraints: 'fill'
        label(constraints: 'left', text: bind {model.replaced})
        checkBox(label: app.getMessage('application.dialog.Finder.regex', 'Regular expression'),
            constraints: 'right', selected: bind('regex', source: model, mutual: true))
        checkBox(label: app.getMessage('application.dialog.Finder.matchcase', 'Match case'),
            constraints: 'right', selected: bind('matchCase', source: model, mutual: true))
        checkBox(label: app.getMessage('application.dialog.Finder.wholeword', 'Whole word'),
            constraints: 'right', selected: bind('wholeWord', source: model, mutual: true))       
    }

    panel(constraints: 'span 2, grow') {
        migLayout layoutConstraints: 'fill'
        buttonPanel(constraints: 'left') {
            button(replaceAllAction)
            button(replaceAction)
        }
        buttonPanel(constraints: 'right') {
            button(previousAction)
            button(nextAction)
        }
    }
    
    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: cancelAction)
}
